package io.mosip.qr_generator.zlib

import io.mockk.clearAllMocks
import io.mosip.pixelpass.zlib.ZLib
import org.junit.After
import org.junit.Assert
import org.junit.Test

class ZLibTest{
    @After
    fun after(){
        clearAllMocks()
    }

    @Test
    fun `should encode given string to zlib format`(){
        val data = "test".toByteArray()
        val expected = byteArrayOf(120, -38, 43, 73, 45, 46, 1, 0, 4, 93, 1, -63)

        val actual = ZLib().encode(data)
        Assert.assertArrayEquals(expected,actual)
    }

    @Test
    fun `should encode given string to zlib format with given compression level`(){
        val data = "test".toByteArray();
        val expected = byteArrayOf(120, 94, 43, 73, 45, 46, 1, 0, 4, 93, 1, -63)

        val actual = ZLib().encode(data,5)
        Assert.assertArrayEquals(expected,actual)
    }
    @Test
    fun `should decode given zlib compressed byte array to sting format`(){
        val data = byteArrayOf(120, -38, 43, 73, 45, 46, 1, 0, 4, 93, 1, -63)
        val expected = "test".toByteArray();

        val actual = ZLib().decode(data)
        Assert.assertArrayEquals(expected,actual)
    }

}