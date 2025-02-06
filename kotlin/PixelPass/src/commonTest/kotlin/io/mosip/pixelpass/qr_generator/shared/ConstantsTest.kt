package io.mosip.pixelpass.qr_generator.shared

import io.mosip.pixelpass.shared.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ConstantsTest {
    @Test
    fun `test QR constants`() {
        assertEquals(15, QR_SCALE, "QR_SCALE should be 15")
        assertEquals(3, QR_BORDER, "QR_BORDER should be 3")
        assertEquals(100, QR_QUALITY, "QR_QUALITY should be 100")
    }

    @Test
    fun `test ZIP constants`() {
        assertEquals(9, DEFAULT_ZLIB_COMPRESSION_LEVEL, "DEFAULT_ZLIB_COMPRESSION_LEVEL should be 9")
        assertEquals("PK", ZIP_HEADER, "ZIP_HEADER should be 'PK'")
        assertEquals("certificate.json", DEFAULT_ZIP_FILE_NAME, "DEFAULT_ZIP_FILE_NAME should be 'certificate.json'")
    }
}