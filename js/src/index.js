const {
    DEFAULT_QR_QUALITY,
    DEFAULT_QR_BORDER,
    DEFAULT_QR_SCALE,
    COLOR_BLACK,
    COLOR_WHITE,
    DEFAULT_ZLIB_COMPRESSION_LEVEL,
    DEFAULT_ECC_LEVEL
} = require('./shared/Constants')
const QRCode = require('qrcode')
const b45 = require("base45-web");
const pako = require('pako');
const cbor = require('cbor-web');

function generateQRData(data, header = "") {
    const compressedData = pako.deflate(data, {level: DEFAULT_ZLIB_COMPRESSION_LEVEL})
    return header + b45.encode(compressedData).toString()
}

async function generateQRCode(data, ecc = DEFAULT_ECC_LEVEL, header = "") {
    const base45Data = generateQRData(data, header)
    const opts = {
        errorCorrectionLevel: ecc,
        quality: DEFAULT_QR_QUALITY,
        margin: DEFAULT_QR_BORDER,
        scale: DEFAULT_QR_SCALE,
        color: {
            dark: COLOR_BLACK,
            light: COLOR_WHITE
        }
    }
    return QRCode.toDataURL(base45Data, opts);
}

function decode(data) {
    const decodedBase45Data = b45.decode(data);
    const compressedData = pako.inflate(decodedBase45Data);
    const textData = new TextDecoder().decode(compressedData);
    try {
        const decodedCBORData = cbor.decode(textData);
        if (decodedCBORData) return JSON.stringify(decodedCBORData)
        return textData
    } catch (e) {
        return textData
    }
}

module.exports = {
    generateQRData,
    generateQRCode,
    decode
}
