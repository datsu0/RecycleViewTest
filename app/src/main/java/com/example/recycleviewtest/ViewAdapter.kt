package com.example.recycleviewtest

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class ViewAdapter(private val list: ArrayList<DataModel>, private val listener: ListListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        Log.d("Life Cycle", "onCreateViewHolder")
        val rowView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return HomeViewHolder(rowView)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        Log.d("Life Cycle", "onBindViewHolder")
        holder.titleView.text = list[position].title
        holder.detailView.text = list[position].detail
        holder.itemView.setOnClickListener {
            listener.onClickRow(it, list[position])
        }

    }

    fun add(data:DataModel){
        list.add(data)
        notifyDataSetChanged()
    }

    fun Delete(position: Int):DataModel{
        list.removeAt(position)
        notifyItemChanged(position)
        notifyDataSetChanged()
        return list[position]
    }

    override fun getItemCount(): Int {
        Log.d("Life Cycle", "getItemCount")
        return list.size
    }

    interface ListListener {
        fun onClickRow(tappedView: View, rowModel: DataModel)
    }

}