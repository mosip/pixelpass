package io.mosip.pixelpass.common

import java.util.Base64

actual  fun decodeFromBase64UrlFormat(content: String): ByteArray {
       return  Base64.getUrlDecoder().decode(content.toByteArray())
}
