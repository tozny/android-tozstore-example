package com.tozny.androide3db;

import com.tozny.e3db.Client;
import com.tozny.e3db.ClientBuilder;
import com.tozny.e3db.Config;
import com.tozny.e3db.E3DBCryptoException;

import java.io.IOException;

public class ClientGenerator {
    private static String clientBlob = "{\"public_key\":\"qm1kcSzBc0PgpQHA26RpkGk3CNRK7cOXIAWFSHE5F0k\",\"public_signing_key\":\"uE2qaKUeq6kCL_eIgHSiNLnjQsACVJ16wNqfO5UX1GI\",\"private_signing_key\":\"qmfrsGSLnZbLpLKyiN_Zxpl9whcA6vu7LgVIzNR_31W4TapopR6rqQIv94iAdKI0ueNCwAJUnXrA2p87lRfUYg\",\"api_url\":\"https://api.e3db.com\",\"client_email\":\"android_test_client1\",\"private_key\":\"xsCGpTGEPKzt7hpNvZzaCQKyynkb85oMyA_3pjHf66o\",\"version\":2,\"api_key_id\":\"40ce9e2297e0d438b0ca91e143cc411fd936f4e7ab5fbbb323a4f100e8868195\",\"api_secret\":\"4cd5ee8b4ff8f4861b53cb3b6bd98d3c268f48367291328c0cd10ba19c0cc2ba\",\"client_id\":\"0d401425-6ee7-4067-af30-d62cc50ef08b\"}";

    public static Client createClient() throws IOException, E3DBCryptoException {
        return new ClientBuilder().fromConfig(Config.fromJson(clientBlob)).build();
    }
}
