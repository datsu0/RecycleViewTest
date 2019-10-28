package com.example.recycleviewtest


import android.app.Activity
import android.content.ClipData
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity() {

    var dataList = mutableListOf<DataModel>()
    val db = FirebaseFirestore.getInstance()
    private val TAG = "MyActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Life Cycle", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = recycler_list

        //dataList = createDataList()

        val adapter = ViewAdapter(dataList, object : ViewAdapter.ListListener {
            override fun onClickRow(tappedView: View, rowModel: DataModel) {
                //Toast.makeText(applicationContext, rowModel.title, Toast.LENGTH_LONG).show()
            }
        })


        button.setOnClickListener{
            if(!TextUtils.isEmpty(edit_text.text.toString())){
                val data :DataModel = DataModel().also{
                    it.detail =edit_text.text.toString()
                    it.title = SimpleDateFormat("yyyy/MM/dd").format(Date())
                }
                dataList.add(data)
                adapter.notifyDataSetChanged()

                firebase.firestore.collection('hoge').add({
                        id:1,
                        name:taro
                })
                firebase.firestore.collection('hoge').doc('huga').add({
                        id:1,
                        name:taro
                })

//                val db = FirebaseFirestore.getInstance()
//                val user = hashMapOf(
//                    "first" to "Ada",
//                    "last" to "Lovelace",
//                    "born" to 1815
//                )
//
//// Add a new document with a generated ID
//                db.collection("users")
//                    .add(user)
//                    .addOnSuccessListener { documentReference ->
//                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.w(TAG, "Error adding document", e)
//                    }

            }
        }




        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter
        val swipeToDismissTouchHelper = getSwipeToDismissTouchHelper(adapter)
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView)
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
                dataList.removeAt(viewHolder.adapterPosition)

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
