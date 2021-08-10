package com.example.lens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnimglabel.setOnClickListener{
            var intent = Intent(this,imglabel::class.java)
            startActivity(intent)
        }
        btntxtrec.setOnClickListener{
            var intent = Intent(this,textRec::class.java)
            startActivity(intent)
        }
        qr.setOnClickListener {
            var intent = Intent(this,QR::class.java)
            startActivity(intent)
        }
        mic.setOnClickListener {
            var intent = Intent(this,speechToText::class.java)
            startActivity(intent)
        }
    }
}