package io.mosip.pixelpass.cose

import COSE.*
import junit.framework.TestCase.*
import org.junit.Assert.assertArrayEquals
import kotlin.test.*
import java.security.SecureRandom

class CwtCryptoCtxTest {

    private fun randomKey(size: Int): ByteArray = ByteArray(size).apply {
        SecureRandom().nextBytes(this)
    }

    @Test
    fun `encrypt should return CwtCryptoCtx with Encrypt messageType and recipients`() {
        val recipient = Recipient()
        val ctx = CwtCryptoCtx.encrypt(listOf(recipient))

        assertEquals(MessageTag.Encrypt, ctx.messageType)
        assertEquals(1, ctx.recipients.size)
        assertTrue(ctx.recipients.contains(recipient))
        assertNull(ctx.key)
        assertNull(ctx.publicKey)
    }

    @Test
    fun `encrypt0 should return CwtCryptoCtx with Encrypt0 messageType and key`() {
        val key = randomKey(32)
        val ctx = CwtCryptoCtx.encrypt0(key)

        assertEquals(MessageTag.Encrypt0, ctx.messageType)
        assertArrayEquals(key, ctx.key)
        assertTrue(ctx.recipients.isEmpty())
        assertNull(ctx.publicKey)
    }

    @Test
    fun `mac should return CwtCryptoCtx with MAC messageType and recipients`() {
        val recipient = Recipient()
        val ctx = CwtCryptoCtx.mac(listOf(recipient))

        assertEquals(MessageTag.MAC, ctx.messageType)
        assertEquals(1, ctx.recipients.size)
        assertTrue(ctx.recipients.contains(recipient))
        assertNull(ctx.key)
        assertNull(ctx.publicKey)
    }

    @Test
    fun `mac0 should return CwtCryptoCtx with MAC0 messageType and key`() {
        val key = randomKey(16)
        val ctx = CwtCryptoCtx.mac0(key)

        assertEquals(MessageTag.MAC0, ctx.messageType)
        assertArrayEquals(key, ctx.key)
        assertTrue(ctx.recipients.isEmpty())
        assertNull(ctx.publicKey)
    }

    @Test
    fun `signVerify should return CwtCryptoCtx with Sign messageType and public key`() {
        val oneKey = OneKey.generateKey(AlgorithmID.ECDSA_256)
        val ctx = CwtCryptoCtx.signVerify(oneKey)

        assertEquals(MessageTag.Sign, ctx.messageType)
        assertEquals(oneKey, ctx.publicKey)
        assertTrue(ctx.recipients.isEmpty())
        assertNull(ctx.key)
    }

    @Test
    fun `sign1Verify should return CwtCryptoCtx with Sign1 messageType and public key`() {
        val oneKey = OneKey.generateKey(AlgorithmID.ECDSA_256)
        val ctx = CwtCryptoCtx.sign1Verify(oneKey)

        assertEquals(MessageTag.Sign1, ctx.messageType)
        assertEquals(oneKey, ctx.publicKey)
        assertTrue(ctx.recipients.isEmpty())
        assertNull(ctx.key)
    }
}
