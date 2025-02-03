package io.mosip.pixelpass.common

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DecoderKMPTest {

    @Before
    fun setUp() {
        mockkStatic(Base64::class)
    }

    @Test
    fun `should decode base64 url encoded content successfully`() {
        val encodedContent = "aGVsbG8gd29ybGQ="
        val expectedDecodedContent = "hello world"
        every { Base64.decode(encodedContent, Base64.DEFAULT) } returns expectedDecodedContent.toByteArray()

        val decodedContent = decodeFromBase64UrlFormatEncoded(encodedContent)

        assertEquals(expectedDecodedContent, decodedContent.toString(Charsets.UTF_8))
    }


    @Test
    fun `should throw error when given base64 url encoded data contains non base64 character`() {
        val encodedContent = "aGVsbG8%d29ybGQ="
        every { Base64.decode(encodedContent, Base64.DEFAULT) } throws IllegalArgumentException("Illegal base64 character 25")

        val exception = assertFailsWith<IllegalArgumentException> {
            decodeFromBase64UrlFormatEncoded(encodedContent)
        }

        assertEquals("Illegal base64 character 25", exception.message)
    }
}
