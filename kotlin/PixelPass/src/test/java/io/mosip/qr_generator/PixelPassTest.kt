package io.mosip.qr_generator

import android.graphics.Bitmap
import android.util.Log
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import io.mosip.pixelpass.PixelPass
import io.mosip.pixelpass.types.ECC
import io.mosip.pixelpass.zlib.ZLib
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileOutputStream

class PixelPassTest {

    @Before
    fun before() {
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
    }

    @After
    fun after() {
        clearAllMocks()
    }

    @Test
    fun `should return bitmap QR for given data`() {
        mockkConstructor(ZLib::class)
        mockkStatic(Bitmap::class)

        val mockedEncoded = byteArrayOf(1, 2, 3, 4)
        val expected = mockk<Bitmap>()
        every { anyConstructed<ZLib>().encode(any(), any()) } returns mockedEncoded
        every { Bitmap.createBitmap(any(), any(), any()) } returns expected
        every { expected.height } returns 10
        every { expected.width } returns 10
        every { expected.setPixel(any(), any(), any()) } just runs


        val data = "test"
        val actual = PixelPass().generateQRCode(data, ECC.M, "")
        Assert.assertNotNull(actual)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data`() {
        val data = "NCFKVPV0QSIP600GP5L0"
        val expected = "hello"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data in cbor`() {
        val data = "NCF3QBXJA5NJRCOC004 QN4"
        val expected = "{\"temp\":15}"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return encoded QR data for given data with CBOR`() {
        val data =
            "{\"str\":\"stringtype\",\"intP\":10,\"intN\":-10,\"intL\":111111110,\"intLN\":111111110,\"float\":10.01,\"nulltype\":null,\"bool\":true,\"bool2\":false,\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"objE\":{},\"objS\":{\"str\":\"stringtype\"}}"
        val expected =
            "NCF6QB2NJXTAGPTV30I-R.431DJENA2JA-NEO:2RZI.3TL69%5L+2T+BTR\$9M PHQUKSIEUJ4\$F W0XQ08LA-NEYJ25/FTELJTPC31L.R-PI+YQXDPV0Q0C5-Q5S2W5OIJWIQZNOLN*XKRK1OP65QQ-NKQVB%/JX1M%9IF+8U48+SB000Z2WWS7"

        val actual = PixelPass().generateQRData(data)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded JSON data for given QR data with CBOR`() {
        val expected =
            "{\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"bool\":true,\"intLN\":111111110,\"intL\":111111110,\"float\":10.01,\"intN\":-10,\"nulltype\":null,\"objS\":{\"str\":\"stringtype\"},\"str\":\"stringtype\",\"intP\":10,\"bool2\":false,\"objE\":{}}"
        val data =
            "NCF6QB2NJXTAGPTV30I-R.431DJENA2JA-NEO:2RZI.3TL69%5L+2T+BTR\$9M PHQUKSIEUJ4\$F W0XQ08LA-NEYJ25/FTELJTPC31L.R-PI+YQXDPV0Q0C5-Q5S2W5OIJWIQZNOLN*XKRK1OP65QQ-NKQVB%/JX1M%9IF+8U48+SB000Z2WWS7"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `encode in js decode in kotlin`() {
        val expected =
            "{\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"bool\":true,\"intLN\":111111110,\"intL\":111111110,\"float\":10.01,\"intN\":-10,\"nulltype\":null,\"objS\":{\"str\":\"stringtype\"},\"str\":\"stringtype\",\"intP\":10,\"bool2\":false,\"objE\":{}}"
        val data =
            "NCF6QBJUBZJA W04IJFLTY\$IFHL4IJNU44TBJQQRJ2\$SVMLM:8QP/I2NC7D8RDDQOVXY4%V3WABH-EF3OU0Q8O5MIP.HDQ1JMZI.9K:V6JR8X\$F1Y9WH5FWE%109/D6XH1+P:GLVHL E7JJ1 H9LOEQS4PRAAUI+SBSCGCHSU7D00089AWS7"

        val actual = PixelPass().decode(data)
        Assert.assertEquals(expected, actual)
    }


    @Test
    fun `should return mapped CBOR data for given data with map`() {
        val expected = "a36131633230376132644a686f6e613365486f6e6179";
        val data = JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}")
        val mapper = mapOf("id" to "1", "name" to "2", "l_name" to "3")

        val actual = PixelPass().getMappedData(data, mapper, true)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return mapped data for given data with map`() {
        val expected = "{ 1: 207, 2: Jhon, 3: Honay }";
        val data = JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}")
        val mapper = mapOf("id" to "1", "name" to "2", "l_name" to "3")

        val actual = PixelPass().getMappedData(data, mapper)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return mapped CBOR data for given data with map for claim 169 semantics`() {
        val expected =
            "b261316e3131313130303030333234303133613263312e30613362454e61346c5065746572204d204a686f6e61356550657465726136614d6137644a686f6e61386831393838303130326139613162313078184e657720436974792c204d4554524f204c494e452c205041623131717065746572406578616d706c652e636f6d6231326a2b31203233342d35363762313362555362313461326231356a4a686f6e20486f6e61696231367903513033434241424446383344303638414342354445363542334344463235453030333646324335343643423930333738433538374130373645374137353944464432374341373837324236434446463333394145414143413631413630323346443144333035413942344633334341413234384345444533384236374437433931354335394135314242344537374431303037374136323532353838373331383346383244363546344334383235303341354130314634314445453631324333353432453533373039383738313545353932423845413230323046443342444443373437383937444231303233374541443137394535354234343142433644384241443037434535333531323943463844353539343435434333413239443734364642463131373444453245374330463334333942453744424541343532304346383838323541414536423146323931413734364142383137374336354232413435394444313942443332433043333037303030344238354331443633303334373037434336393041423042413032333335304338333337464336383934303631454238413731344138463232464532333635453741393034433732444543393734364142454131413332393645434143443141343034353037393445444344324233343834344537433139454237464231413441463342303543334233373442443239343136303346373244334639413632454142394132464441454545454338454536453335304638413138363343304130414231423430353844313534353539413143443531333345464346363832414243333339393630383139433934323738383944363033383042363335413744323144303137393734424241353737393834393046363638414444383644413538313235443943344331323032434131333038463737333445343345384637374345423041463936384138463842383838343946394239384232363632303339393437304544303537453739333144454438323837364443413839364133304430303331413843424437423945444644463136433135433638353346344638443945454330393331374338344544414534423334394645353444323344384543374443394242394636394644374237423233333833423634463232453235466231376132623138665b312c20325d";
        val data = JSONObject(
            "{\"id\":\"11110000324013\",\"version\":\"1.0\",\"language\":\"EN\",\"fullName\":\"Peter M Jhon\",\"firstName\":\"Peter\",\"middleName\":\"M\",\"lastName\":\"Jhon\",\"dob\":\"19880102\",\"gender\":\"1\",\"address\":\"New City, METRO LINE, PA\",\"email\":\"peter@example.com\",\"phone\":\"+1 234-567\",\"nationality\":\"US\",\"maritalStatus\":\"2\",\"guardian\":\"Jhon Honai\",\"binaryImage\":\"03CBABDF83D068ACB5DE65B3CDF25E0036F2C546CB90378C587A076E7A759DFD27CA7872B6CDFF339AEAACA61A6023FD1D305A9B4F33CAA248CEDE38B67D7C915C59A51BB4E77D10077A625258873183F82D65F4C482503A5A01F41DEE612C3542E5370987815E592B8EA2020FD3BDDC747897DB10237EAD179E55B441BC6D8BAD07CE535129CF8D559445CC3A29D746FBF1174DE2E7C0F3439BE7DBEA4520CF88825AAE6B1F291A746AB8177C65B2A459DD19BD32C0C3070004B85C1D63034707CC690AB0BA023350C8337FC6894061EB8A714A8F22FE2365E7A904C72DEC9746ABEA1A3296ECACD1A40450794EDCD2B34844E7C19EB7FB1A4AF3B05C3B374BD2941603F72D3F9A62EAB9A2FDAEEEEC8EE6E350F8A1863C0A0AB1B4058D154559A1CD5133EFCF682ABC339960819C9427889D60380B635A7D21D017974BBA57798490F668ADD86DA58125D9C4C1202CA1308F7734E43E8F77CEB0AF968A8F8B88849F9B98B26620399470ED057E7931DED82876DCA896A30D0031A8CBD7B9EDFDF16C15C6853F4F8D9EEC09317C84EDAE4B349FE54D23D8EC7DC9BB9F69FD7B7B23383B64F22E25F\",\"binaryImageFormat\":\"2\",\"bestQualityFingers\":\"[1, 2]\"}"
        )
        val mapper = mapOf(
            "id" to "1",
            "version" to "2",
            "language" to "3",
            "fullName" to "4",
            "firstName" to "5",
            "middleName" to "6",
            "lastName" to "7",
            "dob" to "8",
            "gender" to "9",
            "address" to "10",
            "email" to "11",
            "phone" to "12",
            "nationality" to "13",
            "maritalStatus" to "14",
            "guardian" to "15",
            "binaryImage" to "16",
            "binaryImageFormat" to "17",
            "bestQualityFingers" to "18"
        )

        val actual = PixelPass().getMappedData(data, mapper, true)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return properly mapped JSON data for given CBOR`() {
        val expected =
            JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}").toString()
        val data = "a302644a686f6e01633230370365486f6e6179"
        val mapper = mapOf("1" to "id", "2" to "name", "3" to "l_name")

        val actual = PixelPass().decodeMappedData(data, mapper)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return properly mapped JSON data for given data`() {
        val expected =
            JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}").toString()
        val data = "{ \"1\": \"207\", 2: Jhon, 3: Honay }"
        val mapper = mapOf("1" to "id", "2" to "name", "3" to "l_name")

        val actual = PixelPass().decodeMappedData(data, mapper)
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data for zipped data`() {
        val expected = "Hello World!!";
        val createTempFile = File("certificate.json")
        val fos = FileOutputStream(createTempFile)
        fos.write(expected.toByteArray())
        fos.close()
        val tempZip = File.createTempFile("temp", ".zip")
        ZipUtil.packEntry(createTempFile, tempZip);

        val actual = PixelPass().decode(tempZip.readBytes())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data for zipped data`() {
        val expected = "Hello World!!";
        val createTempFile = File("certificate.json")
        val fos = FileOutputStream(createTempFile)
        fos.write(expected.toByteArray())
        fos.close()
        val tempZip = File.createTempFile("temp", ".zip")
        ZipUtil.packEntry(createTempFile, tempZip);

        val actual = PixelPass().decode(tempZip.readBytes())
        Assert.assertEquals(expected, actual)
    }
}


