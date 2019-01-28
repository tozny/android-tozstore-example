package com.tozny.androide3db

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.tozny.e3db.ClientBuilder
import com.tozny.e3db.Config
import com.tozny.e3db.ConfigStore
import com.tozny.e3db.android.AndroidConfigStore

class ClientManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_manager)
    }

}
