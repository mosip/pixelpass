package io.mosip.pixelpass.shared

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun Int.isNegative(): Boolean {
    return this < 0
}