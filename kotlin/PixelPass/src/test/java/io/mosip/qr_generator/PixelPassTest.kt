package io.mosip.qr_generator

import android.graphics.Bitmap
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import io.mosip.pixelpass.PixelPass
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.zlib.ZLib
import org.junit.After
import org.junit.Assert
import org.junit.Test

class   PixelPassTest {

    @After
    fun after() {
        clearAllMocks()
    }

    @Test
    fun `should return bitmap QR for given data`() {
        mockkConstructor(ZLib::class)
        mockkStatic(Bitmap::class)

        val mockedEncoded = byteArrayOf(1, 2, 3, 4)
        val expected = mockk<Bitmap>()
        every { anyConstructed<ZLib>().encode(any(), any()) } returns mockedEncoded
        every { Bitmap.createBitmap(any(), any(), any()) } returns expected
        every { expected.height } returns 10
        every { expected.width } returns 10
        every { expected.setPixel(any(), any(), any()) } just runs


        val data = "test"
        val actual = PixelPass().generateQRCode(data, ECC.M, "")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data`() {
        val data = "NCFKVPV0QSIP600GP5L0"
        val expected = "hello"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }
    @Test
    fun `should return decoded data for given QR data in cbor`() {
        val data = "NCFHPE/Q6:96+963Y6:96P563H0 %2DH0"
        val expected = "{\"temp\":15}"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }
}