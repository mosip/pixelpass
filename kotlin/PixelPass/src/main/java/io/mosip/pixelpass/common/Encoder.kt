package io.mosip.pixelpass.common

import org.apache.commons.codec.binary.Base64

object Encoder {
    fun decodeFromBase64UrlFormatEncoded(content: String): ByteArray {
        return Base64.decodeBase64(content)
    }
}