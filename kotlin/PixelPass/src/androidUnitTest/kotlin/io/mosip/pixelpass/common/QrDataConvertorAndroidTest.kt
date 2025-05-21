package io.mosip.pixelpass.common

import io.mosip.pixelpass.convertQrDataIntoBase64
import java.io.ByteArrayOutputStream

import android.graphics.Bitmap
import android.util.Base64
import io.mosip.pixelpass.types.ECC
import io.mockk.*
import io.nayuki.qrcodegen.QrCode
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals


class QrDataConvertorAndroidTest {

    @Before
    fun setUp() {
        mockkStatic(Bitmap::class)
        mockkStatic(Base64::class)
    }

    @Test
    fun `test convertQrDataIntoBase64 with valid data`() {
        val mockBitmap = mockk<Bitmap>()
        val encodedString = "mockedBase64String"

        every { Bitmap.createBitmap(any(), any(), any()) } returns mockBitmap
        every { mockBitmap.compress(Bitmap.CompressFormat.PNG, 100, any()) } answers {
            val outputStream = arg<ByteArrayOutputStream>(2)
            outputStream.write("mockedImageBytes".toByteArray())
            true
        }
        every { mockBitmap.height } returns 100
        every { mockBitmap.width } returns 100
        every { mockBitmap.setPixel(any(), any(), any()) } just Runs

        every { Base64.encodeToString(any(), Base64.NO_WRAP) } returns encodedString

        val data = "Test QR Data"
        val header = "Header"
        val result = convertQrDataIntoBase64("$header$data", ECC.L)

        assertEquals(encodedString, result, "The Base64 encoded string should match the mocked value")
    }

    @Test
    fun `test convertQrDataIntoBase64 with exception`() {
        mockkStatic("io.nayuki.qrcodegen.QrCode")
        every { QrCode.encodeText(any(), any()) } throws RuntimeException("Mocked exception")

        val data = "Test QR Data"
        val header = "Header"
        val result = convertQrDataIntoBase64("$header$data", ECC.L)

        assertEquals("", result, "The result should be an empty string when an exception occurs")
    }
}
