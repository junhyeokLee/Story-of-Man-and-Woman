package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedRankActivity
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.adapter_feed_card_item.view.*
import kotlinx.android.synthetic.main.adapter_feed_card_item.view.tv_title

class FeedCardItemAdapter(
    private val list: List<Feed>,
    private var context: Context,
    private var tv_context: TextView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    private val VIEW_TYPE_ITEM = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context

        val view: View?


        VIEW_TYPE_ITEM


        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed_card_item, parent, false)
                FeedHolder(
                    view,
                    list
                )
            }

            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }


    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)  {


        when(holder.itemViewType){
            VIEW_TYPE_ITEM -> {
                val viewHolder: FeedHolder = holder as FeedHolder
                val feed = list[position]
                viewHolder.bindView(feed, position,context,tv_context)

            }

        }

    }

    override fun getItemCount(): Int {

        var count = 0

        if(list.size <= 3){
            count = list.size
        }else{
            count = 3
        }
        return count
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    }



    internal class FeedHolder(
        itemView: View,
        list: List<Feed>) :  RecyclerView.ViewHolder(itemView) {
        private val iv_feed_card : ImageView = itemView.findViewById(R.id.iv_feed_card)
        private var favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_btn)
        private val bookmarkButton: CheckBox = itemView.findViewById(R.id.bookmark)
        private val layoutTitle : LinearLayout = itemView.findViewById(R.id.layout_title)
        private val layoutBottom: LinearLayout = itemView.findViewById(R.id.layout_bottom)
        private val iv_profile : ImageView = itemView.findViewById(R.id.img_profile)
        private val tv_m_nick : TextView = itemView.findViewById(R.id.tv_m_nick)
        private val tv_gender : TextView = itemView.findViewById(R.id.tv_gender)
        private val tv_age : TextView = itemView.findViewById(R.id.tv_age)

        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int,context: Context,tv_context:TextView) {

            itemView.tv_title.text = item.title
            itemView.layout_card_item.setOnClickListener {
                val lintent = Intent(context, FeedRankActivity::class.java)
                lintent.putExtra("tv_title", tv_context.text.toString())
                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,
                        tv_context as TextView, "RankName")
                (context as Activity).startActivity(lintent, options.toBundle())

            }

            if(position == 0) {
                itemView.tv_rank_num.text = "1."
            }else if(position == 1){
                itemView.tv_rank_num.text = "2."
            }
            else if(position == 2){
                itemView.tv_rank_num.text = "3."
            }
            else if(position == 3){
                itemView.tv_rank_num.text = "4."
            }
            else if(position == 4){
                itemView.tv_rank_num.text = "5."
            }
            tv_m_nick.text = item.creater
            tv_age.text = item.creater_age.toString()
            tv_gender.text = item.creater_gender.toString()

            Glide.with(itemView.context)
                .load(item.creater_image_url)
                .apply(RequestOptions().circleCrop())
                .placeholder(android.R.color.transparent)
                .into(iv_profile)


            with(iv_feed_card){
                var requestOptions = com.bumptech.glide.request.RequestOptions()
                requestOptions = requestOptions.transform(
                    com.bumptech.glide.load.resource.bitmap.CenterCrop(),
                    com.bumptech.glide.load.resource.bitmap.RoundedCorners(16)
                )

                if(item.images.size != 0) {
                    com.bumptech.glide.Glide.with(itemView.context)
                        .load(item.images.get(0).imagePath)
                        .apply(requestOptions)
                        .placeholder(android.R.color.transparent)
                        .into(this)
                }
            }

            with(favoriteButton){
                val feed = item
                val preferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)

                if (preferences.contains("checked" + feed.feed_seq) &&
                    preferences.getBoolean("checked" + feed.feed_seq, false) == true)
                {
                    this.isChecked = true

                } else {
                    this.isChecked = false
                }

            }

            with(bookmarkButton){
                val feed = item
                val preferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
                val editor = preferences.edit()

                if (preferences.contains("bookmark_checked" + feed.feed_seq) &&
                    preferences.getBoolean("bookmark_checked" + feed.feed_seq, false) == true)
                {
                    this.isChecked = true
                } else {
                    this.isChecked = false
                }


            }


        }


    }

}