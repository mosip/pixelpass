import {decodeData, generateQRBase64Image, generateQRData} from "../src";
import expect from "expect";
import {ECC} from "../src/types/ECC";

test("should return decoded data for given QR data", () => {
    const data = "NCFKVPV0QSIP600GP5L0";
    const expected = "hello";

    const actual = decodeData(data);
    expect(actual).toBe(expected);
});


test("should return decoded data for given QR data in cbor", () => {
    const data = "NCFHPE/Q6:96+963Y6:96P563H0 %2DH0";
    const expected =  "{\"temp\":15}";

    const actual = decodeData(data);
    expect(actual).toBe(expected);
});

//todo :: document exceptions
//todo :: write test for same
test("should return encoded QR data for data", () => {
    const expected = "NCFKVPV0QSIP600GP5L0";
    const  data = "hello";

    const actual = generateQRData(data);
    expect(actual).toBe(expected);
});

test("should return encoded QR data for data with header", () => {
    const expected = "mockHeader://" + "NCFKVPV0QSIP600GP5L0";
    const  data = "hello";

    const actual = generateQRData(data ,  "mockHeader://");
    expect(actual).toBe(expected);
});
test("should return base64 encoded QR for given data", async () => {
    const data = "hello";
    const expected = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQ4AAAEOCAYAAAB4sfmlAAAAAklEQVR4AewaftIAAAUpSURBVO3BQY5bCw4EsCrB97+yJvvePH14YKdDsvtHAA4mAEcTgKMJwNEE4GgCcDQBOJoAHE0AjiYARxOAownA0QTgaAJwNAE4mgAcTQCOJgBHE4CjCcDRBOBoAnA0ATiaABxNAI4mAEcTgKMJwNEE4GgCcPTKh7QNP+1uPqFtntjdPNE2T+xunmgbftrdfMIE4GgCcDQBOJoAHE0AjiYARxOAownA0QTg6JUvt7v5DdrmndrmnXY3v8Hu5jdom282ATiaABxNAI4mAEcTgKMJwNEE4GgCcDQBOHrll2ibT9jdfMLu5p3a5p12N9+sbT5hd/MbTACOJgBHE4CjCcDRBOBoAnA0ATiaABxNAI5e4Vdrmyd2N+/UNk/sbvj7TACOJgBHE4CjCcDRBOBoAnA0ATiaABxNAI5e4a/UNu/UNk/sbmACcDQBOJoAHE0AjiYARxOAownA0QTgaAJw9Movsbv5l+xuPqFtntjdfLPdDf/dBOBoAnA0ATiaABxNAI4mAEcTgKMJwNEE4OiVL9c2/NQ2T+xunmibJ3Y3T7TNE7ubd2ob/v8mAEcTgKMJwNEE4GgCcDQBOJoAHE0AjiYAR90/wj+vbT5hd8PfZwJwNAE4mgAcTQCOJgBHE4CjCcDRBOBoAnD0yoe0zRO7myfa5ondzRNt88Tu5om2eWJ38812N0+0zTdrmyd2N+/UNk/sbr7ZBOBoAnA0ATiaABxNAI4mAEcTgKMJwNEE4Kj7Rz6gbd5pd/NE2zyxu/mEtnlid/NE2zyxu3mibf4lu5tPaJsndjefMAE4mgAcTQCOJgBHE4CjCcDRBOBoAnA0ATh65cvtbp5om09om09omyd2N0+0zSfsbp5om09oG36aABxNAI4mAEcTgKMJwNEE4GgCcDQBOJoAHHX/CD+0zTvtbp5omyd2N9+sbZ7Y3bxT27zT7oafJgBHE4CjCcDRBOBoAnA0ATiaABxNAI4mAEev/GPa5ondzTu1zTu1zRO7m3dqm99gd/PN2uaJ3c0nTACOJgBHE4CjCcDRBOBoAnA0ATiaABxNAI66f4S/Ttv8Brubd2qbd9rdPNE277S7+WYTgKMJwNEE4GgCcDQBOJoAHE0AjiYARxOAo+4f+YC24afdzSe0zRO7m09omyd2N+/UNk/sbp5om3fa3XzCBOBoAnA0ATiaABxNAI4mAEcTgKMJwNEE4OiVL7e7+Q3a5hPa5p3a5hN2N0+0zRO7myd2N/w0ATiaABxNAI4mAEcTgKMJwNEE4GgCcDQBOHrll2ibT9jdfELbPLG7eae24b/b3fwGE4CjCcDRBOBoAnA0ATiaABxNAI4mAEcTgKNX+Cvtbp5omyd2N++0u3mibZ5omyd2N+/UNk/sbp5omyd2N99sAnA0ATiaABxNAI4mAEcTgKMJwNEE4GgCcPQKf6W2eae2eae2eWJ380Tb/Aa7myfa5ondzSdMAI4mAEcTgKMJwNEE4GgCcDQBOJoAHE0Ajl75JXY3/5LdzSe0zRO7myfa5ondzTu1zSe0zW8wATiaABxNAI4mAEcTgKMJwNEE4GgCcDQBOHrly7UNP7XNJ+xu3ml380TbPLG7eWJ38wm7myfa5ptNAI4mAEcTgKMJwNEE4GgCcDQBOJoAHE0Ajrp/BOBgAnA0ATiaABxNAI4mAEcTgKMJwNEE4GgCcDQBOJoAHE0AjiYARxOAownA0QTgaAJwNAE4mgAcTQCOJgBHE4CjCcDRBOBoAnA0ATiaABxNAI4mAEf/A4+rCzKLi4oKAAAAAElFTkSuQmCC"

    const actual = await generateQRBase64Image(data,ECC.M)
    expect(actual).toBe(expected);
});

test("should return base64 encoded QR for given data with header", async () => {
    const data = "hello";
    const header = "mockHeader://";
    const expected =  "data:image/png;base64,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"

    const actual = await generateQRBase64Image(data,ECC.M,header)
    expect(actual).toBe(expected);
});