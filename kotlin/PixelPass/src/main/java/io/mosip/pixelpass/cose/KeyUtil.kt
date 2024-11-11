package io.mosip.pixelpass.cose

import COSE.OneKey
import net.i2p.crypto.eddsa.EdDSASecurityProvider
import org.apache.commons.codec.binary.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.KeyFactory
import java.security.Provider
import java.security.Security
import java.security.spec.KeySpec
import java.security.spec.X509EncodedKeySpec

object KeyUtil {
    private val EDDSA_ALGO: String = "EdDSA"

    @Throws(Exception::class)
    fun oneKeyFromPublicKey(publicKeyStr: String?): OneKey {

        //Why eddsa sec provider instead of bouncy castle?

        val edDSAProvider: Provider = EdDSASecurityProvider()

        val ed : Provider = BouncyCastleProvider()

        Security.addProvider(edDSAProvider)
        val publicKeyBytes = Base64.decodeBase64(publicKeyStr)
        val keyFactoryForPubKey = KeyFactory.getInstance(EDDSA_ALGO, edDSAProvider)
        val x509Keyspec: KeySpec = X509EncodedKeySpec(publicKeyBytes)
        val publicKeyObj = keyFactoryForPubKey.generatePublic(x509Keyspec)
        return OneKey(publicKeyObj, null)
    }
}
