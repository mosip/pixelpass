package io.mosip.pixelpass.common

import android.util.Base64

actual  fun decodeFromBase64UrlFormat(content: String): ByteArray {
     return Base64.decode(content, Base64.DEFAULT or Base64.URL_SAFE)
}
