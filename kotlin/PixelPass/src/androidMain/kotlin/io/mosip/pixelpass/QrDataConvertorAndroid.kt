package io.mosip.pixelpass

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import io.mosip.pixelpass.shared.QR_BORDER
import io.mosip.pixelpass.shared.QR_SCALE
import io.mosip.pixelpass.types.ECC
import io.nayuki.qrcodegen.QrCode
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.logging.Logger

private val logger = Logger.getLogger("QrDataConvertor")
actual fun convertQRDataIntoBase64(dataWithHeader: String, ecc: ECC): String {
    try {
        val qrcode = QrCode.encodeText(dataWithHeader, ecc.mEcc)
        val bitMap =  toBitmap(qrcode)
        return encodeToString(bitMap).orEmpty()

    } catch (e: Exception){
        logger.severe("Error occurred while converting Qr Data to Base64 String::+$e")
        return ""
    }
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

fun encodeToString(image: Bitmap): String? {
    val imageString: String?
    val bos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.PNG, 100, bos)
    val imageBytes = bos.toByteArray()
    imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
    bos.close()
    return imageString
}
