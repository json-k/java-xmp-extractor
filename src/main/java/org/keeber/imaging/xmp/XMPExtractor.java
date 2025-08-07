package org.keeber.imaging.xmp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keeber.imaging.xmp.XMPMap.XMPNamespace;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class XMPExtractor {

    /*
     * Packet Scanning
     */
    static byte[] PACKET_B = "<?xpacket begin=".getBytes(StandardCharsets.US_ASCII);    // Packet begin // eg: <?xpacket begin="?" id="W5M0MpCehiHzreSzNTczkc9d"?>
    static byte[] PACKET_T = "?>".getBytes(StandardCharsets.US_ASCII);                  // Packet terminator
    static byte[] PACKET_E = "<?xpacket end=".getBytes(StandardCharsets.US_ASCII);      // Packet end // eg: <?xpacket end="w"?>

    /**
     * Brute force extraction of an XMP packet from an input stream - that is presumed to contain an XMP packet.
     * 
     * NOTE: so we don't read the actual packet opening 'tag' but we do read the trailing one. So they aren't actually tags in the XML 
     * sense and the Adobe SDK will work just fine.
     * 
     * @param is an input stream to search (it will not be closed)
     * @return the string content of the XMP packet - if it was found
     * @throws IOException
     */
    private static Optional<String> getPacket(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int b, s = 0;boolean writing = false,reading = true;
        byte[] p = PACKET_B;
        while ((b = is.read()) >= 0 && reading) {
            if (writing) { os.write(b); }
            if (b == p[s]) {
                s++;
                if (s == p.length) {
                    s = 0;
                    if (p == PACKET_B) {
                        p = PACKET_T;
                    } else if (p == PACKET_T && writing) {
                        reading = false;   
                    } else if (p == PACKET_T && !writing) {
                        p = PACKET_E;
                        writing = true;
                    } else if (p == PACKET_E) {
                        p = PACKET_T;
                    }
                }
            } else {
                s = 0;
            }
        }
        return os.size() > 0 ? 
            Optional.of(os.toString(StandardCharsets.UTF_8)):
            Optional.empty();
    }

    private static boolean isInteger(String n) { return n != null && n.matches("^[0-9]+$"); }
    private static ObjectMapper JSON = new ObjectMapper();
    
    /**
     * Scans the provided input stream for an XMP packet (`<?xpacket begin=...`), extracts the XML (if found), creates 
     * an Adobe SDK metadata object, then converts to JSON, and an XMPMap instance.
     * 
     * @param is in Inputstream that is presumed to have XMP data (it will not be closed).
     * @return an XMPMap Optional is a map was found otherwise an empty reference.
     * @throws IOException
     * @throws XMPParseException
     */
    public static Optional<XMPMap> extractXMPMap(InputStream is) throws IOException, XMPParseException {
        Optional<String> packet = getPacket(is);
        if (!packet.isPresent()) { // No packet was found
            return Optional.empty();
        }
        XMPMap xmp = new XMPMap();
        try {
            XMPIterator xmpi = XMPMetaFactory.parseFromString(packet.get()).iterator(
                new IteratorOptions().setJustLeafnodes(true).setOmitQualifiers(true)
            ); 
            XMPPropertyInfo i;JsonPointer pointer;ObjectNode a = JSON.createObjectNode();
            while ( xmpi.hasNext() ) {
                i = (XMPPropertyInfo) xmpi.next();
                if ( i.getPath() == null ) continue;
                if ( i.getNamespace() != null ) {
                    //Namespaces are tracked via their URI and this is a set ie: it will not add duplicates.
                    xmp.getNamespaces().add(new XMPNamespace(i.getPath().split(":")[0], i.getNamespace()));
                }
                // Convert xpath into a JSON pointer (basically)
                // I was tempted to organize the output by top level namespace BUT the information is already in the output ie: you can do it yourself.
                // And it is actually possible to have an object from one namespace as part of a structure of another so to try and oganize them is 
                // pointless.
                //
                pointer = JsonPointer.compile("/"+Arrays.stream(i.getPath().replaceAll("\\[([0-9]+)\\](\\/|$)", "/$1/").split("/"))
                    .map((String p) -> isInteger(p)?Integer.toString(Integer.parseInt(p) - 1):p)
                    .collect(Collectors.joining("/")));
                if (isInteger(pointer.last().getMatchingProperty())) {
                    a.withArray(pointer.head()).insert(pointer.last().getMatchingIndex(),i.getValue());
                } else {
                    a.withObject(pointer.head()).put(pointer.last().getMatchingProperty(), i.getValue());
                }
            }
            a.propertyStream().forEach( (Map.Entry<String,JsonNode> e) -> xmp.getProperties().put(e.getKey(), e.getValue()));
        } catch (XMPException e) {
            throw new XMPParseException("Error parsing XMP",e);
        }
        return Optional.of(xmp);
    }

    public static class XMPParseException extends Exception {

        XMPParseException(String message, Exception wrapped) {
            super(message,wrapped);
        }

    }

}
