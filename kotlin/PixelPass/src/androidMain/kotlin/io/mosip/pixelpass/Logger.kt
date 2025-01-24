package io.mosip.pixelpass

import android.util.Log

actual fun logMessage( message: String, tag: String){
    Log.d(tag, message)
}