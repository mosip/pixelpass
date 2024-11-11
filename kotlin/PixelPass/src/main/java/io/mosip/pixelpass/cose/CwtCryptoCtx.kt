package io.mosip.pixelpass.cose

import COSE.MessageTag
import COSE.OneKey
import COSE.Recipient
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security


 class CwtCryptoCtx {
    var messageType: MessageTag
        private set

    var recipients: List<Recipient> = emptyList()
        private set

    var key: ByteArray? = null
        private set

    var publicKey: OneKey? = null
        private set


     private constructor(what: MessageTag, key: ByteArray?) {
        this.messageType = what
        this.key = key
    }

     private constructor(what: MessageTag, publicKey: OneKey?) {
        this.messageType = what
        this.publicKey = publicKey
    }

    private constructor(what: MessageTag, recipients: List<Recipient>?) {
        this.messageType = what
        this.recipients = ArrayList()
        (this.recipients as ArrayList<Recipient>).addAll(recipients!!)
    }


    companion object {
        init {
            Security.addProvider(
                BouncyCastleProvider()
            )
        }

        fun encrypt(recipients: List<Recipient>): CwtCryptoCtx {
            return CwtCryptoCtx(MessageTag.Encrypt, recipients)
        }

        fun encrypt0(rawSymmetrickey: ByteArray): CwtCryptoCtx {
            return CwtCryptoCtx(MessageTag.Encrypt0, rawSymmetrickey)
        }

        fun mac(recipients: List<Recipient>): CwtCryptoCtx {
            return CwtCryptoCtx(MessageTag.MAC, recipients)
        }

        fun mac0(rawSymmetricKey: ByteArray): CwtCryptoCtx {
            return CwtCryptoCtx(MessageTag.MAC0, rawSymmetricKey)
        }

        fun signVerify(publicKey: OneKey): CwtCryptoCtx {
            return CwtCryptoCtx(MessageTag.Sign, publicKey)
        }

        fun sign1Verify(publicKey: OneKey): CwtCryptoCtx {
            return CwtCryptoCtx(MessageTag.Sign1, publicKey)
        }
    }
}