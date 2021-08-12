package com.tozny.androide3db

import androidx.lifecycle.MutableLiveData
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.tozny.e3db.*
import com.tozny.e3db.android.AndroidConfigStore

import java.io.IOException
import java.util.*

class ClientGenerator(context: AppCompatActivity){

    val e3dbClient: MutableLiveData<Client> = MutableLiveData();
    val eakInfo: MutableLiveData<LocalEAKInfo> = MutableLiveData()

    private val configJson = """
       {
  "version": "2",
  "public_signing_key": "Fi7R3YNg77C4DUws1IdW9ivav8zq4Z-D3PF3S4kJoLQ",
  "private_signing_key": "PtVq8_gNEGE5nUXDNnkaJ2junYlKvUIZ23Yk9O1zpEgWLtHdg2DvsLgNTCzUh1b2K9q_zOrhn4Pc8XdLiQmgtA",
  "client_id": "b895edc7-a8d9-431a-8536-fe281eb0f98f",
  "api_key_id": "dbd940300fc7949bee258297996c3ac25adec6ffd6943a83de2f3d69d76e1670",
  "api_secret": "2e87c39de607edae66d8cd1d0be2813def7f68d7f2bdd81a0576b6d6d80dbcdb",
  "public_key": "wGSCgNS7YWFCqhaAKCzqhCvKqw4MbW5P_qAlWmjMDVI",
  "private_key": "2wHx3hhYiA5vzBXuV5Q_VF-rh1ZWKxAHdhu03RXdPLo",
  "api_url": "https://dev.e3db.com",
  "client_email": ""
} 
    """.trimIndent()
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
