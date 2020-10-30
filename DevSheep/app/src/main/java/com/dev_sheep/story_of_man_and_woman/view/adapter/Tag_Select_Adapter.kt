package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import kotlinx.android.synthetic.main.adapter_tag_select.view.*

class Tag_Select_Adapter(
    private val list: List<Tag>,
    private val context: Context,
    private val onTagCheckedSeq : OnTagCheckedSeq
) : RecyclerView.Adapter<Tag_Select_Adapter.ViewHolder>() {

    private var lastCheckedPosition = -1
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bindView(item: Tag,position: Int) {
            itemView.tag_seq.text = ""+item.tag_seq
            itemView.select_tag_name.text = "  # "+item.tag_name+"   "


//1 . # 모든사연
//2 . # 잡담
//3 . # 남과 여
//4 . # 고민/질문
//5 . # 남자 이야기
//6 . # 여자 이야기
//7 . # Yes or No

//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_tag_select, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val tag_seq = list[position].tag_seq
        val tag_name = list[position].tag_name
        if(tag_seq == lastCheckedPosition){
            holder.itemView.select_tag_name.isChecked = true
            holder.itemView.select_tag_name.setTextColor(context.resources.getColor(R.color.white))
        }else{
            holder.itemView.select_tag_name.isChecked = false
            holder.itemView.select_tag_name.setTextColor(context.resources.getColor(R.color.black))

        }


        holder.itemView.select_tag_name.setOnClickListener {
            lastCheckedPosition = list[position].tag_seq
            Log.e("Tag Name",""+holder.itemView.select_tag_name.text.toString())
            // activity에 선택된 Tag 넘겨주기
            onTagCheckedSeq.getTagCheckedSeq(tag_seq.toString(),tag_name.toString())
            notifyDataSetChanged()
        }

        holder.bindView(item,position)
    }

    override fun getItemCount(): Int {
        return list.size   // 7개 까지만
    }

    // activity에 선택된 Tag 넘겨주기
    interface OnTagCheckedSeq {
        fun getTagCheckedSeq(tag_seq : String,tag_name: String)
    }

}
