package io.mosip.pixelpass.cose

import org.junit.Assert.assertFalse
import org.junit.Test

class UtilTest {

    @Test
    fun `should return true for PhilSys QRData`() {
        val data = "PH1:RRQXO8P609CKSD00XKDJCX498F3:M0MSM:FKQP"
        val isPhilSysQRData = Util().isPhilSysQRData(data, arrayListOf("PH1"))
        assert(isPhilSysQRData)
    }

    @Test
    fun `should return false for non PhilSys QRData`() {
        val data = "IN1:RRQXO8P609CKSD00XKDJCX498F3:M0MSM:FKQP"
        val isPhilSysQRData = Util().isPhilSysQRData(data, arrayListOf("PH1", "PH2"))
        assertFalse(isPhilSysQRData)
    }
}