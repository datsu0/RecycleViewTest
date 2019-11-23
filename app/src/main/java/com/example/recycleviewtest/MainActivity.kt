package com.example.recycleviewtest

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var dataList = ArrayList<DataModel>()
    private val TAG = "MyActivity"
    val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val reDetail:String
        val reNum:String

        //val recyclerView = recycler_list
        //dataList = createDataList()

        val viewAdapter = ViewAdapter(dataList, object : ViewAdapter.ListListener {
            override fun onClickRow(tappedView: View, rowModel: DataModel) {
                //Toast.makeText(applicationContext, rowModel.title, Toast.LENGTH_LONG).show()
                val intent = Intent(this@MainActivity, SubActivity::class.java)
                intent.putExtra("detail", rowModel.detail)
                intent.putExtra("num",rowModel.num)
                intent.putExtra("unit",rowModel.unit)
                startActivity(intent)
            }
        })


        val getData : CollectionReference = db.collection("users")
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
                            }
                            //dataList.add(data)
                            viewAdapter.add(data)
                            Log.d(TAG, "userList.get(" + i + ").detail " + userList.get(i).detail)
                            Log.d(TAG, "userList.get(" + i + ").title " + userList.get(i).title)
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }


        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_list).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        //fab
        val fabMain:View = findViewById<FloatingActionButton>(R.id.fabMain)
        val fab1:View = findViewById(R.id.fab1)
        val fab2:View = findViewById<FloatingActionButton>(R.id.fab2)
        val fabBack:View = findViewById(R.id.fabBack)
        val text1:TextView = findViewById(R.id.fabtext1)
        val text2:TextView = findViewById(R.id.fabtext2)

        fabMain.visibility = View.VISIBLE
        fab1.visibility = View.GONE
        fab2.visibility = View.GONE
        fabBack.visibility = View.GONE
        text1.visibility = View.GONE
        text2.visibility = View.GONE

        fabMain.setOnClickListener{view ->
            fabMain.visibility = View.GONE
            fab1.visibility = View.VISIBLE
            fab2.visibility = View.VISIBLE
            fabBack.visibility = View.VISIBLE
            text1.visibility = View.VISIBLE
            text2.visibility = View.VISIBLE

            //view.visibility = View.GONE
            fabBack.setOnClickListener { v ->
                fabMain.visibility = View.VISIBLE
                fab1.visibility = View.GONE
                fab2.visibility = View.GONE
                fabBack.visibility = View.GONE
                text1.visibility = View.GONE
                text2.visibility = View.GONE
            }

            fab1.setOnClickListener { v ->
                val unitlist = arrayOf("時間","分","回")
                var unit:String = ""
                val editText = EditText(this@MainActivity)
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("追加")
                    .setMessage("何をしたか入力")
                    .setView(editText)
                    .setPositiveButton("OK") { dialog, which ->
                        if (editText.text.toString() == "" == true) {
                            val toast = Toast.makeText(
                                this@MainActivity,
                                String.format("fill out blank"),
                                Toast.LENGTH_LONG
                            )
                            toast.setGravity(Gravity.TOP, 0, 150)
                            toast.show()
                        }
                        addDataBase(editText,unit)
                    }
                    .show()

                AlertDialog.Builder(this)
                    .setTitle("単位の選択")
                    .setItems(unitlist){dialog, which ->
                        if(which==0){
                            unit = "時間"
                        }else if(which==1){
                            unit = "分"
                        }else{
                            unit = "回"
                        }
                    }
                    .show()

                fabMain.visibility = View.VISIBLE
                fab1.visibility = View.GONE
                fab2.visibility = View.GONE
                fabBack.visibility = View.GONE
                text1.visibility = View.GONE
                text2.visibility = View.GONE
            }

            fab2.setOnClickListener { v ->
                val editText = EditText(this@MainActivity)
                AlertDialog.Builder(this) // FragmentではActivityを取得して生成
                    .setTitle("追加")
                    .setMessage("何をしたか入力")
                    .setView(editText)
                    .setPositiveButton("OK") { dialog, which ->
                        if (editText.text.toString() == "" == true) {
                            val toast = Toast.makeText(
                                this@MainActivity,
                                String.format("fill out blank"),
                                Toast.LENGTH_LONG
                            )
                            toast.setGravity(Gravity.TOP, 0, 150)
                            toast.show()
                        }
                        addDataBase(editText,"円")
                    }
                    .show()

                fabMain.visibility = View.VISIBLE
                fab1.visibility = View.GONE
                fab2.visibility = View.GONE
                fabBack.visibility = View.GONE
                text1.visibility = View.GONE
                text2.visibility = View.GONE
            }
        }
        val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(viewAdapter)
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun addDataBase(edit_text:EditText,unit:String){
        val detail : String = edit_text.text.toString()
            //get from database
        if(!TextUtils.isEmpty(edit_text.text.toString())){
            val data :DataModel = DataModel().also{
                it.detail =detail
                it.title = SimpleDateFormat("yyyy/MM/dd").format(Date())
                it.unit = unit
            }
            dataList.add(data)

            val adapter = ViewAdapter(dataList, object : ViewAdapter.ListListener {
                override fun onClickRow(tappedView: View, rowModel: DataModel) {
                    Toast.makeText(applicationContext, rowModel.title, Toast.LENGTH_LONG).show()
                }
            })
            adapter.notifyDataSetChanged()

//            val recyclerView = recycler_list
//            recyclerView.setHasFixedSize(true)
//            recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
//            recyclerView.adapter = adapter

            val dataMap = hashMapOf(
                "detail" to detail,
                "title" to SimpleDateFormat("yyyy/MM/dd").format(Date()),
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

//                Log.d("read Button","path the on ClickListener")


//                for(key in dataMap.keys){
//                    println("Element at key $key : ${dataMap[key]}")
//                }
        }
    }



//    private fun createDataList(): MutableList<DataModel> {
//        Log.d("Life Cycle", "createDataList")
//        val dataList = mutableListOf<DataModel>()
//        for (i in 0..49) {
//            val data: DataModel = DataModel().also {
//                it.title = "タイトル" + i + "だよ"
//                it.detail = "詳細" + i + "個目だよ"
//            }
//            dataList.add(data)
//        }
//        return dataList
//    }

    //カードのスワイプアクションの定義
    private fun getSwipeToDismissTouchHelper(adapter: RecyclerView.Adapter<HomeViewHolder>) =
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //スワイプ時に実行
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //データリストからスワイプしたデータを削除
                val deleteData = dataList[viewHolder.adapterPosition].detail
                println(deleteData +" find delete position "+viewHolder.adapterPosition)
                dataList.removeAt(viewHolder.adapterPosition)


                db.collection("users")
                    .document(deleteData)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
                //リストからスワイプしたカードを削除
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

            //スワイプした時の背景を設定
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val background = ColorDrawable()
                background.color = Color.parseColor("#f44336")
                if (dX < 0)
                    background.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                else
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )

                background.draw(c)
            }
        })
}
