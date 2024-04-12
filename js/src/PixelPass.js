import {
    COLOR_BLACK, COLOR_WHITE,
    DEFAULT_ECC_LEVEL,
    DEFAULT_QR_BORDER,
    DEFAULT_QR_QUALITY,
    DEFAULT_QR_SCALE, DEFAULT_ZLIB_COMPRESSION_LEVEL
} from "./shared/Constants";

const b45 = require("base45-web");
const pako = require('pako');
import QRCode from 'qrcode';


export function decodeData(base45Data) {
    const decodedBase45Data = b45.decode(base45Data);
    const compressedData = pako.inflate(decodedBase45Data);
    return new TextDecoder().decode(compressedData);
}

export function generateQRData(data, header= "" ) {
    const compressedData= pako.deflate(data , {level : DEFAULT_ZLIB_COMPRESSION_LEVEL})
    return  header + b45.encode(compressedData).toString()
}

export async function generateQRBase64Image(data, ecc = DEFAULT_ECC_LEVEL, header="" ) {
    const base45Data = generateQRData(data,header)
    const opts = {
        errorCorrectionLevel: ecc ,
        quality: DEFAULT_QR_QUALITY,
        margin: DEFAULT_QR_BORDER,
        scale: DEFAULT_QR_SCALE,
        color: {
            dark:COLOR_BLACK,
            light:COLOR_WHITE
        }
    }
    return QRCode.toDataURL(base45Data, opts);
}
