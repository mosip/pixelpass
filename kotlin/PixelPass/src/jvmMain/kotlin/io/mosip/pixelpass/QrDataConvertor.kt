package io.mosip.pixelpass

import io.mosip.pixelpass.types.ECC
import io.nayuki.qrcodegen.QrCode
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.logging.Logger
import javax.imageio.ImageIO

private val logger = Logger.getLogger("QrDataConvertor")
actual fun convertQrDataIntoBase64(dataWithHeader: String, ecc: ECC): String {
    try {
        val qrcode = QrCode.encodeText(dataWithHeader, ecc.mEcc)
        val bufferedImage = toBufferedImage(qrcode, 650)
        return encodeToString(bufferedImage, "png").orEmpty()

    }
    catch (e: Exception){
        logger.severe("Error occurred while converting Qr Data to Base64 String::$e")
        e.printStackTrace()
        return ""
    }
}

private fun toBufferedImage(qrCode: QrCode, targetSize: Int): BufferedImage {
    val scaleFactor = targetSize.toFloat() / qrCode.size.toFloat()
    val image = BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until targetSize) {
        for (x in 0 until targetSize) {
            val qrX = (x / scaleFactor).toInt()
            val qrY = (y / scaleFactor).toInt()

            val color = if (qrCode.getModule(qrX, qrY)) 0x000000 else 0xFFFFFF
            image.setRGB(x, y, color)
        }
    }

    return image
}


private fun encodeToString(image: BufferedImage?, type: String?): String? {
    var imageString: String? = null
    val bos = ByteArrayOutputStream()

    try {
        ImageIO.write(image, type, bos)
        val imageBytes = bos.toByteArray()
        val encoder = Base64.getEncoder()
        imageString = encoder.encodeToString(imageBytes)
        bos.close()
    } catch (e: Exception) {
        logger.severe("Error occurred while Encoding to Base64 String::$e")
        e.printStackTrace()
    }
    return imageString
}