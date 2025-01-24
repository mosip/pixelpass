package io.mosip.pixelpass.cose

import COSE.*
import com.upokecenter.cbor.CBORObject
import com.upokecenter.cbor.CBORObject.DecodeFromBytes
import com.upokecenter.cbor.CBORType
import io.mosip.pixelpass.exception.AceException


class CWT(private val claims: Map<Short, CBORObject>) {

    fun getClaim(name: Short): CBORObject {
        return claims[name]!!
    }

    companion object {
        @Throws(Exception::class)
        fun processCOSE(coseCwt: ByteArray, ctx: CwtCryptoCtx): CWT {

            var cbor = DecodeFromBytes(coseCwt)

            if (cbor.HasTag(61)) {
                cbor = cbor.UntagOne()
            }
            val coseRaw = Message.DecodeFromBytes(cbor.EncodeToBytes())

            if (coseRaw is SignMessage) {
                //Check all signers, if kid is present compare that first
                val myKid = ctx.publicKey!![CBORObject.FromObject(HeaderKeys.KID)]
                for (s: Signer in coseRaw.signerList) {
                    val kid = s.findAttribute(HeaderKeys.KID)
                    if (myKid == null || myKid.equals(kid)) {
                        s.setKey(ctx.publicKey)
                        if (coseRaw.validate(s)) {
                            return CWT(getParams(DecodeFromBytes(coseRaw.GetContent())))
                        }
                    }
                }
                throw AceException("No valid signature found")
            }
            else if (coseRaw is Sign1Message) {
                if (coseRaw.validate(ctx.publicKey)) {
                    return CWT(getParams(DecodeFromBytes(coseRaw.GetContent())))
                }
            }
            else if (coseRaw is MACMessage) {
                for (me: Recipient in ctx.recipients) {
                    val myKid = me.findAttribute(HeaderKeys.KID)
                    val myAlg = me.findAttribute(HeaderKeys.Algorithm)
                    val key = CBORObject.NewMap()
                    key.Add(KeyKeys.KeyType.AsCBOR(), KeyKeys.KeyType_Octet)
                    key.Add(KeyKeys.Octet_K.AsCBOR(), CBORObject.FromObject(
                            me.getKey(AlgorithmID.FromCBOR(myAlg))))
                    for (r: Recipient in coseRaw.recipientList) {
                        if (myKid == null || myKid.equals(r.findAttribute(HeaderKeys.KID))) {
                            if (myAlg.equals(r.findAttribute(HeaderKeys.Algorithm))) {
                                val coseKey = OneKey(key)
                                r.SetKey(coseKey)
                                if (coseRaw.Validate(r)) {
                                    return CWT(getParams(DecodeFromBytes(coseRaw.GetContent())))
                                }
                            }
                        }
                    }
                }
                throw AceException("No valid MAC found")
            }
            else if (coseRaw is MAC0Message) {
                if (coseRaw.Validate(ctx.key)) {
                    return CWT(getParams(DecodeFromBytes(coseRaw.GetContent())))
                }
            }
            else if (coseRaw is EncryptMessage) {
                for (me: Recipient in ctx.recipients) {
                    val myKid = me.findAttribute(HeaderKeys.KID)
                    val myAlg = me.findAttribute(HeaderKeys.Algorithm)
                    val key = CBORObject.NewMap()
                    key.Add(KeyKeys.KeyType.AsCBOR(), KeyKeys.KeyType_Octet)
                    key.Add(
                        KeyKeys.Octet_K.AsCBOR(), CBORObject.FromObject(
                            me.getKey(AlgorithmID.FromCBOR(myAlg)))
                    )
                    for (r: Recipient in coseRaw.recipientList) {
                        if (myKid == null || myKid.equals(r.findAttribute(HeaderKeys.KID))) {
                            if (myAlg.equals(r.findAttribute(HeaderKeys.Algorithm))) {
                                val coseKey = OneKey(key)
                                r.SetKey(coseKey)
                                val plaintext: ByteArray = processDecrypt(coseRaw, r)
                                return CWT(getParams(DecodeFromBytes(plaintext)))
                            }
                        }
                    }
                }
                throw AceException("No valid key for ciphertext found")
            }
            else if (coseRaw is Encrypt0Message) {
                return CWT(getParams(DecodeFromBytes(coseRaw.decrypt(ctx.key))))
            }
            throw AceException("Unknown or invalid COSE crypto wrapper")
        }

        private fun processDecrypt(m: EncryptMessage, r: Recipient): ByteArray {
            try {
                return m.decrypt(r)
            } catch (e: CoseException) {
                throw e
            }
        }

        @Throws(AceException::class)
        private fun getParams(cbor: CBORObject): Map<Short, CBORObject> {
            if (cbor.type != CBORType.Map) {
                throw AceException("CBOR object is not a Map")
            }
            val ret: MutableMap<Short, CBORObject> = HashMap()
            for (key: CBORObject in cbor.keys) {
                if (key.type != CBORType.Integer) {
                    throw AceException("CBOR key was not a Short: $key")
                }
                ret[key.AsInt16()] = cbor.get(key)
            }
            return ret
        }
    }
}