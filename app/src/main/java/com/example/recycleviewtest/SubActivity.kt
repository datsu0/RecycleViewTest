package com.example.recycleviewtest

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.media.SoundPool
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.firestore.CollectionReference
import com.jjoe64.graphview.GraphView
import kotlinx.android.synthetic.main.activity_sub.*
import kotlin.collections.ArrayList


class SubActivity : AppCompatActivity() {
    private val figures = arrayOfNulls<String>(5)
    var graphDataList = ArrayList<DataModel>()
    var maxNum:Float = 0f
    val db = FirebaseFirestore.getInstance()
    private val TAG = "SubActivity"
    private val REQUEST_CHOOSER = 1000
    private var m_uri: Uri? = null
    private lateinit var soundPool: SoundPool
    private var soundButton = 0
    private var soundDesition = 0
    private var soundBack = 0
    private var maxCount:Int = 0


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(1)
            .build()

        soundButton = soundPool.load(this, R.raw.decision3, 1)
        soundDesition = soundPool.load(this,R.raw.decision1,1)
        soundBack = soundPool.load(this,R.raw.cancel2,1)

        val toolbar:Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("入力ページ")
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        } ?: IllegalAccessException("Toolbar cannot be null")



        val detail:String = intent.getStringExtra("detail")
        val chart = bar_chart


        val getData : CollectionReference = db.collection(detail)
        getData
            .get()
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.toObjects(DataModel::class.java) != null) {
                        val userList = document.toObjects(DataModel::class.java)
                        Log.d(TAG, "getDataAll")
                        Log.d(TAG, "userList.size " + userList.size)
                        for(i in 0 until  userList.size){
                            val data : DataModel = DataModel().also {
                                it.detail = userList.get(i).detail
                                it.title = userList.get(i).title
                                it.num = userList.get(i).num
                                it.unit = userList.get(i).unit
                                it.image = userList.get(i).image
                                it.count = userList.get(i).count
                            }
                            graphDataList.add(data)
                            //define max value y for graph
                            if(maxNum<data.num){
                                maxNum=data.num+0f
                            }

                            if(maxCount<data.count){
                                maxCount=data.count
                            }

                            Log.d(TAG, "graphDataList.get(" + i + ").detail " + userList.get(i).detail)
                            Log.d(TAG, "graphDataList.get(" + i + ").title " + userList.get(i).title)
                            Log.d(TAG, "graphDataList.get(" + i + ").num " + userList.get(i).num)
                            Log.d(TAG, "graphDataList.size = " + graphDataList.size)
                        }
                        setBar(chart,graphDataList,maxNum)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        initViews(chart)

    }

    override fun onBackPressed() {
        // Do something
        soundPool.play(soundBack, 1.0f, 1.0f, 0, 0, 1.0f)
        val intent = Intent(this@SubActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish()
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initViews(chart: BarChart) {
        val textView:TextView = findViewById<TextView>(R.id.text)
        val textNum: TextView = findViewById(R.id.textnum)
        val textUnit: TextView = findViewById(R.id.textunit)
        var backView: TextView = findViewById(R.id.background)
        var numBackView: TextView = findViewById(R.id.numBackGround)
        val imageView: ImageView = findViewById(R.id.imageView)
        val intent = intent
        val detail = intent.getStringExtra("detail")
        val num = intent.getIntExtra("num", 0)
        val unit = intent.getStringExtra("unit")
        textView.setText(detail)
        textNum.setText(num.toString())
        textUnit.setText(unit)
        var show = 0


        if(unit=="円"){
            imageView!!.setImageResource(R.drawable.ic_yen_sign_solid)
            numBackView.setBackgroundColor(Color.parseColor("#F0A03A"))

        }else{
            imageView!!.setImageResource(R.drawable.ic_clock_regular)
            numBackView.setBackgroundColor(Color.parseColor("#828DFF"))
        }

        val numPicker: NumberPicker = findViewById<NumberPicker>(R.id.numPicker0) as NumberPicker
        val numPicker1: NumberPicker = findViewById<NumberPicker>(R.id.numPicker1) as NumberPicker
        val numPicker2: NumberPicker = findViewById<NumberPicker>(R.id.numPicker2) as NumberPicker
        val numPicker3: NumberPicker = findViewById<NumberPicker>(R.id.numPicker3) as NumberPicker
        val numPicker4: NumberPicker = findViewById<NumberPicker>(R.id.numPicker4) as NumberPicker
        val button1: Button = findViewById(R.id.button1)
        //val fab: FloatingActionButton = findViewById(R.id.fabMain)
        val pulsFab: FloatingActionButton = findViewById(R.id.pulsFab)
        //val minusFab: FloatingActionButton = findViewById(R.id.minusFab)
//        val image: ImageView = findViewById(R.id.imageView)
//        val text: TextView = findViewById(R.id.Imagetext)
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
            soundPool.play(soundButton, 1.0f, 1.0f, 0, 0, 1.0f)
            if(flag==0){
                boundSmallAnimation(pulsFab)
                numPicker.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 100)
                }
                numPicker1.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 100)
                }
                numPicker2.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 100)
                }
                numPicker3.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 100)
                }
                numPicker4.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 100)
                }
                button1.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }
                background.run{
                    visibility = View.VISIBLE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.alpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }

                flag=1
            }else{
                boundAnimation(pulsFab)
                numPicker.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }
                numPicker1.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }
                numPicker2.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }
                numPicker3.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }
                numPicker4.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 0)
                }
                button1.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 200)
                }
                background.run{
                    visibility = View.GONE
                    val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                    postDelayed({
                        startAnimation(animation)
                    }, 200)
                }
                flag=0
            }

            //boundSmallAnimation(minusFab)
            sign = 1

        }


        button1.setOnClickListener {
            soundPool.play(soundDesition, 1.0f, 1.0f, 0, 0, 1.0f)


            //chart.visibility = GraphView.GONE

            val getNum = numPicker.value + numPicker1.value * 10 + numPicker2.value * 100 +
                    numPicker3.value * 1000 + numPicker4.value * 10000


            val detail = intent.getStringExtra("detail")
            val num = intent.getIntExtra("num", 0)
            val unit = intent.getStringExtra("unit")
            show = getNum + show
            textNum.setText((show+num).toString())
            rewriteDataBase(detail,num+show,unit)
            maxCount++
            addDataBase(detail,getNum,unit,maxCount)
            upDateGraph(chart,getNum,graphDataList,maxCount,maxNum)


            boundAnimation(pulsFab)
            numPicker.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 0)
            }
            numPicker1.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 0)
            }
            numPicker2.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 0)
            }
            numPicker3.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 0)
            }
            numPicker4.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 0)
            }
            button1.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 200)
            }
            background.run{
                visibility = View.GONE
                val animation = AnimationUtils.loadAnimation(context, R.anim.backalpha)
                postDelayed({
                    startAnimation(animation)
                }, 200)
            }
            flag=0

//            val sp = getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
//            val e = sp.edit()
//            e.putInt("num" + data!!, num + getNum)
//            e.commit()

//            val intent1 = Intent(this@SubActivity, MainActivity::class.java)
//            startActivity(intent1)
//            finish()
        }

//        fab.setOnClickListener {
//            val intent = Intent(this@SubActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

//        image.setOnClickListener { v->
//            //Toast.makeText(this@SubActivity,"image",Toast.LENGTH_SHORT).show();
//            //カメラの起動Intentの用意
//            val photoName = System.currentTimeMillis().toString() + ".jpg"
//            val contentValues = ContentValues()
//            contentValues.put(MediaStore.Images.Media.TITLE, photoName)
//            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            m_uri = contentResolver
//                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//            val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri)
//            val intentGallery: Intent
//            intentGallery = Intent(Intent.ACTION_OPEN_DOCUMENT)
//            intentGallery.addCategory(Intent.CATEGORY_OPENABLE)
//            intentGallery.type = "image/jpeg"
//            val intent = Intent.createChooser(intentCamera, "画像の選択")
//            intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intentGallery))
//            startActivityForResult(intent, REQUEST_CHOOSER)
//        }

//        text.setOnClickListener { v ->
//            Toast.makeText(this@SubActivity,"image",Toast.LENGTH_SHORT).show();
//        }

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
        val animation = SpringAnimation(view, DynamicAnimation.SCALE_X,1.0f)
        val animation2 = SpringAnimation(view, DynamicAnimation.SCALE_Y,1.0f)
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

    private fun addDataBase(detail:String,num:Int,unit:String,maxCount:Int){
        val dataMap = hashMapOf(
            "detail" to detail,
            "title" to SimpleDateFormat("yyyy/MM/dd").format(Date()),
            "num" to num,
            "unit" to unit,
            "count" to maxCount
        )

        db.collection(detail)
            .add(dataMap)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun upDateGraph(chart: BarChart,addData:Int,graphDataList: ArrayList<DataModel>,maxCount:Int,maxNum:Float){
        val data : DataModel = DataModel().also {
            it.detail = "tmp"
            it.title = "tmp"
            it.num = addData
            it.unit = "tmp"
            it.image = null
            it.count = maxCount
        }
        if(maxNum<addData){
            graphDataList.add(data)
            setBar(chart,graphDataList,addData+0f)
        }else{
            graphDataList.add(data)
            setBar(chart,graphDataList,maxNum)
        }

    }

    private fun setBar(chart:BarChart,graphDataList: ArrayList<DataModel>,setNum:Float){
        //表示データ取得
        Log.d(TAG, "pre send graphDataList.size = " + graphDataList.size)
        chart.data = BarData(getBarData(graphDataList))
        chart.setBackgroundColor(Color.parseColor("#ffffff"))
        var setSize : Int =graphDataList.size
        if(setSize>5){
            setSize=5
        }

        //Y軸(左)の設定
        chart.axisLeft.apply {
            axisMinimum = 0f
            axisMaximum = setNum
            labelCount = setSize
            setDrawTopYLabelEntry(true)
            setValueFormatter { value, axis -> "" + value.toInt()}
        }

        //Y軸(右)の設定
        chart.axisRight.apply {
            setDrawLabels(false)
            setDrawGridLines(false)
            setDrawZeroLine(false)
            setDrawTopYLabelEntry(true)
        }

        //X軸の設定
        val labelsName = arrayListOf<String>("","1つ前","2つ前","3つ前","4つ前","5つ前") //最初の””は原点の値
        var labels = arrayListOf<String>("")
        if(setSize!=0){
            for (i in 0..setSize-1){
                labels.add(labelsName[setSize-i])
            }
        }

        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            labelCount = setSize //表示させるラベル数
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            setDrawGridLines(false)
            setDrawAxisLine(true)
        }

        //グラフ上の表示
        chart.apply {
            setDrawValueAboveBar(true)
            description.isEnabled = false
            isClickable = false
            legend.isEnabled = false //凡例
            setScaleEnabled(false)
            animateY(1200, Easing.EasingOption.Linear)
        }
    }


    private fun getBarData(list:ArrayList<DataModel>): ArrayList<IBarDataSet> {
        //表示させるデータ
//        Log.d(TAG, "entries!!!!" + list.size)
//        Log.d(TAG, "entries!!!!" + list.get(0).num)
//        Log.d(TAG, "entries!!!!" + list.get(1).num)
        var setSize = list.size

        Collections.sort(list,PersonComparator())
        for (i in 0..list.size-1){
                    Log.d(TAG, "is sorted list ?" + list.get(i).count)
        }

        var entries = ArrayList<BarEntry>()
        if (list.size != 0 && list.size <= 5) {
            for (i in 0..setSize - 1) {
                val num: Float = list.get(i).num + 0.0f
                entries.add(BarEntry(i + 1f, num))
            }
        }else if(list.size>5){
            for (i in 1..5) {
                val num: Float = list.get(list.size-i).num + 0.0f
                entries.add(BarEntry( 6f-i, num))
            }
        }else {
            entries.add(BarEntry( 1f, 0f))
        }

//        val entries = ArrayList<BarEntry>().apply {
//            add(BarEntry(1f, graphDataList.get(pos-4).num+0f))
//            add(BarEntry(2f, graphDataList.get(pos-3).num+0f))
//            add(BarEntry(3f, graphDataList.get(pos-2).num+0f))
//            add(BarEntry(4f, graphDataList.get(pos-1).num+0f))
//            add(BarEntry(5f, graphDataList.get(pos).num+0f))
//        }

        val dataSet = BarDataSet(entries, "bar").apply {
            //整数で表示
            valueFormatter = IValueFormatter { value, _, _, _ -> "" + value.toInt() }
            //ハイライトさせない
            isHighlightEnabled = false
            //Barの色をセット
            setColors(intArrayOf(R.color.cardViewBule, R.color.cardViewOrenge, R.color.cardViewBule), this@SubActivity)
        }

        val bars = ArrayList<IBarDataSet>()
        bars.add(dataSet)
        return bars
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CHOOSER) {
//
//            if (resultCode != Activity.RESULT_OK) {
//                // キャンセル時
//                return
//            }
//
//            val resultUri = (if (data != null) data.data else m_uri) ?: return
//
//            MediaScannerConnection.scanFile(
//                this,
//                arrayOf<String?>(resultUri.path),
//                arrayOf("image/jpeg"), null
//            )
//
//            // 画像を設定
//            val imageView = findViewById(R.id.imageView) as ImageView
//            imageView.setImageURI(resultUri)
//            Glide.with(imageView)
//                .load(resultUri)
//                .apply(RequestOptions.bitmapTransform(RoundedCorners(360)))
//                .into(imageView)
//
//            this.imageView = imageView
//        }
//    }
}

class PersonComparator : Comparator<DataModel> {

    override fun compare(p1: DataModel, p2: DataModel): Int {
        return if (p1.count < p2.count) -1 else 1
    }
}
