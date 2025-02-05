package io.mosip.pixelpass.common

import android.util.Base64

actual  fun decodeFromBase64UrlFormatEncoded(content: String): ByteArray {
     var base64: String = content.replace('-', '+').replace('_', '/')
     when (base64.length % 4) {
         2 -> base64 += "=="
         3 -> base64 += "="
         else -> {}
     }

     return Base64.decode(base64, Base64.DEFAULT)
}