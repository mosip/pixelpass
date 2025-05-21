package io.mosip.pixelpass

import io.mockk.*
import io.mosip.pixelpass.exception.UnknownBinaryFileTypeException
import io.mosip.pixelpass.zlib.ZLib
import junit.framework.TestCase.assertEquals
import nl.minvws.encoding.Base45
import org.json.JSONObject
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.FileOutputStream
import kotlin.test.*


class PixelPassTest {

    @AfterTest
    fun after() {
        clearAllMocks()
    }


    @Test
    fun `should return decoded data for given QR data`() {
        val data = "NCFKVPV0QSIP600GP5L0"
        val expected = "hello"

        val actual = PixelPass().decode(data)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded data for given QR data in cbor`() {
        val data = "NCF3QBXJA5NJRCOC004 QN4"
        val expected = "{\"temp\":15}"

        val actual = PixelPass().decode(data)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return encoded QR data for given data with CBOR`() {
        val data =
            "{\"str\":\"stringtype\",\"intP\":10,\"intN\":-10,\"intL\":111111110,\"intLN\":111111110,\"float\":10.01,\"nulltype\":null,\"bool\":true,\"bool2\":false,\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"objE\":{},\"objS\":{\"str\":\"stringtype\"}}"
        val expected =
            "NCF6QB2NJXTAGPTV30I-R.431DJENA2JA-NEO:2RZI.3TL69%5L+2T+BTR\$9M PHQUKSIEUJ4\$F W0XQ08LA-NEYJ25/FTELJTPC31L.R-PI+YQXDPV0Q0C5-Q5S2W5OIJWIQZNOLN*XKRK1OP65QQ-NKQVB%/JX1M%9IF+8U48+SB000Z2WWS7"

        val actual = PixelPass().generateQRData(data)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return decoded JSON data for given QR data with CBOR`() {
        val expected =
            "{\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"bool\":true,\"intLN\":111111110,\"intL\":111111110,\"float\":10.01,\"intN\":-10,\"nulltype\":null,\"objS\":{\"str\":\"stringtype\"},\"str\":\"stringtype\",\"intP\":10,\"bool2\":false,\"objE\":{}}"
        val data =
            "NCF6QB2NJXTAGPTV30I-R.431DJENA2JA-NEO:2RZI.3TL69%5L+2T+BTR\$9M PHQUKSIEUJ4\$F W0XQ08LA-NEYJ25/FTELJTPC31L.R-PI+YQXDPV0Q0C5-Q5S2W5OIJWIQZNOLN*XKRK1OP65QQ-NKQVB%/JX1M%9IF+8U48+SB000Z2WWS7"

        val actual = PixelPass().decode(data)
        assertEquals(expected, actual)
    }

    @Test
    fun `encode in js decode in kotlin`() {
        val expected =
            "{\"arryE\":[],\"arryF\":[1,2,3,-4,\"hello\",{\"temp\":123}],\"bool\":true,\"intLN\":111111110,\"intL\":111111110,\"float\":10.01,\"intN\":-10,\"nulltype\":null,\"objS\":{\"str\":\"stringtype\"},\"str\":\"stringtype\",\"intP\":10,\"bool2\":false,\"objE\":{}}"
        val data =
            "NCF6QBJUBZJA W04IJFLTY\$IFHL4IJNU44TBJQQRJ2\$SVMLM:8QP/I2NC7D8RDDQOVXY4%V3WABH-EF3OU0Q8O5MIP.HDQ1JMZI.9K:V6JR8X\$F1Y9WH5FWE%109/D6XH1+P:GLVHL E7JJ1 H9LOEQS4PRAAUI+SBSCGCHSU7D00089AWS7"

        val actual = PixelPass().decode(data)
        assertEquals(expected, actual)
    }


    @Test
    fun `should return mapped CBOR data for given data with map`() {
        val expected = "a36131633230376132644a686f6e613365486f6e6179";
        val data = JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}")
        val mapper = mapOf("id" to "1", "name" to "2", "l_name" to "3")

        val actual = PixelPass().getMappedData(data, mapper, true)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return mapped data for given data with map`() {
        val expected = "{ 1: 207, 2: Jhon, 3: Honay }";
        val data = JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}")
        val mapper = mapOf("id" to "1", "name" to "2", "l_name" to "3")

        val actual = PixelPass().getMappedData(data, mapper)
        assertEquals(expected, actual)
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
        assertEquals(expected, actual)
    }

    @Test
    fun `should return properly mapped JSON data for given CBOR`() {
        val expected =
            JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}").toString()
        val data = "a302644a686f6e01633230370365486f6e6179"
        val mapper = mapOf("1" to "id", "2" to "name", "3" to "l_name")

        val actual = PixelPass().decodeMappedData(data, mapper)
        assertEquals(expected, actual)
    }

    @Test
    fun `should return properly mapped JSON data for given data`() {
        val expected =
            JSONObject("{\"name\": \"Jhon\", \"id\": \"207\", \"l_name\": \"Honay\"}").toString()
        val data = "{ \"1\": \"207\", 2: Jhon, 3: Honay }"
        val mapper = mapOf("1" to "id", "2" to "name", "3" to "l_name")

        val actual = PixelPass().decodeMappedData(data, mapper)
        assertEquals(expected, actual)
    }

    @Test
    fun `should verify and return decoded data for the given signed cbor data`() {
        val expected = "{ NFI: 5796524113, img: UklGRtoBAABXRUJQVlA4IM4BAABwCgCdASotAC0APpFAmkmlo6IhKrVaqLASCWMAxQv6hS2QBv1tGlm79xr5JS3T1e9es3IjYZwiTX77BvNKQ8ay480hd5VQBSTmsHH9RqRnIyE7vzUEmzB43iKHAAD9ErU994dKrN51DoAmEiFnU7vXUN5QJwJCFyTcIv4Hjx6Ngowb4Ns5ocabETr6WdXdXFCQrq5Hlm9W7-7DYC7ujsdjHuRI6UmxEpShlVrDbuAHanu7O7YFouLw684VxSkmCoqJSUWf5a2-d275ulg4WlhQs1CpFiE6l6RnYal1LKLNIwALMQzTtfhDG0FyDJnvhuBLeGvX7Qm5UacpV8DSFMfld-13GhBKplexC7zcHUyN9WMEOBDcSKxH_8g5_8PVAGx-qaJJ4JEVOTWv91Brnt-CcCNnmabaAtaf725dAavpIzHUU5aFUioZj6c1C1vvR4h1Z0_l1LwoBWnM8cDJwbhMLjOgJlXI-SMV4Tw-IZO0Fy5TR3nmNORKQNk-0zZ01jSLxThMilGeIq5OmfqzFkzO6Fm7-WIehwoUZOYwiPdYBfcld95pIlsd_rFbHcRtr081JPQWeGh7Thwl8Ib9cb9oo09GEOJkpV5l2_wdIAA, gender: M, surName: GANHOUNOUTO, givenName: HOUNNALEMI MARIUS BELLOR, dateOfBirth: 11/10/1986 }"
        val signCborData = "PH1:RRQXO8P609CKSD00XKDJCX498F3:M0MSM:FKQPCC\$CD53P1LD+9TC9XW6LA7SW6-Q6+96OPCH\$DFBBNRS%B93/8V50.100Y817AG6AS64V50M20+B18%J.E5010H10EGIWMJD.KFMK/H5XKBTBMI81H406N1S\$GF9IW1W+E3EYNKI3MV44ZQXBU%UMCL4AXJ%Z92XV2YUPP8URM/-PD5FA5A-T4YDM%0WCZK%J4+O7RV6%RJRCFJG4000V:V2-M7DV J9O5SM\$1Q 41A4FQA+9RW4S--4:F8VT43J40/0 %3UMGQN36WREKK7RJNK72GBD:RE8AX3MB29H3E.EULVO:-5B2I*NCU%S6MTFHMOZIT*IZVOLESYKD6UNM0NSQKPKUH2QS-OI 45NH\$B9S9KI/LJ4FUPV*6B/IB49A*8A+Z2EI7CZKQFCQ E7QK5J4UI1/R190NYL8EC8UQ1SCU:FS2AFHDREA1NEA8A5.GO9S2I0T-0UCD3UJ9*3BYL19\$RMU9:0V0O0-52J89Q492EP0FWX-Q9WD1KLCF9GFIKA72BMM7A 3K2MGIL44JJAPR/5R4CU4YB:WL9K4+\$QD1JJHAIA3A6LRJ1ABUDBHW2DL2TIZNZU0++PXGO2MOHS9VN63%4EIPQJ4FLSK%7CUI*-2YNALIF+U6IH9*KRPVQUYE-S6/-OJU9EEADH46.99VV% 237QLFBWNVU%3FC10YC4665CV.X06X4C5SKF4SZ3/IMFY3:%DR0AAU4\$\$2H9DG*9\$Z4.2H4HECAD:0A262PWC0.BL RVU3C2093D0/D5\$CODCN3D6%E++9F\$DDPDNB8G69DZAE1ASTASED/ED1\$C++9F\$D98FG69DZA8+9+S9UY9Y34RB88C9+MA0H82T9B1A.PDIECF CX-C*EDAWE5JD 96\$96L/5NA77*6:88ATV24UX.J1RR47RSERWZ1WCV*SB\$H1OWM++46BC5SFZZ9M175 R4 8BD6WAT\$PG44VATRO.TJACCMAIOEXY0TTMD4EU+DD0"
        val actual = PixelPass().decodeCWT(signCborData,
            "MCowBQYDK2VwAyEAAF8LPSpgm1XFXR8pZtuT3c80Jxjmub3Q-17gV3sCftU",
            emptyMap(),
            arrayListOf("PH1"),
            "EdDSA"
        )
        assertEquals(expected, actual)
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

        val actual = PixelPass().decodeBinary(tempZip.readBytes())
        assertEquals(expected, actual)
        tempZip.deleteOnExit()
    }

    @Test
    fun `should throw error if binary data type not zip`() {
        val tempZip = File.createTempFile("temp", ".png")

        tempZip.writeBytes(byteArrayOf(0x00, 0x01, 0x02)) // Writing some dummy bytes

        assertFailsWith<UnknownBinaryFileTypeException> { // ✅ Correct usage
            PixelPass().decodeBinary(tempZip.readBytes())
        }

        tempZip.deleteOnExit()
    }
    @Test
    fun `should encode raw data when JSON parsing fails`() {
        val invalidJson = "this is not valid json"
        val result = PixelPass().generateQRData(invalidJson)
        assertTrue(result.isNotEmpty(), "Encoded result should not be empty")

        val strippedHeader = result
        val decoded = Base45.getDecoder().decode(strippedHeader)
        val decompressed = ZLib().decode(decoded)
        val original = String(decompressed)

        assertEquals(invalidJson, original)
    }

    @Test
    fun `should return encoded QR data for JSON array`() {
        val jsonArray = """[{"name":"Alice","age":30},{"name":"Bob","age":25}]"""

        val result = PixelPass().generateQRData(jsonArray)

        assertTrue(result.isNotEmpty(), "Result should not be empty")

        val encoded = result
        val decodedBytes = Base45.getDecoder().decode(encoded)
        val decompressedBytes = ZLib().decode(decodedBytes)

        assertNotNull(decompressedBytes)
    }

}


