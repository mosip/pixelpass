# PixelPass

## Features

- Create QR Code for given data
- Uses zlib compression and base45 encoding
- Decode QR data encoded by PixelPass

## Installation 
`npm i @mosip/pixelpass`

[npm](https://www.npmjs.com/package/@mosip/pixelpass)

## APIs
`generateQRData( data, header )`

`data` - Data needs to be compressed and encoded

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

returns a zlib compressed and base45 encoded string with header prepended if provided.

`generateQRCode( data, ecc , header )`

`data` - Data needs to be compressed and encoded

`ecc` - Error Correction Level for the QR generated. defaults to `"L"`

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

returns a base64 encoded PNG image with header prepended if provided.

`decode(data)`

`data` - Data needs to be decoded and decompressed without header

returns a base45 decoded and zlib decompressed string

## Errors / Exceptions
`Cannot read properties of null (reading 'length')` - thrown when the string passed to encode is null.
`Cannot read properties of undefined (reading 'length')` - thrown when the string passed to encode is undefined.
`byteArrayArg is null or undefined.` -  thrown when the string passed to encode is null or undefined.
`utf8StringArg is null or undefined.` - thrown when the string passed to decode is null or undefined.
`utf8StringArg has incorrect length.` - thrown when the string passed to decode is of invalid length.
`Invalid character at position X.` - thrown when the string passed to decode is invalid with an unknown character then base45 character set. Also denotes the invalid character position.
`incorrect data check` - thrown when the string passed to decode is invalid.

## License
MIT
