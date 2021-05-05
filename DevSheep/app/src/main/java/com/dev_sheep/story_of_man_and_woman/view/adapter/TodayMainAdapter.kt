package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.adapter_main_today.view.*
import kotlinx.android.synthetic.main.adapter_main_today.view.tv_title

class TodayMainAdapter(private val feedList: MutableList<Feed>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_main_today, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val viewHolder : ViewHolder = holder as ViewHolder
            val item = feedList[position]
            viewHolder.bindView(item,position)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: Feed,position:Int) {
            Log.e("gender ?",""+item.creater_gender.toString())
            Log.e("age ?",""+item.creater_age.toString())
            Log.e("title ?",""+item.title.toString())


            if(position == 1){
                itemView.today_line_top.visibility = View.VISIBLE
                itemView.today_line_bottom.visibility = View.VISIBLE
            }else{
                itemView.today_line_top.visibility = View.GONE
                itemView.today_line_bottom.visibility = View.GONE
            }

            if(item.creater_gender.equals("남")) {
                itemView.iv_gender.setImageResource(R.drawable.ic_gender_male)
//                Glide.with(itemView.context)
//                    .load(R.drawable.ic_gender_male)
//                    .placeholder(android.R.color.transparent)
//                    .error(R.drawable.error_loading)
//                    .into(itemView.iv_gender)
            }else if(item.creater_gender.equals("여")){
                itemView.iv_gender.setImageResource(R.drawable.ic_gender_female)
//                Glide.with(itemView.context)
//                    .load(R.drawable.ic_gender_female)
//                    .placeholder(android.R.color.transparent)
//                    .error(R.drawable.error_loading)
//                    .into(itemView.iv_gender)
            }

            itemView.tv_title.text = item.title
            itemView.tv_comment.text = "댓글 "+item.comment_no

        }

    }


    override fun getItemCount(): Int {
        return if(feedList == null) 0
        else feedList.size
    }

}