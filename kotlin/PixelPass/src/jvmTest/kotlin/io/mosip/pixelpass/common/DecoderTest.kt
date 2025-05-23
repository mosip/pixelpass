package io.mosip.pixelpass.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import kotlin.test.Test


class DecoderTest {


    @Test
    fun `should decode the base64 url encoded content successfully in Java environment`() {
        val decodedContent = decodeFromBase64UrlFormat("aGVsbG8gd29ybGQ=")

        assertEquals("hello world", decodedContent.toString(Charsets.UTF_8))
    }

    @Test
    fun `should throw error when given base64 url encoded data contains non base64 character in Java environment`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            decodeFromBase64UrlFormat("aGVsbG8%d29ybGQ=")
        }

        assertEquals(
            "Illegal base64 character 25",
            exception.message
        )
    }

    @Test
    fun `should throw error when given base64 url encoded data has truncated bytes in Java environment`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            decodeFromBase64UrlFormat("aGVsbG8gd29ybG=")
        }

        assertEquals(
            "Input byte array has wrong 4-byte ending unit",
            exception.message
        )
    }

}
