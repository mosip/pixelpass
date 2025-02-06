package io.mosip.pixelpass.cose

class Util {
    fun isPhilSysQRData(data: String, prefixArray: ArrayList<String>): Boolean{
        val prefix = data.substring(0,data.indexOf(":"))
        return prefixArray.contains(prefix)

    }
}