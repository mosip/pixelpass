package io.mosip.pixelpass.common

import android.os.Build
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EncoderTest {

    @Before
    fun setUp() {
        mockkObject(BuildConfig)
    }

    @Test
    fun `should decode the given byteArray from base64 url formal with API greater than or equal to Version O`() {
        every { BuildConfig.getVersionSDKInt() } returns Build.VERSION_CODES.O
        val base64UrlEncodedData = "aGVsbG8gd29ybGQ="

        val decodedData: ByteArray =
            Encoder().decodeFromBase64UrlFormatEncoded(base64UrlEncodedData)

        assertEquals("hello world", decodedData.toString(Charsets.UTF_8))
    }

    @Test
    fun `should decode the given byteArray from base64 url formal with API lesser than  Version O`() {
        every { BuildConfig.getVersionSDKInt() } returns Build.VERSION_CODES.N
        mockkStatic(android.util.Base64::class)
        every {
            android.util.Base64.decode(
                any<String>(),
                android.util.Base64.DEFAULT
            )
        } returns "hello world".toByteArray()
        val base64UrlEncodedData = "aGVsbG8gd29ybGQ="

        val decodedData: ByteArray =
            Encoder().decodeFromBase64UrlFormatEncoded(base64UrlEncodedData)

        assertEquals("hello world", decodedData.toString(Charsets.UTF_8))
    }
}