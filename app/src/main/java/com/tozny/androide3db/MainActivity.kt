package com.tozny.androide3db

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun startEncryptText(view: View) {
        val intent = Intent(this, EncryptTextActivity::class.java)
        startActivity(intent)
    }

    fun startDecryptText(view: View) {
        val intent = Intent(this, DecryptTextActivity::class.java)
        startActivity(intent)
    }

    fun startEncryptPhoto(view: View) {
        val intent = Intent(this, EncryptPhotoActivity::class.java)
        startActivity(intent)
    }
}
