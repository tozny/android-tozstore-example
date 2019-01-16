package com.tozny.androide3db

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.tozny.e3db.RecordData
import com.tozny.e3db.ResultHandler
import java.time.Instant

class EncryptTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypt_text)
    }

    fun encryptAndUploadText(view: View) {
        val textToEncrypt = findViewById<EditText>(R.id.encrypt_text_to_encrypt_text).text
        val contentType = findViewById<EditText>(R.id.encrypt_text_content_type_text).text

        val client = ClientGenerator.createClient()

        val data = HashMap<String, String>()
        data.put("info", textToEncrypt.toString())
        val meta = HashMap<String, String>()
        client.write(contentType.toString(), RecordData(data), meta) {result ->
            if (result.isError) {
                Toast.makeText(applicationContext, "Failed to encrypt and upload please try again", Toast.LENGTH_LONG).show()
                Log.e("EncryptTextActivity", "Failed to encrypt and upload", result?.asError()?.other())
            } else {
                Toast.makeText(applicationContext, "Data encrypted and uploaded", Toast.LENGTH_LONG).show()
                textToEncrypt.clear()
                contentType.clear()
            }
        }
    }
}