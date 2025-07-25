package io.mosip;

import io.mosip.pixelpass.PixelPass;
import io.mosip.pixelpass.types.ECC;

public class Main {
    public static void main(String[] args) {
        PixelPass pixelpass = new PixelPass();
        String base64PngImage = pixelpass.generateQRCode(insuranceVc, ECC.H, "");
        System.out.println("Hello world!"+base64PngImage);
    }
    static String insuranceVc = """
                   {
              "@context": [
                "https://www.w3.org/2018/credentials/v1",
                "https://holashchand.github.io/test_project/insurance-context.json",
                {
                  "LifeInsuranceCredential": {
                    "@id": "InsuranceCredential"
                  }
                },
                "https://w3id.org/security/suites/ed25519-2020/v1"
              ],
              "credentialSubject": {
                "id": "did:jwk:eyJrdHkiOiJFQyIsInVzZSI6InNpZyIsImNydiI6IlAtMjU2Iiwia2lkIjoiM2ZkbGlncW5RMVlEWklPRUNYRzVZcjFUcW4zbk9QNGtJNF9VdTN1eHpmRSIsIngiOiI4X1dvd3h2dXVjNmI4SGdWUmRpdzUzUmlZYWlTdUlqSHJlbEVlbEdpdHYwIiwieSI6Ikh0Q0dZLW1IcDQya0RrRmhUZWpkcFFhQllFMGNQRkhyNC1UNE1ORFYzT0EiLCJhbGciOiJFUzI1NiJ9",
                "dob": "1986-06-17",
                "email": "swati@gmail.com",
                "gender": "Female",
                "mobile": "0123456789",
                "benefits": [
                  "Critical Surgery",
                  "Full body checkup"
                ],
                "fullName": "Swati",
                "policyName": "Start Insurance Gold Premium",
                "policyNumber": "8793000",
                "policyIssuedOn": "2024-07-16",
                "policyExpiresOn": "2034-07-16"
              },
              "expirationDate": "2024-11-16T09:55:53.336Z",
              "id": "did:rcw:2d2d0601-f3ec-477f-b2ef-7a504d9bc2f5",
              "issuanceDate": "2024-10-17T09:55:53.394Z",
              "issuer": "did:web:api.dev1.mosip.net:identity-service:8ebda1d0-665b-4bb7-abc7-d4bf56b6ee09",
              "proof": {
                "created": "2024-10-17T09:55:53Z",
                "proofPurpose": "assertionMethod",
                "proofValue": "z5gwUgKKpJQdviRzpetw4kHqtZgR29fk86eyEUYQ8ev2EGQ7NbXoPATRAPJGuXTcbiz83GGhedam2nP3pENG3Gi8Q",
                "type": "Ed25519Signature2020",
                "verificationMethod": "did:web:api.dev1.mosip.net:identity-service:8ebda1d0-665b-4bb7-abc7-d4bf56b6ee09#key-0"
              },
              "type": [
                "VerifiableCredential",
                "LifeInsuranceCredential"
              ]
            }
            """;
}