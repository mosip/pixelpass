package io.mosip.pixelpass.types

import io.nayuki.qrcodegen.QrCode

enum class ECC(val mEcc: QrCode.Ecc) {
    L(QrCode.Ecc.LOW),
    M(QrCode.Ecc.MEDIUM),
    Q(QrCode.Ecc.QUARTILE),
    H(QrCode.Ecc.HIGH)
}