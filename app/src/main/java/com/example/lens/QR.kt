package com.example.lens

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_qr.*

class QR : AppCompatActivity() {

    private lateinit var codescanner: CodeScanner
    lateinit var s:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr)
        startScanning()

        qrsearch.setOnClickListener {
            searchWeb(qrtext.text.toString())
        }
        qrpay.setOnClickListener {

        }
        qrbuy.setOnClickListener {
            val intent = Intent(this,web::class.java)
            startActivity(intent)
        }
    }

    private fun searchWeb(query: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, query)
        startActivity(intent)
    }

    private fun startScanning() {
        val scannerView = scanner_view
        codescanner = CodeScanner(this,scannerView)
        codescanner.camera = CodeScanner.CAMERA_BACK
        codescanner.formats=CodeScanner.ALL_FORMATS

        codescanner.autoFocusMode = AutoFocusMode.SAFE
        codescanner.scanMode = ScanMode.SINGLE
        codescanner.isAutoFocusEnabled = true
        codescanner.isFlashEnabled=false

        codescanner.decodeCallback = DecodeCallback {
           runOnUiThread {
               s = it.text
               qrtext.setText(s)
           }
        }
        codescanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this,"${it.message}",Toast.LENGTH_SHORT).show()
            }
        }
        scannerView.setOnClickListener {
            codescanner.startPreview()
        }

    }

    override fun onResume() {
        super.onResume()
        if(::codescanner.isInitialized){
            codescanner?.startPreview()
        }
    }

    override fun onPause() {

        if(::codescanner.isInitialized){
            codescanner?.releaseResources()
        }
        super.onPause()
    }
}