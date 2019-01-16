package com.tozny.androide3db

import android.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.tozny.e3db.QueryParamsBuilder
import com.tozny.e3db.Record
import java.util.*


class DecryptTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt_text)

        findViewById<EditText>(R.id.decrypt_text_content_type_text).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val decryptButton = findViewById<Button>(R.id.decrypt_text_button)
                decryptButton.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

        findViewById<EditText>(R.id.decrypt_text_uuid_text).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val decryptButton = findViewById<Button>(R.id.decrypt_text_uuid_button)
                decryptButton.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        })

    }

    fun queryText(view: View) {
        val contentType = findViewById<EditText>(R.id.decrypt_text_content_type_text).text

        val client = ClientGenerator.createClient()
        val queryParams = QueryParamsBuilder().setTypes(contentType.toString()).setIncludeData(true).build()
//        val queryResult:
        client.query(queryParams) {result ->
            if (result.isError) {
                Toast.makeText(
                    applicationContext,
                    "Failed to decrypt text, could not query for records of the given content type",
                    Toast.LENGTH_LONG).show()
                Log.e("DecryptTextActivity", "Failed to query for contentType $contentType", result?.asError()?.other())
            } else {
                val linear = findViewById<LinearLayout>(R.id.decrypt_text_query_layout)
                linear.removeAllViews()
                val records = result.asValue().records()
                val deque = ArrayDeque<Record>()
                for (record in records) {
                    deque.push(record)
                }
                val params = lowerMargin()
                deque.forEach { record ->
                    val textView = TextView(applicationContext)
                    textView.setText("${record.meta().recordId()}\t ${record.meta().lastModified()}")
                    textView.layoutParams = params
                    textView.setTag(record.meta().recordId())
                    textView.setOnClickListener {
                            decryptText(view, record.meta().recordId().toString())
                    }
                    linear.addView(textView)
                }
                contentType.clear()
            }
        }
    }

    fun decryptText(view: View) {
        decryptText(view, findViewById<EditText>(R.id.decrypt_text_uuid_text).text.toString())
    }

    fun decryptText(view: View, recordId: String) {
        val linear = findViewById<LinearLayout>(R.id.decrypt_text_query_layout)
        val client = ClientGenerator.createClient()
        try {
            client.read(UUID.fromString(recordId)) {result ->
                if (result.isError) {
                    Toast.makeText(
                        applicationContext,
                        "Failed to decrypt text, could not access or decrypt record",
                        Toast.LENGTH_LONG).show()
                    Log.e("DecryptTextActivity", "Failed to read record $recordId", result?.asError()?.other())
                } else {
                    val textView = TextView(applicationContext)
                    textView.text = result.asValue().data().toString()
                    linear.removeAllViews()
                    linear.addView(textView)
                }
            }

        } catch (e: IllegalArgumentException) {
            Toast.makeText(
                applicationContext,
                "The supplied uuid must be in the form 'E147E215-0F80-4F65-9B0B-D067C0730F06'",
                Toast.LENGTH_LONG).show()
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
