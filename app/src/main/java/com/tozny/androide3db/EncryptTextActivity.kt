package com.tozny.androide3db

import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.tozny.e3db.*
import java.lang.RuntimeException
import java.util.*

class EncryptTextActivity : AppCompatActivity() {
    val clients = ClientGenerator(this)
    val suffix = "_fips_encrypted.bin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypt_text)
    }

    fun encryptAndUploadText(view: View) {
        val textToEncrypt = findViewById<EditText>(R.id.encrypt_text_to_encrypt_text).text
        val contentType = findViewById<EditText>(R.id.encrypt_text_content_type_text).text


        val data = HashMap<String, String>()
        data.put("info", textToEncrypt.toString())
        val meta = HashMap<String, String>()
        encryptAndUpload(contentType, data, meta, textToEncrypt)
    }

    private fun encryptAndUpload(
        contentType: Editable,
        data: HashMap<String, String>,
        meta: HashMap<String, String>,
        textToEncrypt: Editable
    ) {
        clients.e3dbClient.value?.let {
            it.write(contentType.toString(), RecordData(data), meta) { result ->
                if (result.isError) {
                    Toast.makeText(
                        applicationContext,
                        "Failed to encrypt and upload please try again",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    Log.e("EncryptTextActivity", "Failed to encrypt and upload", result?.asError()?.other())
                } else {
                    Toast.makeText(applicationContext, "Data encrypted and uploaded", Toast.LENGTH_LONG).show()
                    textToEncrypt.clear()
                    contentType.clear()
                }
            }
        }
    }

    fun storeLocal(view: View) {
        val textToEncrypt = findViewById<EditText>(R.id.encrypt_text_to_encrypt_text).text
        val contentType = findViewById<EditText>(R.id.encrypt_text_content_type_text).text


        val data = HashMap<String, String>()
        data.put("info", textToEncrypt.toString())
        val meta = HashMap<String, String>()
        val recordType = contentType.toString()

        clients.fipsLocal.value?.let { fips ->
            clients.e3dbClient.value?.let { e3db ->
                clients.eakInfo.value?.let { eak ->
                    val encryptedRecord = fips.fipsLocalEncryptRecord(
                        e3db,
                        recordType,
                        RecordData(data),
                        meta,
                        eak
                    )
                    val randomUUID = UUID.randomUUID()
                    openFileOutput("${randomUUID}$suffix", Context.MODE_PRIVATE)?.use { file ->
                        file.write(encryptedRecord.toByteArray())
                    }
                    Toast.makeText(
                        applicationContext,
                        "${randomUUID} successfully encrypted locally",
                        Toast.LENGTH_LONG
                    ).show()
                    textToEncrypt.clear()
                    contentType.clear()
                }
            }
        } ?: throw RuntimeException("Could not store local value")

    }

    fun readLocal(view: View) {
        val linear = findViewById<LinearLayout>(R.id.local_encrypt_text_query_layout)
        linear.removeAllViews()
        getFilesDir().listFiles({ file -> file.name.contains(suffix)}).forEach { sessionFile ->
            Log.i("MainActivity", "Uploading: ${sessionFile.name}")
            // First, load the encrypted record from disk and parse it into a `LocalEncryptedRecord` instance.

            val name = sessionFile.name
            val params = lowerMargin()
            val textView = TextView(applicationContext)
            textView.text = "${name}\t ${sessionFile.lastModified()}"
            textView.layoutParams = params
            textView.tag = name
            textView.setOnClickListener {
                decryptLocal(view, sessionFile.name)
            }
            linear.addView(textView)

                // Take the decrypted data and upload it as a new record to E3DB.
        }
    }

    fun decryptLocal(view: View, name: String) {
        val linear = findViewById<LinearLayout>(R.id.local_encrypt_text_query_layout)

        getFilesDir().listFiles({ file -> file.name.contains(name)}).forEach { sessionFile ->
            Log.i("MainActivity", "Uploading: ${sessionFile.name}")
            // First, load the encrypted record from disk and parse it into a `LocalEncryptedRecord` instance.
            val let = clients.fipsLocal.value?.let { fips ->
                clients.e3dbClient.value?.let { e3db ->
                    clients.eakInfo.value?.let { eak ->
                        val use = sessionFile.bufferedReader().use { it.readText() }
                        val record = fips.fipsLocalDecryptExistingRecord(e3db, use, eak)
                        val textView = TextView(applicationContext)
                        textView.text = record.toSerialized()
                        linear.removeAllViews()
                        linear.addView(textView)
                    }
                }
            }
        }

    }

    private fun lowerMargin() : LinearLayout.LayoutParams {
        val params = LinearLayout.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 32
        return params
    }
}
