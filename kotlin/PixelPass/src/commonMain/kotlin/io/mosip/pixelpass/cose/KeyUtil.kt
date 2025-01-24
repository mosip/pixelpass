package io.mosip.pixelpass.cose

import COSE.OneKey
import net.i2p.crypto.eddsa.EdDSASecurityProvider
import org.apache.commons.codec.binary.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.KeyFactory
import java.security.Security
import java.security.spec.X509EncodedKeySpec

object KeyUtil {
    private const val EDDSA_ALGO: String = "EdDSA"

    @Throws(Exception::class)
    fun oneKeyFromPublicKey(publicKey: String, algorithm: String, isPhilSysData: Boolean): OneKey {
        val (provider, publicKeyBytes, algo) = if (isPhilSysData) {
            val provider = EdDSASecurityProvider()
            Security.addProvider(provider)
            Triple(provider, Base64.decodeBase64(publicKey), EDDSA_ALGO)
        } else {
            val provider = BouncyCastleProvider()
            Security.addProvider(provider)
            val pemObject = PemReader(StringReader(publicKey)).readPemObject()
            Triple(provider, pemObject.content, algorithm)
        }
        val pubKeySpec = X509EncodedKeySpec(publicKeyBytes)
        val publicKeyObj = KeyFactory.getInstance(algo, provider).generatePublic(pubKeySpec)
        return OneKey(publicKeyObj, null)
    }
}