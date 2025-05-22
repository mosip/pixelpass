package io.mosip.pixelpass


import COSE.OneKey
import co.nstant.`in`.cbor.CborDecoder
import co.nstant.`in`.cbor.CborEncoder
import co.nstant.`in`.cbor.model.DataItem
import io.mosip.pixelpass.cbor.Utils
import io.mosip.pixelpass.common.decodeFromBase64UrlFormat
import io.mosip.pixelpass.exception.InvalidSignatureException
import io.mosip.pixelpass.exception.UnknownBinaryFileTypeException
import io.mosip.pixelpass.cose.CWT
import io.mosip.pixelpass.cose.CwtCryptoCtx
import io.mosip.pixelpass.shared.DEFAULT_ZIP_FILE_NAME
import io.mosip.pixelpass.cose.KeyUtil
import io.mosip.pixelpass.cose.Util
import io.mosip.pixelpass.shared.ZIP_HEADER
import io.mosip.pixelpass.shared.decodeHex
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.zlib.ZLib
import nl.minvws.encoding.Base45
import org.json.JSONArray
import org.json.JSONObject
import org.zeroturnaround.zip.ZipUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.logging.Logger

class PixelPass {
    private val logger = Logger.getLogger(PixelPass::class.java.name)
    fun toJson(base64UrlEncodedCborEncodedString: String): Any {
        val decodedData: ByteArray =
            decodeFromBase64UrlFormat(base64UrlEncodedCborEncodedString)
        val cbor: DataItem? =
            CborDecoder(ByteArrayInputStream(decodedData)).decode()[0]
        return Utils().toJson(cbor!!)
    }

    fun generateQRCode(data: String, ecc: ECC = ECC.L, header: String = ""): String {
        val dataWithHeader = generateQRData(data, header)
        val qrcodeImage = convertQRDataIntoBase64(dataWithHeader, ecc)
        return qrcodeImage
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
             logger.severe("Error occurred while converting Qr Data to Base64 String::$e")
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
            val value = jsonData.get(next.toString())
            mappedJson.put(key.toString(),value)
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
            val value = jsonData.get(next.toString())
            payload.put(key.toString(),value)
        }
        return payload.toString()
    }


    fun decodeCWT(
        cwt: String,
        publicKeyString: String,
        mapper: Map<String, String>,
        philSysPrefix: ArrayList<String>,
        algorithm: String
    ): String {

        val isPhilSysData = Util().isPhilSysQRData(cwt, philSysPrefix)
        val splittedData: String = cwt.substring(cwt.indexOf(":") + 1)
        val base45DecodedData = Base45.getDecoder().decode(splittedData)
        val oneKey = KeyUtil.oneKeyFromPublicKey(publicKeyString, algorithm, isPhilSysData)
        return verifyAndDecode(base45DecodedData, oneKey, mapper)
    }

    private fun verifyAndDecode(rawCbor: ByteArray, oneKey: OneKey, mapper: Map<String, String>): String {
        try {
            val ctx: CwtCryptoCtx = CwtCryptoCtx.sign1Verify(oneKey.PublicKey())
            val cwt = CWT.processCOSE(rawCbor, ctx)
            val cborObject = cwt.getClaim(169.toShort())
            val jsonObject = JSONObject(cborObject.ToJSONString())
            return getMappedData(jsonObject, mapper, false)
        } catch (e: java.lang.Exception) {
            throw InvalidSignatureException("Signature is invalid : $e")
        }
    }



}
