package io.mosip.pixelpass.cose

import COSE.OneKey
import net.i2p.crypto.eddsa.EdDSASecurityProvider
import org.apache.commons.codec.binary.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.pem.PemReader
import java.io.StringReader
import java.security.KeyFactory
import java.security.Provider
import java.security.Security
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec

object KeyUtil {
    private const val EDDSA_ALGO: String = "EdDSA"

    @Throws(Exception::class)
    fun oneKeyFromPublicKeyForPhilSysData(publicKeyStr: String?): OneKey {
        val edDSAProvider: Provider = EdDSASecurityProvider()
        Security.addProvider(edDSAProvider)
        val publicKeyBytes = Base64.decodeBase64(publicKeyStr)
        val keyFactoryForPubKey = KeyFactory.getInstance(EDDSA_ALGO, edDSAProvider)
        val x509KeySpec: KeySpec = X509EncodedKeySpec(publicKeyBytes)
        val publicKeyObj = keyFactoryForPubKey.generatePublic(x509KeySpec)
        return OneKey(publicKeyObj, null)
    }

    @Throws(Exception::class)
    fun oneKeyFromPublicKey(publicKeyPem: String, algorithm: String): OneKey {
        val provider: Provider = BouncyCastleProvider()
        Security.addProvider(provider)
        val strReader = StringReader(publicKeyPem)
        val pemReader = PemReader(strReader)
        val pemObject = pemReader.readPemObject()
        val pubKeyBytes = pemObject.content
        val pubKeySpec = X509EncodedKeySpec(pubKeyBytes)
        val keyFactory = KeyFactory.getInstance(algorithm, provider)
        val publicKeyObj = keyFactory.generatePublic(pubKeySpec)
        return OneKey(publicKeyObj, null)
    }
}