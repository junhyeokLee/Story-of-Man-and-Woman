package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.adapter_feed_card_item.view.*
import kotlinx.android.synthetic.main.adapter_feed_card_item.view.tv_title

class FeedCardItemAdapter(
    private val list: List<Feed>,
    private var context: Context,
    private val onClickViewListener: FeedCardItemAdapter.OnClickViewListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                    list,
                    onClickViewListener
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
                viewHolder.bindView(feed, position)

            }

        }

    }

    override fun getItemCount(): Int {
        return 5
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    }



    internal class FeedHolder(
        itemView: View,
        list: List<Feed>,
        onClickViewListener: FeedCardItemAdapter.OnClickViewListener
    ) :  RecyclerView.ViewHolder(itemView) {
        private val iv_feed_card : ImageView = itemView.findViewById(R.id.iv_feed_card)
        private val linearLayoutBackground : LinearLayout = itemView.findViewById(R.id.linearLayoutBackground)
        private val onClickFeedView = onClickViewListener
        private var favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_btn)
        private val bookmarkButton: CheckBox = itemView.findViewById(R.id.bookmark)
        private val layoutTitle : LinearLayout = itemView.findViewById(R.id.layout_title)
        private val layoutBottom: LinearLayout = itemView.findViewById(R.id.layout_bottom)

        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int) {

            itemView.tv_title.text = item.title

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


            with(iv_feed_card){
                var requestOptions = com.bumptech.glide.request.RequestOptions()
                requestOptions = requestOptions.transform(
                    com.bumptech.glide.load.resource.bitmap.CenterCrop(),
                    com.bumptech.glide.load.resource.bitmap.RoundedCorners(6)
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


            with(linearLayoutBackground){
                setOnClickListener {
                    onClickFeedView.OnClickFeed(
                        item,
                        favoriteButton,
                        bookmarkButton,
                        position
                    )
                }
            }

            with(layoutTitle){
                setOnClickListener {
                    onClickFeedView.OnClickFeed(
                        item,
                        favoriteButton,
                        bookmarkButton,
                        position
                    )
                }
            }

            with(layoutBottom){
                setOnClickListener {
                    onClickFeedView.OnClickFeed(
                        item,
                        favoriteButton,
                        bookmarkButton,
                        position
                    )
                }
            }


        }


    }

    // omeFragment에서 클릭시 뷰모델 사용하여 조회수 올리기위함
    interface OnClickViewListener {
        fun OnClickFeed(
            feed: Feed,
            cb: CheckBox,
            cb2: CheckBox,
            position: Int
        )
    }
}