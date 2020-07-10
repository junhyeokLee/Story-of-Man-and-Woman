package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import kotlinx.android.synthetic.main.adpater_tag.view.*

abstract class Test_tag_Adapter(
    private val list: List<Test>,
    private val context: Context
) : RecyclerView.Adapter<Test_tag_Adapter.ViewHolder>() {


    abstract fun load()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: Test) {

            itemView.text_tag.text = "  # "+item.name+"   "

//1 . # 모든사연
//2 . # 아무 이야기
//3 . # 남과 여
//4 . # 고민상담
//5 . # 남자 이야기
//6 . # 여자 이야기
//7 . # 사랑 이야기
//8 . # Q & A


            // 오늘의 사연, 이달의 사연, 많이 본 사연 , 많은 응원중.., 공지사항

            // 오늘의 베스트 top5, 이달의 베스트 top5


//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adpater_tag, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bindView(item)
    }

    override fun getItemCount(): Int {
        return list.size   // 7개 까지만
    }

}


