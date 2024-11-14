package io.mosip.pixelpass.common

import org.junit.Assert.assertEquals
import org.junit.Test

class EncoderTest {

    @Test
    fun `should decode the given byteArray from base64 url`() {
        val base64UrlEncodedData = "aGVsbG8gd29ybGQ="

        val decodedData: ByteArray =
            Encoder.decodeFromBase64UrlFormatEncoded(base64UrlEncodedData)

        assertEquals("hello world", decodedData.toString(Charsets.UTF_8))
    }

}