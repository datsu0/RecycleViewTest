package com.example.recycleviewtest

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView

class HomeViewHolder(itemView: View) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    val titleView: TextView = itemView.findViewById(R.id.row_title)
    val detailView: TextView = itemView.findViewById(R.id.row_detail)
    val numView: TextView = itemView.findViewById(R.id.row_num)
    val unitView: TextView = itemView.findViewById(R.id.row_unit)
    var image: ImageView? = itemView.findViewById(R.id.imageView)
}