package com.tozny.androide3db

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.tozny.e3db.ConfigStore
import com.tozny.e3db.android.AndroidConfigStore
import com.tozny.e3db.android.KeyAuthentication
import com.tozny.e3db.android.KeyAuthenticator

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

    fun login(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    
    fun startGetPrompt(view: View) {
        Log.e("prompt", "logging prompt ")
        val androidConfigStore = AndroidConfigStore(
            view.context,
            "test",
            KeyAuthentication.withBiometric(),
            KeyAuthenticator.defaultAuthenticator(
                view.context as FragmentActivity,
                BiometricPrompt.PromptInfo.Builder().let { b ->
                    b.setTitle("title").setDescription("this is a description")
                        .setNegativeButtonText("cancel button").setSubtitle("sub title 1").build()
                })
        )
        androidConfigStore.save("sample info", object : ConfigStore.SaveHandler {
            override fun saveConfigDidSucceed() {
                Toast.makeText(
                    applicationContext,
                    "Saved Config",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

            override fun saveConfigDidCancel() {
                Toast.makeText(
                    applicationContext,
                    "Config save cancelled",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

            override fun saveConfigDidFail(e: Throwable?) {
                Toast.makeText(
                    applicationContext,
                    "Config save failed",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        })

    }
}
