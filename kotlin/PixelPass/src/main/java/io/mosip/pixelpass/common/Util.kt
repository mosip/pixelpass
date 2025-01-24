package io.mosip.pixelpass.common

object Util {
    fun isAndroid():Boolean {
        return System.getProperty("java.vm.name")?.contains("Dalvik") ?: false
    }
}