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
import android.graphics.Color

import android.net.Uri
import android.media.MediaScannerConnection
import android.view.View
import android.view.animation.AnimationUtils
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_sub.*


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
    }


    private fun initViews() {
        val textView:TextView = findViewById<TextView>(R.id.text)
        val textNum: TextView = findViewById(R.id.textnum)
        val textUnit: TextView = findViewById(R.id.textunit)
        var backView: TextView = findViewById(R.id.background)
        val intent = intent
        val detail = intent.getStringExtra("detail")
        val num = intent.getIntExtra("num", 0)
        val unit = intent.getStringExtra("unit")
        textView.setText(detail)
        textNum.setText(num.toString())
        textUnit.setText(unit)

        val numPicker: NumberPicker = findViewById<NumberPicker>(R.id.numPicker0) as NumberPicker
        val numPicker1: NumberPicker = findViewById<NumberPicker>(R.id.numPicker1) as NumberPicker
        val numPicker2: NumberPicker = findViewById<NumberPicker>(R.id.numPicker2) as NumberPicker
        val numPicker3: NumberPicker = findViewById<NumberPicker>(R.id.numPicker3) as NumberPicker
        val numPicker4: NumberPicker = findViewById<NumberPicker>(R.id.numPicker4) as NumberPicker
        val button1: Button = findViewById(R.id.button1)
        val fab: FloatingActionButton = findViewById(R.id.fabMain)
        val pulsFab: FloatingActionButton = findViewById(R.id.pulsFab)
        //val minusFab: FloatingActionButton = findViewById(R.id.minusFab)
        val image: ImageView = findViewById(R.id.imageView)
        val text: TextView = findViewById(R.id.Imagetext)
        var sign = 1
        var flag = 0

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

        numPicker.visibility=View.GONE
        numPicker1.visibility=View.GONE
        numPicker2.visibility=View.GONE
        numPicker3.visibility=View.GONE
        numPicker4.visibility=View.GONE
        button1.visibility=View.GONE
        background.visibility=View.GONE

        pulsFab.setOnClickListener {
            if(flag==0){
                rotateAnimation(pulsFab)
                numPicker.visibility=View.VISIBLE
                numPicker1.visibility=View.VISIBLE
                numPicker2.visibility=View.VISIBLE
                numPicker3.visibility=View.VISIBLE
                numPicker4.visibility=View.VISIBLE
                button1.visibility=View.VISIBLE
                background.visibility=View.VISIBLE
                flag=1
            }else{
                rotateBackAnimation(pulsFab)
                numPicker.visibility=View.GONE
                numPicker1.visibility=View.GONE
                numPicker2.visibility=View.GONE
                numPicker3.visibility=View.GONE
                numPicker4.visibility=View.GONE
                button1.visibility=View.GONE
                background.visibility=View.GONE
                flag=0
            }

            //boundSmallAnimation(minusFab)

            val color:String = "#fff0f5"
            numPicker.setBackgroundColor(Color.parseColor(color))
            numPicker1.setBackgroundColor(Color.parseColor(color))
            numPicker2.setBackgroundColor(Color.parseColor(color))
            numPicker3.setBackgroundColor(Color.parseColor(color))
            numPicker4.setBackgroundColor(Color.parseColor(color))
            background.setBackgroundColor(Color.parseColor(color))
            sign = 1

        }

//        minusFab.setOnClickListener {
//            boundAnimation(minusFab)
//            boundSmallAnimation(pulsFab)
//            numPicker.visibility=View.VISIBLE
//            numPicker1.visibility=View.VISIBLE
//            numPicker2.visibility=View.VISIBLE
//            numPicker3.visibility=View.VISIBLE
//            numPicker4.visibility=View.VISIBLE
//            button1.visibility=View.VISIBLE
//            background.visibility=View.VISIBLE
//            val color:String = "#e0ffff"
//            numPicker.setBackgroundColor(Color.parseColor(color))
//            numPicker1.setBackgroundColor(Color.parseColor(color))
//            numPicker2.setBackgroundColor(Color.parseColor(color))
//            numPicker3.setBackgroundColor(Color.parseColor(color))
//            numPicker4.setBackgroundColor(Color.parseColor(color))
//            background.setBackgroundColor(Color.parseColor(color))
//            sign = -1
//        }

        button1.setOnClickListener {
//            textView1.setText(numPicker.getValue() + "");
//                            figures[0] = String.valueOf(numPicker.getValue());
//                            figures[1] = String.valueOf(numPicker1.getValue());
//                            figures[2] = String.valueOf(numPicker2.getValue());
//                            figures[3] = String.valueOf(numPicker3.getValue());
//                            figures[4] = String.valueOf(numPicker4.getValue());
//                            String str = String.format("%s%s%s.%s%s",
//                                    figures[0], figures[1], figures[2], figures[3], figures[4]);

            val getNum = numPicker.value + numPicker1.value * 10 + numPicker2.value * 100 +
                    numPicker3.value * 1000 + numPicker4.value * 10000


            val detail = intent.getStringExtra("detail")
            val num = intent.getIntExtra("num", 0)
            val unit = intent.getStringExtra("unit")
            val show = num + (getNum*sign)
            textNum.setText(show.toString())
            rewriteDataBase(detail,num+(getNum*sign),unit)

//            val sp = getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
//            val e = sp.edit()
//            e.putInt("num" + data!!, num + getNum)
//            e.commit()

//            val intent1 = Intent(this@SubActivity, MainActivity::class.java)
//            startActivity(intent1)
//            finish()
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

    private fun rotateAnimation(view: View){
        //val animation = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f)
        val animation = SpringAnimation(view, DynamicAnimation.ROTATION,45f)
        animation.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_HIGH
        }

        animation.setStartVelocity(0.01f)
        animation.start()
    }

    private fun rotateBackAnimation(view: View){
        //val animation = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f)
        val animation = SpringAnimation(view, DynamicAnimation.ROTATION,0f)
        animation.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_HIGH
        }

        animation.setStartVelocity(0.01f)
        animation.start()
    }

    private fun boundAnimation(view: View){
        //val animation = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f)
        val animation = SpringAnimation(view, DynamicAnimation.SCALE_X,1.2f)
        val animation2 = SpringAnimation(view, DynamicAnimation.SCALE_Y,1.2f)
        animation.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_HIGH
        }
        animation2.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_HIGH
        }
        animation.setStartVelocity(0.01f)
        animation.start()
        animation2.setStartVelocity(0.01f)
        animation2.start()
    }

    private fun boundSmallAnimation(view: View){
        //val animation = SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, 0f)
        val animation = SpringAnimation(view, DynamicAnimation.SCALE_X,0.8f)
        val animation2 = SpringAnimation(view, DynamicAnimation.SCALE_Y,0.8f)
        animation.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_HIGH
        }
        animation2.spring.apply {
            dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            stiffness = SpringForce.STIFFNESS_HIGH
        }
        animation.setStartVelocity(0.01f)
        animation.start()
        animation2.setStartVelocity(0.01f)
        animation2.start()
    }

    private fun rewriteDataBase(detail:String,num:Int,unit:String){
        Toast.makeText(this@SubActivity,detail+unit,Toast.LENGTH_SHORT).show();
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
