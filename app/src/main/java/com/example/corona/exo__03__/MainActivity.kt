package com.example.corona.exo__03__

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start_adhan.setOnClickListener {
            startService(Intent(baseContext,Adhan::class.java))
        }
        stop_adhan.setOnClickListener {
            stopService(Intent(baseContext,Adhan::class.java))
        }
    }


}
