package com.example.lens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.actions.NoteIntents
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_text_rec.*

class textRec : AppCompatActivity()
{
    private var isText =true
    lateinit var s:String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_rec)

        select.setOnClickListener {
            pickImage()
        }
        call.setOnClickListener {
           var f=false
            for(i in s)
            {
                if((i in '0'..'9') || i=='#' || i=='*')
                    continue
                else {
                    f=true
                    Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                    break
                }
            }
            if(!f)
            {
                checkPermission()
            }
        }
        note.setOnClickListener {
            createNote(textView3.text.toString())
        }
        capture.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }
        clear.setOnClickListener {
            textView3.setText("Select or Capture an Image")
            imageView.setImageBitmap(null)
        }
        search.setOnClickListener {
            searchWeb(textView3.text.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK)
        {
            when(requestCode)
            {
                IMAGE_PICK_CODE->{
                    val bitmap = getImageFromData(data)
                    bitmap.apply {
                        imageView.setImageBitmap(this)
                        if(isText)
                        {
                            if (bitmap != null) {
                                startTextRecognizing(bitmap)
                            }
                        }
                    }
                }
            }
        }
        if(requestCode==101)
        {
            var pic = data?.getParcelableExtra<Bitmap>("data")
            imageView.setImageBitmap(pic)
            if (pic != null) {
                startTextRecognizing(pic)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun pickImage() {
        var intent = Intent().apply{
            action=Intent.ACTION_PICK
            type="image/*"
        }
        startActivityForResult(Intent.createChooser(intent,"Select Image"),IMAGE_PICK_CODE)
    }
    companion object{
        private var IMAGE_PICK_CODE=100
    }


    private fun getImageFromData(data:Intent?):Bitmap?
    {
        val selectedImage = data?.data
        return MediaStore.Images.Media.getBitmap(
            this.contentResolver,
            selectedImage
        )
    }

    private fun startTextRecognizing(bitmap:Bitmap)
    {
        if(imageView.drawable!=null)
        {
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
            detector.processImage(image).addOnSuccessListener {
                firebaseVisionText->
                processTextBlock(firebaseVisionText)
            }
                .addOnFailureListener{
                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                }
        }
        else
        {
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun processTextBlock(result: FirebaseVisionText) {
          s = result.text
        textView3.setText(s)
        }
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                Toast.makeText(this,"Enable Phone Permission",Toast.LENGTH_SHORT).show()

            } else {

                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 42) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                call.isEnabled=false
            }
            return
        }

    }

    private fun callPhone(){
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$s"))
        startActivity(intent)
    }
    private fun searchWeb(query: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, query)
        startActivity(intent)
    }
    private fun createNote(text: String) {
        val intent = Intent(NoteIntents.ACTION_CREATE_NOTE)
        intent.putExtra(NoteIntents.EXTRA_TEXT, text)
        val title: String = "Choose"
// Create intent to show the chooser dialog
        val chooser: Intent = Intent.createChooser(intent, title)

// Verify the original intent will resolve to at least one activity
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        }
    }
}