# PixelPass

## Features

- Create QR Code for given data
- Uses zlib compression and base45 encoding
- Decode QR data encoded by PixelPass

## Installation

todo :: add maven link

## APIs

`generateQRBase64Image( data, ecc, header )`

`data` - Data needs to be compressed and encoded

`ecc` - Error Correction Level for the QR generated. defaults to `"M"`

`header` - Data header need to be prepend to identify the encoded data. defaults to `""`

returns a Bitmap image with header prepended if provided.

`decode(data)`

`data` - Data needs to be decoded and decompressed without header

returns a base45 decoded and zlib decompressed string

## License
MIT