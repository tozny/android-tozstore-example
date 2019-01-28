package com.tozny.androide3db

import android.arch.lifecycle.MutableLiveData
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tozny.e3db.*
import com.tozny.e3db.android.AndroidConfigStore

import java.io.IOException
import java.util.*

class ClientGenerator(context: AppCompatActivity){

    val e3dbClient: MutableLiveData<Client> = MutableLiveData();
    val eakInfo: MutableLiveData<LocalEAKInfo> = MutableLiveData()

    private val configJson =
        "{\"public_key\":\"qm1kcSzBc0PgpQHA26RpkGk3CNRK7cOXIAWFSHE5F0k\",\"public_signing_key\":\"uE2qaKUeq6kCL_eIgHSiNLnjQsACVJ16wNqfO5UX1GI\",\"private_signing_key\":\"qmfrsGSLnZbLpLKyiN_Zxpl9whcA6vu7LgVIzNR_31W4TapopR6rqQIv94iAdKI0ueNCwAJUnXrA2p87lRfUYg\",\"api_url\":\"https://api.e3db.com\",\"client_email\":\"android_test_client1\",\"private_key\":\"xsCGpTGEPKzt7hpNvZzaCQKyynkb85oMyA_3pjHf66o\",\"version\":2,\"api_key_id\":\"40ce9e2297e0d438b0ca91e143cc411fd936f4e7ab5fbbb323a4f100e8868195\",\"api_secret\":\"4cd5ee8b4ff8f4861b53cb3b6bd98d3c268f48367291328c0cd10ba19c0cc2ba\",\"client_id\":\"0d401425-6ee7-4067-af30-d62cc50ef08b\"}"
    val REGISTRATION_TOKEN = "83f00c3b6e8ac66aef4de99856034a6b3b68baedd1aebdc29502171b0464ad41"
    val HOST = "https://api.e3db.com"
    val fipsConfigJson = "{\"private_key\":\"73kseulsALruDqlVNtuQ9DpTsXH5LIqepJaMrzakGdk\"}"



    init  {
        createDefaultClient()
        getEncryptionKey(context, "session")
    }

    @Throws(IOException::class)
    fun createClientFromString(json: String): Client {
        return ClientBuilder().fromConfig(Config.fromJson(json)).build()
    }

    @Throws(IOException::class)
    fun createDefaultClient() {
        e3dbClient.postValue(createClientFromString(configJson))
    }

    fun createNewConfig(token: String, context: AppCompatActivity): Client? {
        val client: MutableLiveData<Client> = MutableLiveData()
        Client.register(token, "${UUID.randomUUID()}",  HOST) { result: Result<Config> ->
            if(result.isError)
                throw RuntimeException(result.asError().other())

            // At this point we've registered with E3DB and received credentials. We'll save those credentials
            // to secure storage then create a `Client` instance for later use.
            val credentials = result.asValue()
            Config.saveConfigSecurely(AndroidConfigStore(context), credentials.json(), object : ConfigStore.SaveHandler {
                override fun saveConfigDidSucceed() {
                    // Create a e3dbClient with the given credentials and continue.
                    client.postValue(ClientBuilder().fromConfig(credentials).build())
                }

                override fun saveConfigDidCancel() {
                    // In this example app, this method should never be called.
                    TODO("Cancelled config save")
                }

                override fun saveConfigDidFail(p0: Throwable?) {
                    // In this example app, this method should never be called.
                    TODO("Failed to save config")
                }
            })
        }

        return client.value
    }

    fun saveStringAsDefaultConfig(creds: String, context: AppCompatActivity) {
        saveAsDefaultConfig(Config.fromJson(creds), context)
    }

    fun saveAsDefaultConfig(credentials: Config, context: AppCompatActivity) {
        Config.saveConfigSecurely(AndroidConfigStore(context), credentials.json(), object : ConfigStore.SaveHandler {
            override fun saveConfigDidSucceed() {
                // Create a e3dbClient with the given credentials and continue.
            }

            override fun saveConfigDidCancel() {
                // In this example app, this method should never be called.
                TODO("Cancelled config save")
            }

            override fun saveConfigDidFail(p0: Throwable?) {
                // In this example app, this method should never be called.
                TODO("Failed to save config")
            }
        })
    }

    fun saveAsNamedConfig(credentials: Config, context: AppCompatActivity, identifier: String) {
        Config.saveConfigSecurely(AndroidConfigStore(context, identifier), credentials.json(), object : ConfigStore.SaveHandler {
            override fun saveConfigDidSucceed() {
                // Create a e3dbClient with the given credentials and continue.
            }

            override fun saveConfigDidCancel() {
                // In this example app, this method should never be called.
                TODO("Cancelled config save")
            }

            override fun saveConfigDidFail(p0: Throwable?) {
                // In this example app, this method should never be called.
                TODO("Failed to save config")
            }
        })
    }

    fun loadClient(context: AppCompatActivity, identifier: String): Client? {
        val client: MutableLiveData<Client> = MutableLiveData()
        Config.loadConfigSecurely(AndroidConfigStore(context, identifier), object : ConfigStore.LoadHandler {
            override fun loadConfigDidSucceed(p0: String?) {
                // If this method is called, credentials were found in secure storage and are
                // available as the `p0` argument. The same credentials will be returned for a given
                // install (that is, credentials are not re-generated each time).
                //
                val config = Config.fromJson(p0)
                Log.i("MainActivity", "Loaded e3dbClient ${config.clientId}")
                Log.i("MainActivity", "$p0")

                // Use those credentials to create an instance of the `Client` class, which can be used to communicate
                // with E3DB.
                client.postValue(ClientBuilder().fromConfig(config).build())
            }

            override fun loadConfigDidCancel() {
                // In this example we should never see this method called.
                TODO("*impossible*: Config loading cancelled")
            }

            override fun loadConfigDidFail(p0: Throwable?) {
                // In this example we should never see this method called.
                TODO("Failed to load config")
            }

            override fun loadConfigNotFound() {
                // Configuration was not found, meaning we have never registered an E3DB e3dbClient
                // for this install. We'll do that now.
                client.postValue(createNewConfig(REGISTRATION_TOKEN, context))
            }
        })
        return client.value
    }

    fun loadClient(context: AppCompatActivity): Client? {
        val client: MutableLiveData<Client> = MutableLiveData()
        Config.loadConfigSecurely(AndroidConfigStore(context), object : ConfigStore.LoadHandler {
            override fun loadConfigDidSucceed(p0: String?) {
                // If this method is called, credentials were found in secure storage and are
                // available as the `p0` argument. The same credentials will be returned for a given
                // install (that is, credentials are not re-generated each time).
                //
                val config = Config.fromJson(p0)
                Log.i("MainActivity", "Loaded e3dbClient ${config.clientId}")
                Log.i("MainActivity", "$p0")

                // Use those credentials to create an instance of the `Client` class, which can be used to communicate
                // with E3DB.
                client.postValue(ClientBuilder().fromConfig(config).build())
            }
            override fun loadConfigDidCancel() {
                // In this example we should never see this method called.
                TODO("*impossible*: Config loading cancelled")
            }

            override fun loadConfigDidFail(p0: Throwable?) {
                // In this example we should never see this method called.
                TODO("Failed to load config")
            }

            override fun loadConfigNotFound() {
                // Configuration was not found, meaning we have never registered an E3DB e3dbClient
                // for this install. We'll do that now.
                client.postValue(createNewConfig(REGISTRATION_TOKEN, context))
            }
        })
        return client.value
    }


    private fun getEncryptionKey(context: AppCompatActivity, recordType: String) {
        /**
         * `createWriterKey` creates the encryption key that this app will use when writing session data. This key
         * is unique to the E3DB e3dbClient associated with this app install.
         *
         * The key returned can be saved to disk and re-used but for this sample we just retrieve it every time.
         */
        e3dbClient.observe({context.lifecycle }, { it?.let { client ->
            client.createWriterKey(recordType) { result ->
                if (result.isError) {
                    Log.e("MainActivity", "Error creating key", result.asError().other())
                    throw RuntimeException(result.asError().other())
                }

                eakInfo.postValue(result.asValue())
            }
        }})
    }




}
