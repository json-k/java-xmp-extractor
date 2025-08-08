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

Example output - `XMPMap` converted to JSON for display:

```Javascript
{
  "namespaces" : [ {
    "prefix" : "dc",
    "uri" : "http://purl.org/dc/elements/1.1/"
  }, {
    "prefix" : "photoshop",
    "uri" : "http://ns.adobe.com/photoshop/1.0/"
  }, {
    "prefix" : "xmp",
    "uri" : "http://ns.adobe.com/xap/1.0/"
  }, {
    "prefix" : "exif",
    "uri" : "http://ns.adobe.com/exif/1.0/"
  }, {
    "prefix" : "xmpRights",
    "uri" : "http://ns.adobe.com/xap/1.0/rights/"
  }, {
    "prefix" : "tiff",
    "uri" : "http://ns.adobe.com/tiff/1.0/"
  }, {
    "prefix" : "xmpMM",
    "uri" : "http://ns.adobe.com/xap/1.0/mm/"
  } ],
  "properties" : {
    "exif:GainControl" : "2",
    "xmp:CreatorTool" : "Adobe Photoshop CS3 Macintosh",
    "exif:SensingMethod" : "2",
    "photoshop:ICCProfile" : "sRGB IEC61966-2.1",
    "exif:PixelYDimension" : "2787",
    "photoshop:Urgency" : "9",
    "dc:format" : "image/jpeg",
    "tiff:ResolutionUnit" : "2",
    "tiff:Make" : "NIKON CORPORATION",
    "exif:WhiteBalance" : "0",
    "tiff:YCbCrPositioning" : "2",
    "photoshop:Credit" : "David Ausserhofer",
    "photoshop:CaptionWriter" : "David Ausserhofer",
    "dc:title" : [ "Frevert_8703" ],
    "xmp:CreateDate" : "2009-07-14T15:28:06Z",
    "xmp:ModifyDate" : "2009-07-26T00:25:21Z",
    "photoshop:History" : "",
    "exif:GPSVersionID" : "2.2.0.0",
    "photoshop:City" : "Berlin",
    "exif:FNumber" : "63/10",
    "exif:SubjectDistanceRange" : "0",
    "tiff:Orientation" : "1",
    "exif:FocalLength" : "1050/10",
    "xmpRights:Marked" : "True",
    "exif:NativeDigest" : "36864,40960,40961,37121,37122,40962,40963,37510,40964,36867,36868,33434,33437,34850,34852,34855,34856,37377,37378,37379,37380,37381,37382,37383,37384,37385,37386,37396,41483,41484,41486,41487,41488,41492,41493,41495,41728,41729,41730,41985,41986,41987,41988,41989,41990,41991,41992,41993,41994,41995,41996,42016,0,2,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,20,22,23,24,25,26,27,28,30;9FD2914E5888D6A7643B847BBFA61C5D",
    "exif:FlashpixVersion" : "0100",
    "exif:ExposureBiasValue" : "0/6",
    "tiff:YResolution" : "3000000/10000",
    "dc:description" : [ "Max-Planck-Institut für Bildungsforschung , für Petra Fox-Kuchenbecker, 14. Juli 2009 Foto David Ausserhofer" ],
    "exif:ExifVersion" : "0221",
    "dc:creator" : [ "David Ausserhofer" ],
    "exif:MaxApertureValue" : "31/10",
    "tiff:NativeDigest" : "256,257,258,259,262,274,277,284,530,531,282,283,296,301,318,319,529,532,306,270,271,272,305,315,33432;90DC18E36BEE9B0A909DA7C32A0755B4",
    "dc:rights" : [ "David Ausserhofer" ],
    "exif:FileSource" : "3",
    "exif:Flash" : {
      "exif:Fired" : "False",
      "exif:Return" : "0",
      "exif:Mode" : "0",
      "exif:Function" : "False",
      "exif:RedEyeMode" : "False"
    },
    "exif:ExposureTime" : "10/1000",
    "exif:SceneType" : "1",
    "exif:ComponentsConfiguration" : [ "1", "2", "3", "0" ],
    "exif:ColorSpace" : "1",
    "exif:DigitalZoomRatio" : "1/1",
    "photoshop:DateCreated" : "2009-07-14",
    "exif:PixelXDimension" : "4204",
    "exif:ExposureProgram" : "1",
    "exif:Contrast" : "0",
    "exif:ISOSpeedRatings" : [ "2500" ],
    "exif:DateTime" : "2009-07-26T00:25:21Z",
    "exif:MeteringMode" : "5",
    "exif:CustomRendered" : "0",
    "exif:DateTimeDigitized" : "2009-07-14T15:28:06Z",
    "xmp:MetadataDate" : "2009-07-26T10:54:13Z",
    "exif:SceneCaptureType" : "0",
    "exif:ExposureMode" : "1",
    "photoshop:Headline" : "Max-Planck-Institut für Bildungsforschung",
    "dc:subject" : [ "Max-Planck-Institut für Bildungsforschung MPIB" ],
    "tiff:XResolution" : "3000000/10000",
    "exif:DateTimeOriginal" : "2009-07-14T15:28:06Z",
    "exif:LightSource" : "0",
    "exif:Saturation" : "0",
    "exif:Sharpness" : "0",
    "tiff:Model" : "NIKON D3",
    "photoshop:ColorMode" : "3",
    "exif:CompressedBitsPerPixel" : "4/1",
    "photoshop:Country" : "Deutschland",
    "xmpMM:InstanceID" : "uuid:7A4F6124B37ADE119D879F9342011130",
    "xmpRights:WebStatement" : "www.ausserhofer.de",
    "exif:FocalLengthIn35mmFilm" : "105"
  }
}
```


## TODO

Unit tests (always with the unit tests).