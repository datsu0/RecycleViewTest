package com.example.recycleviewtest

import android.content.ClipData
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.activity_sub.view.*


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
        holder.unitView.text = list[position].unit
        holder.numView.text = list[position].num.toString()
        if(list[position].unit=="円"){
            holder.image!!.setImageResource(R.drawable.ic_yen_sign_solid)
            holder.card.setCardBackgroundColor(Color.parseColor("R.color.cardViewBule"))
        }else if(list[position].unit=="回"){
            holder.image!!.setImageResource(R.drawable.ic_countbyline)
            holder.card.setCardBackgroundColor(Color.parseColor("#6CCA60"))
        }else{
            holder.image!!.setImageResource(R.drawable.ic_clock_regular)
            holder.card.setCardBackgroundColor(Color.parseColor("#828DFF"))
        }


        holder.itemView.setOnClickListener {
            listener.onClickRow(it, list[position])
        }

    }

    fun moveItem(from: Int, to: Int) {
        val fromEmoji = list[from]
        list.removeAt(from)
        if (to < from) {
            list.add(to, fromEmoji)
        } else {
            list.add(to - 1, fromEmoji)
        }
    }

    fun add(data:DataModel){
        list.add(data)
        notifyDataSetChanged()
    }

    fun delete(position: Int):DataModel{
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