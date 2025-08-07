# Java XMP Extractor

## The Why

I've been working with files in ecommerce, Premedia, and the printing industry for 20+ years. I've probably tried hundreds of libraries for extracting metadata. I've attached 10's of those files to single projects to clean up, quantize, normalize, and finally straighten out managing of metadata (once and for all). I've built 10's of those projects (for the last time).

This really is the last time.

If you want the metadata in the file put it in an XMP packet—if the format doesn't support XMP pick a container than does—if there isn't a namespace or property that seems to fix you data make a custom one and store it appropriately (in the packet).

Thats it.

## The How

Read a stream a byte at a time until we find an XMP packet header,read the packet, parse it with the Adobe XMP SDK for Java, and create a JSON object (with terrible property names) that keep references to the namespace.

Each of the top level properties becomes a property in the output map.

## Use

Reading from a file:

```Java
    try (InputStream is = new FileInputStream(file)) {
        Optional<XMPMap> map = XMPExtractor.extractXMPMap(is);
        print(map.isPresent()?map.get():"[NO PACKET]");
    } catch (FileNotFoundException e) {
        logger.log(Level.SEVERE, null, e);
    } catch (IOException e) {
        logger.log(Level.SEVERE, null, e);
    } catch (XMPParseException e) {
        logger.log(Level.SEVERE, null, e);
    }
```

## TODO

Unit tests (always with the unit tests).