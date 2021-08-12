package com.tozny.androide3db

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tozny.e3db.CredentialType
import com.tozny.e3db.LoginAction
import com.tozny.e3db.LoginActionHandler
import com.tozny.e3db.Realm
import java.net.URI

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun beginLogin(view: View) {
        Toast.makeText(applicationContext, "Testing", Toast.LENGTH_LONG).show()
        val realmName = findViewById<EditText>(R.id.login_realm_name).text.toString()
        val username= findViewById<EditText>(R.id.login_username).text.toString()
        val password = findViewById<EditText>(R.id.login_password).text.toString()
        val realm = Realm(realmName, "account", URI("https://id.tozny.com/elidartrealm/recover"), URI("https://api.e3db.com"), null)
        realm.login(username, password, actionHandler = null) { result ->
            result?.let {
                when (it.isError) {
                    true -> {
                        Toast.makeText(applicationContext, "Failed to login", Toast.LENGTH_LONG).show()
                        Log.e("login", "failed to login", it.asError().other())

                    }
                    false -> {
                        Toast.makeText(applicationContext, "Login Succeeded", Toast.LENGTH_LONG).show()
                        it.asValue()!!
                    }
                }
            }
        }

    }

}