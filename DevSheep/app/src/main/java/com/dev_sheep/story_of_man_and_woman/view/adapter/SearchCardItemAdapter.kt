package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.preference.PreferenceManager
import android.util.Log
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
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedSearchActivity
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.adapter_feed.view.tv_content
import kotlinx.android.synthetic.main.adapter_feed_card_item.view.tv_title
import kotlinx.android.synthetic.main.adapter_search_card_item.view.*
import org.jsoup.Jsoup

class SearchCardItemAdapter(
    private val list: List<Feed>,
    private var context: Context,
    private var tag_name: TextView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    private val VIEW_TYPE_ITEM = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context

        val view: View?

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_search_card_item, parent, false)
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
                val feed = list.get(position)
                viewHolder.bindView(feed, position,tag_name)

            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }





    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    }



    internal class FeedHolder(
        itemView: View,
        list: List<Feed>
    ) :  RecyclerView.ViewHolder(itemView) {

        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int,tag_name: TextView) {

            itemView.tv_title.text = item.title
            itemView.tv_content.text = Jsoup.parse(item.content).text()

            itemView.layout_title.setOnClickListener {
                val intent = Intent(itemView.context, FeedSearchActivity::class.java)
                intent.putExtra("tag_seq",item.tag_seq.toString())
                intent.putExtra("tag_name",item.tag_name)

                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity,
                        tag_name as TextView, "RankName")
                itemView.context.startActivity(intent,options.toBundle())

            }

//            itemView.tv_content.setOnClickListener {
//                val intent = Intent(itemView.context, FeedSearchActivity::class.java)
//                intent.putExtra("tag_seq",item.tag_seq.toString())
//                intent.putExtra("tag_name",item.tag_name)
//                itemView.context.startActivity(intent)
//            }
//

            if(item.tag_name.toString().equals("이별")){
                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.WHITE)
            }
            if(item.tag_name.toString().equals("사랑 이야기")){
                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.WHITE)
            }
            if(item.tag_name.toString().equals("아무 이야기")){
                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.WHITE)
            }
            if(item.tag_name.toString().equals("연애 이야기")){
                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.WHITE)
            }
            if(item.tag_name.toString().equals("고민 있어요")){
//                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.DKGRAY)
            }
            if(item.tag_name.toString().equals("잡담")){
//                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.DKGRAY)
            }
            if(item.tag_name.toString().equals("사랑과 전쟁")){
                itemView.tv_title.setTextColor(Color.WHITE)
                itemView.tv_content.setTextColor(Color.WHITE)
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
