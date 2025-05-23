package io.mosip.pixelpass

import io.mockk.every
import io.mockk.mockkStatic
import io.mosip.pixelpass.types.ECC
import io.nayuki.qrcodegen.QrCode
import kotlin.test.Test
import kotlin.test.assertEquals

class QrDataConvertorJvmTest {

    @Test
    fun `test convertQrDataIntoBase64 with valid data`() {
        val data = "Test QR Data"
        val header = "Header"
        val result = convertQRDataIntoBase64("$header$data", ECC.L)

        assert(result.isNotEmpty()) { "Result should not be empty for valid input" }
    }

    @Test
    fun `test convertQrDataIntoBase64 with exception`() {
        mockkStatic("io.nayuki.qrcodegen.QrCode")
        every { QrCode.encodeText(any(), any()) } throws RuntimeException("Mocked exception")

        val data = "Test QR Data"
        val header = "Header"
        val result = convertQRDataIntoBase64("$header$data", ECC.L)

        assertEquals("", result, "The result should be an empty string when an exception occurs")
    }

}
