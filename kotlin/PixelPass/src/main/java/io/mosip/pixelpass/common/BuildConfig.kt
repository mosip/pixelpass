package io.mosip.pixelpass.common

import android.os.Build

object BuildConfig {
    fun getVersionSDKInt(): Int {
        return Build.VERSION.SDK_INT
    }
}