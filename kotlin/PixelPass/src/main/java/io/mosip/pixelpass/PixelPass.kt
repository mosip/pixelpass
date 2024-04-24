package io.mosip.pixelpass

import android.graphics.Bitmap
import android.graphics.Color
import co.nstant.`in`.cbor.CborDecoder
import io.mosip.pixelpass.cbor.Utils
import io.mosip.pixelpass.shared.QR_BORDER
import io.mosip.pixelpass.shared.QR_SCALE
import io.mosip.pixelpass.shared.decodeHex
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.zlib.ZLib
import io.nayuki.qrcodegen.QrCode
import nl.minvws.encoding.Base45
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.util.Objects

class PixelPass {
    fun generateQRCode(data: String, ecc: ECC = ECC.M, header: String = ""): Bitmap {
        val dataWithHeader = getDataBytes(data, header)
        val qrcode = QrCode.encodeText(String(dataWithHeader), ecc.mEcc)
        return toBitmap(qrcode)
    }

    fun decode(data: String): String {
        val decodedBase45Data = Base45.getDecoder().decode(data)
        val decompressedData = String(ZLib().decode(decodedBase45Data))
        return try {
            val cborDecodedData = CborDecoder(ByteArrayInputStream(decompressedData.decodeHex())).decode()[0]
            (Utils().toJson(cborDecodedData) as JSONObject ).toString().replace("\\","")
        }catch (_: Exception){
            decompressedData
        }
    }

    private fun getDataBytes(
        data: String,
        header: String
    ): ByteArray {
        val compressedData = ZLib().encode(data.toByteArray())
        val base45Data = String(Base45.getEncoder().encode(compressedData))
        return (header + base45Data).toByteArray()
    }
    private fun toBitmap(qrCode: QrCode): Bitmap {
        Objects.requireNonNull(qrCode)
        require(!(QR_SCALE <= 0 || QR_BORDER < 0)) { "Value out of range" }
        require(!(QR_BORDER > Int.MAX_VALUE / 2 || qrCode.size + QR_BORDER * 2L > Int.MAX_VALUE / QR_SCALE)) { "Scale or border too large" }
        val result = Bitmap.createBitmap(
            (qrCode.size + QR_BORDER * 2) * QR_SCALE,
            (qrCode.size + QR_BORDER * 2) * QR_SCALE,
            Bitmap.Config.ARGB_8888
        )
        for (y in 0 until result.getHeight()) {
            for (x in 0 until result.getWidth()) {
                val color = qrCode.getModule(x / QR_SCALE - QR_BORDER, y / QR_SCALE - QR_BORDER)
                result.setPixel(x, y, if (color) Color.BLACK else Color.WHITE)
            }
        }
        return result
    }
}