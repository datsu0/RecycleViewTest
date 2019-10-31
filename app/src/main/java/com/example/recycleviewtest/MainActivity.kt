package com.example.recycleviewtest

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var dataList = mutableListOf<DataModel>()
    private val TAG = "MyActivity"
    var groupIdList = listOf<DataModel>()
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Life Cycle", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = recycler_list

        //dataList = createDataList()

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
                        for(i in 1 until  userList.size){
                            val data : DataModel = DataModel().also {
                                it.detail = userList.get(i).detail
                                it.title = userList.get(i).title
                            }
                            dataList.add(data)
                            Log.d(TAG, "userList.get(" + i + ").detail " + userList.get(i).detail)
                            Log.d(TAG, "userList.get(" + i + ").title " + userList.get(i).title)
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
//            .addOnSuccessListener { documentSnapshot ->
//                for(begin in documentSnapshot.documents){
//                    val hashmap = begin.data
//                    hashmap?.put("id",begin.id)
//                    val Data = Gson().toJson(hashmap)
//                    val docsData = Gson().fromJson<String>(Data,String::class.java)
//                    Log.e("docsData",docsData)
//                }
//            }


//            .addOnSuccessListener { documentSnapshot ->
//                val dataText = documentSnapshot.toObjects(DataModel::class.java)
//                groupIdList=dataText.toList()
//                if(groupIdList.isEmpty()){
//                    for(i in 1..groupIdList.size){
//                        dataList.add(groupIdList.get(i))
//                    }
//                }
//                for (document in documentSnapshot) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnSuccessListener {
//                    result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//
//
//
////                if(it.isEmpty)return@addOnSuccessListener
////                val dataDocumentList = it.documents
////                dataDocumentList.forEach{
////                    groupIdList.add(it.id)
////                }
//
//            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }

        val adapter = ViewAdapter(dataList, object : ViewAdapter.ListListener {
            override fun onClickRow(tappedView: View, rowModel: DataModel) {
                //Toast.makeText(applicationContext, rowModel.title, Toast.LENGTH_LONG).show()
            }
        })

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter



        //button
        button.setOnClickListener{
            val detail : String = edit_text.text.toString()
            if(!TextUtils.isEmpty(edit_text.text.toString())){
                val data :DataModel = DataModel().also{
                    it.detail =detail
                    it.title = SimpleDateFormat("yyyy/MM/dd").format(Date())
                }

                dataList.add(data)
                recyclerView.setHasFixedSize(true)
                recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(applicationContext)
                recyclerView.adapter = adapter

                val dataMap = hashMapOf(
                    "detail" to detail,
                    "title" to SimpleDateFormat("yyyy/MM/dd").format(Date())
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
