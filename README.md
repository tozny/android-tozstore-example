# Tozny Android Example App

Android app that can be used to demonstrate and test Tozny products and tools such as TozStore and the e3db-java library.

## Setup

Update lines 17-19 of ClientGenerator.kt with both a valid [Tozny Client registration token](https://www.youtube.com/watch?v=L5ieMF9JZOg) for your account, and a JSON encoded set of Tozny client credentials

```java
    private val configJson =
        "{\"public_key\":\"qm1kcSzBc0PgpQHA26RpkGk3CNRK7cOXIAWFSHE5F0k\",\"public_signing_key\":\"uE2qaKUeq6kCL_eIgHSiNLnjQsACVJ16wNqfO5UX1GI\",\"private_signing_key\":\"qmfrsGSLnZbLpLKyiN_Zxpl9whcA6vu7LgVIzNR_31W4TapopR6rqQIv94iAdKI0ueNCwAJUnXrA2p87lRfUYg\",\"api_url\":\"https://api.e3db.com\",\"client_email\":\"android_test_client1\",\"private_key\":\"xsCGpTGEPKzt7hpNvZzaCQKyynkb85oMyA_3pjHf66o\",\"version\":2,\"api_key_id\":\"40ce9e2297e0d438b0ca91e143cc411fd936f4e7ab5fbbb323a4f100e8868195\",\"api_secret\":\"4cd5ee8b4ff8f4861b53cb3b6bd98d3c268f48367291328c0cd10ba19c0cc2ba\",\"client_id\":\"0d401425-6ee7-4067-af30-d62cc50ef08b\"}"
    val REGISTRATION_TOKEN = "83f00c3b6e8ac66aef4de99856034a6b3b68baedd1aebdc29502171b0464ad41"
```

## Capabilities

* Encrypt Text
* Decrypt Text
* Encrypt File (Photo)
* Decrypt File (Photo)
* Share Encrypted Data with another Tozny Client
* Trigger Biometric Prompt

## Limitations

- The app has to be run on an actual device, running on the Android emulator and clicking any of the buttons will cause the app to crash
