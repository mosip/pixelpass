package io.mosip.pixelpass

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.CborEncoder
import co.nstant.`in`.cbor.model.DataItem
import io.mosip.pixelpass.cbor.Utils
import io.mosip.pixelpass.common.Encoder
import io.mosip.pixelpass.shared.DEFAULT_ZIP_FILE_NAME
import io.mosip.pixelpass.shared.QR_BORDER
import io.mosip.pixelpass.shared.QR_SCALE
import io.mosip.pixelpass.shared.ZIP_HEADER
import io.mosip.pixelpass.shared.decodeHex
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.zlib.ZLib
import io.nayuki.qrcodegen.QrCode
import nl.minvws.encoding.Base45
import org.json.JSONArray
import org.json.JSONObject
import org.zeroturnaround.zip.ZipUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Objects

class PixelPass {
    fun toJson(base64UrlEncodedCborEncodedString: String): Any {
        val decodedData: ByteArray =
            Encoder().decodeFromBase64UrlFormatEncoded(base64UrlEncodedCborEncodedString)
        val cbor: DataItem? =
            CborDecoder(ByteArrayInputStream(decodedData)).decode()[0]
        return Utils().toJson(cbor!!)
    }

    fun generateQRCode(data: String, ecc: ECC = ECC.L, header: String = ""): Bitmap {
        val dataWithHeader = generateQRData(data, header).toByteArray()
        val qrcode = QrCode.encodeText(String(dataWithHeader), ecc.mEcc)
        return toBitmap(qrcode)
    }

    fun decode(data: String): String {
        val decodedBase45Data = Base45.getDecoder().decode(data)
        val decompressedData = ZLib().decode(decodedBase45Data)
        return try {
            val cborDecodedData = CborDecoder(ByteArrayInputStream(decompressedData)).decode()[0]

            val json = Utils().toJson(cborDecodedData)
            if (json.toString().startsWith('[') && json.toString().endsWith(']'))
                ( json as JSONArray ).toString().replace("\\","")
            else
                ( json as JSONObject ).toString().replace("\\","")
        }catch (_: Exception){
            String(decompressedData)
        }
    }

    fun decodeBinary(data: ByteArray): String {
        if (String(data).startsWith(ZIP_HEADER)) {
            var tempFile: File? = null
            try {
                tempFile = File.createTempFile("temp", ".zip")
                tempFile.writeBytes(data)
                if (ZipUtil.containsEntry(tempFile, DEFAULT_ZIP_FILE_NAME))
                    return String(ZipUtil.unpackEntry(tempFile, DEFAULT_ZIP_FILE_NAME))
            } catch (e: Exception) {
                throw e
            } finally {
                tempFile?.delete()
            }
        }
        throw UnknownBinaryFileTypeException();
    }

     fun generateQRData(
        data: String,
        header: String = ""
    ): String {
         val parsedData: Any?
         var compressedData = byteArrayOf()
         var b45EncodedData = ""
         try {
             if (data.startsWith('[') && data.endsWith(']')) {
                 parsedData = JSONArray(data)
             }
             else {
                 parsedData = JSONObject(data)
             }
             val toDataItem = Utils().toDataItem(parsedData)

             val cborByteArrayOutputStream = ByteArrayOutputStream()
             CborEncoder(cborByteArrayOutputStream).nonCanonical().encode(toDataItem)
             compressedData = ZLib().encode(cborByteArrayOutputStream.toByteArray())

         }catch (e: Exception){
             Log.e("PixelPass",e.toString())
             compressedData = ZLib().encode(data.toByteArray())
         }finally {
             b45EncodedData = String(Base45.getEncoder().encode(compressedData))
         }

        return (header + b45EncodedData)
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun getMappedData(jsonData: JSONObject, mapper: Map<String,String>, cborEnable: Boolean = false): String {

        val mappedJson = JSONObject()
        val iterator = jsonData.keys().iterator()
        while (iterator.hasNext()){
            val next = iterator.next()
            val key = mapper[next] ?: next
            val value = jsonData.get(next)
            mappedJson.put(key,value)
        }

        val payload = Utils().toDataItem(mappedJson)

        if (cborEnable) {
            val cborByteArrayOutputStream = ByteArrayOutputStream()
            CborEncoder(cborByteArrayOutputStream).encode(payload)
            return cborByteArrayOutputStream.toByteArray().toHexString()
        }
        return payload.toString()
    }

    fun decodeMappedData(data: String, mapper: Map<String,String>): String {
        var jsonData: JSONObject
        try {
            val cborDecodedData = CborDecoder(ByteArrayInputStream(data.decodeHex())).decode()[0]
            jsonData =  (Utils().toJson(cborDecodedData) as JSONObject)
        }catch (_: Exception){
            jsonData = JSONObject(data)
        }

        val payload = JSONObject()
        val iterator = jsonData.keys().iterator()
        while (iterator.hasNext()){
            val next = iterator.next()
            val key = mapper[next] ?: next
            val value = jsonData.get(next)
            payload.put(key,value)
        }
        return payload.toString()
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