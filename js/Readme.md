# PixelPass

Pixelpass is a library which can do multiple things which are listed below,

- Given a data → `generateQRCode` → returns a QR Code.

- Given a JSON String → `generateQRData` → Gives back CBOR encoded data.

- Given a CBOR encoded data → `decode` → Gives back JSON String.

- Given a JSON and Mapper → `getMappedCborData` → Gives back CBOR encoded data.

- Given a CBOR encoded data and Mapper → `decodeMappedCborData` → Gives back a JSON.

## Features

- Compresses the data using zlib compression of level 9.

- Encodes/ Decodes the data using base45.

- When the Data is JSON, it does the CBOR encode/decode to reduce size further.

- When JSON and a Mapper is given, it maps the JSON with Mapper and then does the CBOR encode/decode which further reduces the size of the data.

## Installation 
`npm i @mosip/pixelpass`

[npm](https://www.npmjs.com/package/@mosip/pixelpass)

## Example
Prerequisites
* [nodejs](https://nodejs.org/en/learn/getting-started/how-to-install-nodejs)
* [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

To run the example app copy the below command and paste it to your terminal.

```
git clone https://github.com/mosip/pixelpass.git && cd pixelpass && git checkout develop && cd js && npm i && cd example && npm i && npm start
```

## APIs
### generateQRCode( data, ecc , header )

 - `data` - Data needs to be compressed and encoded.

 - `ecc` - Error Correction Level for the QR generated. defaults to `"L"`.

 - `header` - Data header need to be prepend to identify the encoded data. defaults to `""`.

```javascript
import { generateQRCode } from '@mosip/pixelpass';

const data = "Hello";
const qrCode = generateQRCode(data, ecc, header);

// ecc is Error Correction Level for the QR generated. defaults to "L".
// header defaults to empty string if not passed.
```
The `generateQRCode` takes a data, ECC (Error correction level) which when not passed defaults to L and header which defaults to empty string if not passed.
Returns a base64 encoded PNG image.

### generateQRData( data, header )

- `data` - Data needs to be compressed and encoded.

- `header` - Data header need to be prepend to identify the encoded data. defaults to `""`.

```javascript
import { generateQRData } from '@mosip/pixelpass';

const jsonString = "{\"name\":\"Steve\",\"id\":\"1\",\"l_name\":\"jobs\"}";
const header = "jsonstring";

const encodedCBORData = generateQRData(jsonString, header);

// header defaults to empty string if not passed.
```
The `generateQRData` takes a valid JSON string and a header which when not passed defaults to an empty string.
This API will return a base45 encoded string which is `Compressed > CBOR Encoded > Base45 Encoded`.


### decode( data )

- `data` - Data needs to be decoded and decompressed without header.

```javascript
import { decode } from '@mosip/pixelpass';

const encodedData = "NCFWTL$PPB$PN$AWGAE%5UW5A%ADFAHR9 IE:GG6ZJJCL2.AJKAMHA100+8S.1";
const jsonString = decode(encodedData);
```
The `decode` will take a base45 encoded string  as parameter and gives us decoded JSON string which is Base45 `Decoded > CBOR Decoded > Decompressed`.

### getMappedCborData( jsonData, mapper );

- `jsonData` - A JSON data.
- `mapper` - A Map which is used to map with the JSON.

```javascript
import { getMappedCborData } from '@mosip/pixelpass';

const jsonData = {"name": "Jhon", "id": "207", "l_name": "Honay"};
const mapper = {"id": "1", "name": "2", "l_name": "3"};

const byteBuffer = getMappedCborData(jsonData, mapper);

const cborEncodedString = byteBuffer.toString('hex');
```
The `getMappedCborData` takes 2 arguments a JSON and a map with which we will be creating a new map with keys and values mapped based on the mapper.
The example of a converted map would look like, `{ "1": "207", "2": "Jhon", "3": "Honay"}`

### decodeMappedCborData( cborEncodedString, mapper )

- `cborEncodedString` - A CBOREncoded string
- `mapper` - A Map which is used to map with the JSON.

```javascript
import { decodeMappedCborData } from '@mosip/pixelpass';

const cborEncodedString = "a302644a686f6e01633230370365486f6e6179";
const mapper = {"1": "id", "2": "name", "3": "l_name"};

const jsonData = decodeMappedCborData(cborEncodedString, mapper);
```

The `decodeMappedCborData` takes 2 arguments a string which is CBOR Encoded and a map with which we will be creating a JSON by mapping the keys and values.
The example of the returned JSON would look like, `{"name": "Jhon", "id": "207", "l_name": "Honay"}`


## Errors / Exceptions
- `Cannot read properties of null (reading 'length')` - thrown when the string passed to encode is null.

- `Cannot read properties of undefined (reading 'length')` - thrown when the string passed to encode is undefined.

- `byteArrayArg is null or undefined.` -  thrown when the string passed to encode is null or undefined.

- `utf8StringArg is null or undefined.` - thrown when the string passed to decode is null or undefined.

- `utf8StringArg has incorrect length.` - thrown when the string passed to decode is of invalid length.

- `Invalid character at position X.` - thrown when the string passed to decode is invalid with an unknown character then base45 character set. Also denotes the invalid character position.

- `incorrect data check` - thrown when the string passed to decode is invalid.


## License
MIT
