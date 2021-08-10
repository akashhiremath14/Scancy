 @file:Suppress("DEPRECATION")

package com.example.lens
import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.lens.ml.Imagelabel
import kotlinx.android.synthetic.main.activity_imglabel.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class imglabel : AppCompatActivity()
{
    lateinit var bitmap : Bitmap
    lateinit var imgview: ImageView
    lateinit var s:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imglabel)
        capture.isEnabled=false
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 111)
        }
        else {
            capture.isEnabled = true
        }
        capture.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i, 101)
        }

        imgview = imageView
        val filename = "label.txt"
        val inputString = application.assets.open(filename).bufferedReader().use { it.readText() }
        var townList =inputString.split("\n")
        select.setOnClickListener(View.OnClickListener {

            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }
        )
        clear.setOnClickListener {
            imageView.setImageDrawable(null)
            textView.setText("Select or Capture an Image")
        }
        search.setOnClickListener {
            searchWeb(textView.text.toString())
            s=textView.text.toString()
        }
     
        predict.setOnClickListener(View.OnClickListener
        {
            var resized: Bitmap = createScaledBitmap(bitmap, 224, 224, true)
            val model = Imagelabel.newInstance(this)

// Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.UINT8)
            var tbuffer = TensorImage.fromBitmap(resized)
            var byteBuffer = tbuffer.buffer
            inputFeature0.loadBuffer(byteBuffer)

// Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            var max = getMax(outputFeature0.floatArray)
            textView.text = townList[max]
// Releases model resources if no longer used.
            model.close()
        }
        )
      Buy.setOnClickListener {
          val intent = Intent(this,web::class.java)
          startActivity(intent)
      }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100) {
            imgview.setImageURI(data?.data)
            var uri: Uri? = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        }
        if(requestCode==101)
        {
            var pic = data?.getParcelableExtra<Bitmap>("data")
            imageView.setImageBitmap(pic)
            if (pic != null) {
                bitmap = pic
            }
        }
    }
    fun getMax(arr: FloatArray) : Int
    {
        var ind =0
        var min =0.0f
        for(i in 0..1000)
        {
            if(arr[i]>min)
            {
                ind=i
                min=arr[i]
            }
        }
        return ind
    }

    fun searchWeb(query: String) {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.putExtra(SearchManager.QUERY, query)

        startActivity(intent)
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==111 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            capture.isEnabled=true
        }
    }
}