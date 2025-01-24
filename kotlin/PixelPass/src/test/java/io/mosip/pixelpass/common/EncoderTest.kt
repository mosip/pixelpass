package io.mosip.pixelpass.common

import android.os.Build
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class EncoderTest {
    @Before
    fun setUp() {
        mockkObject(Util)
        every { Util.isAndroid() } returns false
        mockkObject(BuildConfig)

        mockkStatic(android.util.Base64::class)

    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `should decode the base64 url encoded content successfully in Java environment`() {
        val decodedContent = Encoder.decodeFromBase64UrlFormatEncoded("aGVsbG8gd29ybGQ=")

        assertEquals("hello world", decodedContent.toString(Charsets.UTF_8))
    }

    @Test
    fun `should throw error when given base64 url encoded data contains non base64 character in Java environment`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Encoder.decodeFromBase64UrlFormatEncoded("aGVsbG8%d29ybGQ=")
        }

        assertEquals(
            "Illegal base64 character 25",
            exception.message
        )
    }

    @Test
    fun `should throw error when given base64 url encoded data has truncated bytes in Java environment`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Encoder.decodeFromBase64UrlFormatEncoded("aGVsbG8gd29ybG=")
        }

        assertEquals(
            "Input byte array has wrong 4-byte ending unit",
            exception.message
        )
    }


    @Test
    fun `should decode the base64 url encoded content successfully with API greater than or equal to Version O in Android environment`() {
        every { Util.isAndroid() } returns true
        every { BuildConfig.getVersionSDKInt() } returns Build.VERSION_CODES.O

        val decodedData: ByteArray = Encoder.decodeFromBase64UrlFormatEncoded("aGVsbG8gd29ybGQ")

        assertTrue("hello world".toByteArray().contentEquals(decodedData))
    }

    @Test
    fun `should decode the base64 url encoded content successfully with API lesser than  Version O in Android environment`() {
        every { Util.isAndroid() } returns true
        every { BuildConfig.getVersionSDKInt() } returns Build.VERSION_CODES.N
        every {
            android.util.Base64.decode(
                "aGVsbG8gd29ybGQ=",
                android.util.Base64.DEFAULT
            )
        } returns "hello world".toByteArray()

        val decodedData: ByteArray =
            Encoder.decodeFromBase64UrlFormatEncoded("aGVsbG8gd29ybGQ")

        assertEquals("hello world", decodedData.toString(Charsets.UTF_8))
    }

}