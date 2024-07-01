# PixelPass

## Features

- Create QR Code for given data
- Uses zlib compression and base45 encoding
- Decode QR data encoded by PixelPass

## Installation

todo :: add maven link

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


`getMappedCborData( jsonData, mapper )`

- `jsonData` - A JSON data. Which is a JSONObject.
- `mapper` - A Map which is used to map with the JSON. Which is a Map<String,String>.

return a hex string which is a CBOR encoded JSON with given mapper.

`decodeMappedCborData( cborEncodedString, mapper )`

- `cborEncodedString` - A CBOR Encoded string
- `mapper` - A Map which is used to map with the JSON.Which is a Map<String,String>

return a JSONObject which mapped with given mapper.

## License
MIT