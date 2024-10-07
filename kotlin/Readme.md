# PixelPass

## Features

- Create QR Code for given data
- Uses zlib compression and base45 encoding
- Decode QR data encoded by PixelPass

## Installation

[Maven](https://oss.sonatype.org/service/local/repositories/releases/content/io/mosip/pixelpass/maven-metadata.xml)

## APIs

`generateQRCode( data, ecc, header )`

`data` - Data needs to be compressed and encoded

`ecc` - Error Correction Level for the QR generated. defaults to `"L"`

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

returns a Bitmap image with header prepended if provided.

`generateQRData( data, header )`

`data` - Data needs to be compressed and encoded

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

returns compressed and encoded data for qrcode with header prepended if provided.

`decode(data)`

`data` - Data needs to be decoded and decompressed without header

returns a base45 decoded and zlib decompressed string

`decodeBinary(data)`

`data` - Data needs to be decompressed without header. Should be sent as a ByteArray. Currently only zip binary data is only supported.

returns a unzipped string


`getMappedData( jsonData, mapper, cborEnable )`

- `jsonData` - A JSON data. Which is a JSONObject.
- `mapper` - A Map which is used to map with the JSON. Which is a Map<String,String>.
- `cborEnable` - A Boolean which is used to enable or disable CBOR encoding on mapped data. Defaults to `false` if not provided.

return a hex string which is a CBOR encoded JSON with given mapper if `cborEnable` is set to true. Or returns a JSON remapped string.

`decodeMappedData( data, mapper )`

- `data` - A CBOR Encoded string or JSON string which needs to be re mapped.
- `mapper` - A Map which is used to map with the JSON.Which is a Map<String,String>

return a JSONObject which mapped with given mapper.

## License
MPL-2.0