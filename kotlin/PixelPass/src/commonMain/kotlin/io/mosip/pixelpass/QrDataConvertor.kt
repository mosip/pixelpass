package io.mosip.pixelpass

import io.mosip.pixelpass.types.ECC

expect fun convertQrDataIntoBase64(dataWithHeader: String, ecc: ECC): String
