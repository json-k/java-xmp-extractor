package org.keeber.imaging.xmp;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.keeber.imaging.xmp.XMPExtractor.XMPParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {
    static final Logger logger = Logger.getGlobal();
    private static ObjectMapper JSON = new ObjectMapper();

    private static void print(Object o){
        try {
            System.out.println(o instanceof String?(String) o:JSON.writerWithDefaultPrettyPrinter().writeValueAsString(o));
        } catch (JsonProcessingException e) {
            logger.log(Level.INFO,"Could not log:",o);
        }
    }

    public static void main(String... args) {
            Arrays.stream(new File("./samples/").listFiles(file -> file.getName().endsWith(".jpg"))).forEach((File file) -> {
                try (InputStream is = new FileInputStream(file)) {
                    print(file.getName());
                    Optional<XMPMap> map = XMPExtractor.extractXMPMap(is);
                    print(map.isPresent()?map.get():"[NO PACKET]");
                    if (map.isPresent()) {
                        OutputStream os = new FileOutputStream("./build/"+file.getName()+".json");
                        os.write(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(map.get()).getBytes(StandardCharsets.UTF_8));
                        os.close();
                    }
                } catch (FileNotFoundException e) {
                    logger.log(Level.SEVERE, null, e);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, null, e);
                } catch (XMPParseException e) {
                    logger.log(Level.SEVERE, null, e);
                }
            });


    }

}
