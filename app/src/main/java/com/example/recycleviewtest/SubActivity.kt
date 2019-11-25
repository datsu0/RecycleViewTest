package com.example.recycleviewtest

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.provider.MediaStore
import android.content.ContentValues

import android.net.Uri
import android.media.MediaScannerConnection
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.HandlerCompat.postDelayed
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions


class SubActivity : AppCompatActivity() {
    private val figures = arrayOfNulls<String>(5)
    val db = FirebaseFirestore.getInstance()
    private val TAG = "SubActivity"
    private val REQUEST_CHOOSER = 1000
    private var m_uri: Uri? = null
    private var imageView:ImageView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        initViews()
        var textView:TextView = findViewById<TextView>(R.id.text)
        val intent = intent
        val data = intent.getStringExtra("detail")
        textView.setText("タイトル　：　"+data)
    }


    private fun initViews() {

        var numPicker: NumberPicker = findViewById<NumberPicker>(R.id.numPicker0) as NumberPicker
        var numPicker1: NumberPicker = findViewById<NumberPicker>(R.id.numPicker1) as NumberPicker
        var numPicker2: NumberPicker = findViewById<NumberPicker>(R.id.numPicker2) as NumberPicker
        var numPicker3: NumberPicker = findViewById<NumberPicker>(R.id.numPicker3) as NumberPicker
        var numPicker4: NumberPicker = findViewById<NumberPicker>(R.id.numPicker4) as NumberPicker
        var button1: Button = findViewById(R.id.button1)
        var fab: FloatingActionButton = findViewById(R.id.fabMain)
        var image: ImageView = findViewById(R.id.imageView)
        var text: TextView = findViewById(R.id.Imagetext)
        var textView1: TextView? = null
        numPicker.maxValue = 9
        numPicker.minValue = 0
        numPicker1.maxValue = 9
        numPicker1.minValue = 0
        numPicker2.maxValue = 9
        numPicker2.minValue = 0
        numPicker3.maxValue = 9
        numPicker3.minValue = 0
        numPicker4.maxValue = 9
        numPicker4.minValue = 0


        button1.setOnClickListener {
            //textView1.setText(numPicker.getValue() + "");
            //                figures[0] = String.valueOf(numPicker.getValue());
            //                figures[1] = String.valueOf(numPicker1.getValue());
            //                figures[2] = String.valueOf(numPicker2.getValue());
            //                figures[3] = String.valueOf(numPicker3.getValue());
            //                figures[4] = String.valueOf(numPicker4.getValue());
            //                String str = String.format("%s%s%s.%s%s",
            //                        figures[0], figures[1], figures[2], figures[3], figures[4]);

            val getNum = numPicker.value + numPicker1.value * 10 + numPicker2.value * 100 +
                    numPicker3.value * 1000 + numPicker4.value * 10000


            val detail = intent.getStringExtra("detail")
            val num = intent.getIntExtra("num", 0)
            val unit = intent.getStringExtra("unit")
            rewriteDataBase(detail,num+getNum,unit)
            //Toast.makeText(SubActivity.this,data,Toast.LENGTH_SHORT).show();
//            val sp = getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
//            val e = sp.edit()
//            e.putInt("num" + data!!, num + getNum)
//            e.commit()

            val intent1 = Intent(this@SubActivity, MainActivity::class.java)
            startActivity(intent1)
            finish()
        }

        fab.setOnClickListener {
            val intent = Intent(this@SubActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        image.setOnClickListener { v->
            //Toast.makeText(this@SubActivity,"image",Toast.LENGTH_SHORT).show();
            //カメラの起動Intentの用意
            val photoName = System.currentTimeMillis().toString() + ".jpg"
            val contentValues = ContentValues()
            contentValues.put(MediaStore.Images.Media.TITLE, photoName)
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            m_uri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri)
            val intentGallery: Intent
            intentGallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intentGallery.addCategory(Intent.CATEGORY_OPENABLE)
            intentGallery.type = "image/jpeg"
            val intent = Intent.createChooser(intentCamera, "画像の選択")
            intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentGallery))
            startActivityForResult(intent, REQUEST_CHOOSER)
        }

        text.setOnClickListener { v ->
            //Toast.makeText(this@SubActivity,"image",Toast.LENGTH_SHORT).show();
        }

    }

    private fun rewriteDataBase(detail:String,num:Int,unit:String){

        val dataMap = hashMapOf(
            "detail" to detail,
            "title" to SimpleDateFormat("yyyy/MM/dd").format(Date()),
            "num" to num,
            "unit" to unit
        )

        db.collection("users")
            //.add(dataMap)
            .document(detail)
            .set(dataMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHOOSER) {

            if (resultCode != Activity.RESULT_OK) {
                // キャンセル時
                return
            }

            val resultUri = (if (data != null) data.data else m_uri) ?: return

            MediaScannerConnection.scanFile(
                this,
                arrayOf<String?>(resultUri.path),
                arrayOf("image/jpeg"), null
            )

            // 画像を設定
            val imageView = findViewById(R.id.imageView) as ImageView
            imageView.setImageURI(resultUri)
            Glide.with(imageView)
                .load(resultUri)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(360)))
                .into(imageView)

            this.imageView = imageView
        }
    }
}
