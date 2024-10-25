package io.mosip.pixelpass.cbor

import android.os.Build
import io.mockk.every
import io.mockk.mockkObject
import io.mosip.pixelpass.common.BuildConfig
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Base64

class UtilsTest {

    @Before
    fun setUp() {
        mockkObject(BuildConfig)
        every { BuildConfig.getVersionSDKInt() } returns Build.VERSION_CODES.O
    }

    @Test
    fun `test cbor to JSON conversion`() {
        val data =
            "omd2ZXJzaW9uYzEuMGRkYXRhgaJiazFidjFiazKiZGsyLjGhZmsyLjEuMYHYGEmhZmsyLjEuMQFkazIuMoRDoQEmoRghWQFjMIIBXzCCAQSgAwIBAgIGAYwpA4_aMAoGCCqGSM49BAMCMDYxNDAyBgNVBAMMKzNfd1F3Y3Qxd28xQzBST3FfWXRqSTRHdTBqVXRiVTJCQXZteEltQzVqS3MwHhcNMjMxMjAyMDUzMjI4WhcNMjQwOTI3MDUzMjI4WjA2MTQwMgYDVQQDDCszX3dRd2N0MXdvMUMwUk9xX1l0akk0R3UwalV0YlUyQkF2bXhJbUM1aktzMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEQw7367PjIwU17ckX_G4ZqLW2EjPG0efV0cYzhvq2Ujkymrc33RVkgEE6q9iAAeLhl85IraAzT39SjOBV1EKu3jAKBggqhkjOPQQDAgNJADBGAiEAo4TsuxDl5-3eEp6SHDrBVn1rqOkGGLoOukJhelndGqICIQCpocrjWDwrWexoQZOOrwnEYRBmmfhaPor2OZCrbP3U69gYWLulZmsyLjIuMWMxLjBmazIuMi4yZnYyLjIuMmZrMi4yLjOhdmNvbS5leGFtcGxlLm5hbWVzcGFjZTGhAVggChSiDWMcNBzAxM6I-CuUe0P15BIwt06OIiNYkNyITxRmazIuMi40ZnYyLjIuNGZrMi4yLjWjYWHAdDIwMjMtMTItMDRUMTI6NDk6NDFaYWLAdDIwMjMtMTItMDRUMTI6NDk6NDFaYWPAdDIwMzMtMTItMDRUMTI6NDk6NDFaWEAE6jL7xUnhRbxd1LNq9xBA8G_RXGqFhc1GlKASbsfu7Mk-UJZzPvHis7zMRfYl2GNNgiTN-zbjFX_5IDdLi0jr"

        val toJson: Any = Utils().toJson(data)

        println("toJson ${toJson.toString()}")

        Assert.assertEquals(
            "{\"data\":[{\"k1\":\"v1\",\"k2\":{\"k2.2\":[{\"1\":-7},{\"33\":\"0�\\u0001_0�\\u0001\\u0004�\\u0003\\u0002\\u0001\\u0002\\u0002\\u0006\\u0001�)\\u0003��0\\n\\u0006\\b*�H�=\\u0004\\u0003\\u0002061402\\u0006\\u0003U\\u0004\\u0003\\f+3_wQwct1wo1C0ROq_YtjI4Gu0jUtbU2BAvmxImC5jKs0\\u001e\\u0017\\r231202053228Z\\u0017\\r240927053228Z061402\\u0006\\u0003U\\u0004\\u0003\\f+3_wQwct1wo1C0ROq_YtjI4Gu0jUtbU2BAvmxImC5jKs0Y0\\u0013\\u0006\\u0007*�H�=\\u0002\\u0001\\u0006\\b*�H�=\\u0003\\u0001\\u0007\\u0003B\\u0000\\u0004C\\u000e���#\\u00055��\\u0017�n\\u0019���\\u00123������3���R92��7�\\u0015d�A:�\u0600\\u0001���H��3O\u007FR��U�B��0\\n\\u0006\\b*�H�=\\u0004\\u0003\\u0002\\u0003I\\u00000F\\u0002!\\u0000���\\u0010����\\u0012��\\u001c:�V}k��\\u0006\\u0018�\\u000e�BazY�\\u001a�\\u0002!\\u0000����X<+Y�hA���\\t�a\\u0010f��Z>��9��l���\"},{\"k2.2.1\":\"1.0\",\"k2.2.2\":\"v2.2.2\",\"k2.2.3\":{\"com.example.namespace1\":{\"1\":\"\\n\\u0014�\\rc\\u001c4\\u001c��Έ�+�{C��\\u00120�N�\\\"#X�܈O\\u0014\"}},\"k2.2.4\":\"v2.2.4\",\"k2.2.5\":{\"a\":\"2023-12-04T12:49:41Z\",\"b\":\"2023-12-04T12:49:41Z\",\"c\":\"2033-12-04T12:49:41Z\"}},\"\\u0004�2��I�E�]Գj�\\u0010@�o�\\\\j���F��\\u0012n����>P�s>�⳼�E�%�cM�\$��6�\\u0015\u007F� 7K�H�\"],\"k2.1\":{\"k2.1.1\":[{\"k2.1.1\":1}]}}}],\"version\":\"1.0\"}",
            toJson.toString()
        )
    }
}