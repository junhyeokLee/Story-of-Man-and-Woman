package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.opengl.Visibility
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.utils.BaseDiffUtil
import com.victor.loading.rotate.RotateLoading
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.adapter_feed.view.bookmark
import kotlinx.android.synthetic.main.adapter_feed.view.favorite_btn
import kotlinx.android.synthetic.main.adapter_feed.view.img_profile
import kotlinx.android.synthetic.main.adapter_feed.view.like_count
import kotlinx.android.synthetic.main.adapter_feed.view.tv_m_nick
import kotlinx.android.synthetic.main.adapter_feed.view.tv_title
import kotlinx.android.synthetic.main.adapter_feed.view.view_count
import kotlinx.android.synthetic.main.adapter_feed_tag.view.*
import kotlinx.android.synthetic.main.adapter_feed_tag.view.tv_feed_date
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapterTag(
    private val list: MutableList<Feed>,
    private var context: Context,
    private val onClickViewListener: OnClickViewListener,
    private val onClickLikeListener: OnClickLikeListener,
    private val onClickBookMarkListener: OnClickBookMarkListener,
    private val onClickProfileListener: OnClickProfileListener,
    private val onEndlessScrollListener: OnEndlessScrollListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_ITEM = 1
    private var isLoadingAdded = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        val view: View?

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.adapter_feed_tag,
                    parent,
                    false
                )
                FeedHolder(
                    view,
                    isLoadingAdded,
                    onClickViewListener,
                    onClickLikeListener,
                    onClickBookMarkListener,
                    onClickProfileListener,
                    onEndlessScrollListener
                )
            }
            VIEW_TYPE_LOADING -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.progress_loading,
                    parent,
                    false
                )
                FeedAdapterRank.LoadingViewHolder(view, isLoadingAdded)
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

            VIEW_TYPE_LOADING -> {
                val viewHolder: LoadingViewHolder = holder as LoadingViewHolder
                viewHolder.bindView()
            }

        }

    }

    fun updateList(feeds: MutableList<Feed>) {

        this.list.clear()
        this.list.addAll(feeds)
        notifyItemInserted(this.list.size)
        // diif util 리사이클러뷰 재활용 능력 향상시켜줌 깜빡임 현상없어짐
//        val diffUtil = BaseDiffUtil(feeds, this.list)
//        val diffResult = DiffUtil.calculateDiff(diffUtil)
//
//        this.list.clear()
//        this.list.addAll(feeds)
//        diffResult.dispatchUpdatesTo(this)

    }

    override fun getItemViewType(position: Int): Int {
        return if(position == list.size ) VIEW_TYPE_LOADING
        else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if(list == null) 0
        else list.size
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    }



    internal class FeedHolder(
        itemView: View,
        isLoadingAdded: Boolean,
        onClickViewListener: OnClickViewListener,
        onClickLikeListener: OnClickLikeListener,
        onClickBookMarkListener: OnClickBookMarkListener,
        onClickProfileListener: OnClickProfileListener,
        onEndlessScrollListener: OnEndlessScrollListener
    ) :  RecyclerView.ViewHolder(itemView){
        private val feed_layout: RelativeLayout = itemView.findViewById(R.id.feed_layout)
        private var favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_btn)
        private val favoriteValue: TextView = itemView.findViewById(R.id.like_count)
        private val bookmarkButton: CheckBox = itemView.findViewById(R.id.bookmark)
        private val img_profile : ImageView = itemView.findViewById(R.id.img_profile)
        private val iv_feed_tag : ImageView = itemView.findViewById(R.id.iv_feed_tag)
        private val m_nick : TextView = itemView.findViewById(R.id.tv_m_nick)
        private val profile_layout : LinearLayout = itemView.findViewById(R.id.profile_layout)
        private val recycler_layout : LinearLayout = itemView.findViewById(R.id.recycler_layout)
        private val layout_iv_feed : FrameLayout = itemView.findViewById(R.id.layout_iv_feed)
        private val tv_age : TextView = itemView.findViewById(R.id.tv_age)
        private val tv_gender : TextView = itemView.findViewById(R.id.tv_gender)
        private val tv_content : TextView = itemView.findViewById(R.id.tv_content)

        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private val onClickFeedView = onClickViewListener
        private val onClickFeedLike = onClickLikeListener
        private val onClickBookMark = onClickBookMarkListener
        private val onClickProfile = onClickProfileListener
        lateinit var m_seq : String
        private var isLoadingAdded = isLoadingAdded
        private val onEndlessScrollListener = onEndlessScrollListener

        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int) {
            // 마지막 피드 인지 아닌지 체크
            if(item.last_index.equals("true")){
                isLoadingAdded = false
                onEndlessScrollListener.OnEndless(true)
            }else if(item.last_index.equals("false")) {
                isLoadingAdded = true
                onEndlessScrollListener.OnEndless(false)
            }

            ViewCompat.setTransitionName(itemView.tv_m_nick, position.toString() + "Text")
            ViewCompat.setTransitionName(itemView.img_profile, (position).toString() + "Img")

            itemView.tv_m_nick.text = item.creater
            itemView.tv_title.text = item.title
//            content.text = Jsoup.parse(item.content).text()
            itemView.tv_tag.text = "#"+item.tag_name
            itemView.tv_feed_date.text = calculateTime(sdf.parse(item.feed_date))
            itemView.view_count.text = item.view_no.toString()
            itemView.comment_count.text = item.comment_no.toString()
            itemView.like_count.text = item.like_no.toString()
            tv_age.text = item.creater_age.toString()
            tv_gender.text = item.creater_gender.toString()
            tv_content.text = Jsoup.parse(item.content).text()
//            tv_tag.text = "#"+item.tag_name.toString()

            Glide.with(itemView.context)
                .load(item.creater_image_url)
                .apply(RequestOptions().circleCrop())
                .placeholder(android.R.color.transparent)
                .into(itemView.img_profile)


            with(iv_feed_tag){
                var requestOptions = RequestOptions()
                requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(16))

                if(item.images.size != 0) {
                    Glide.with(itemView.context)
                        .load(item.images.get(0).imagePath)
                        .apply(requestOptions)
                        .placeholder(android.R.color.transparent)
                        .into(this)
                }else{
                    Glide.with(itemView.context)
                        .load("http://storymaw.com/data/feed/empty_image.jpg")
                        .apply(requestOptions)
                        .placeholder(android.R.color.transparent)
                        .into(this)
                }
            }

            // 자신의 seq 가져오기
            val preferences: SharedPreferences = itemView.context!!.getSharedPreferences(
                "m_seq",
                Context.MODE_PRIVATE
            )
            m_seq = preferences.getString("inputMseq", "")!!

            with(profile_layout){
                setOnClickListener {
                    onClickProfile.OnClickProfile(item, m_nick, itemView.img_profile)
                }
            }

            with(favoriteButton){
                val feed = item
                var favCount : String
                val preferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
                val editor = preferences.edit()


                if (preferences.contains("checked" + feed.feed_seq) &&
                    preferences.getBoolean("checked" + feed.feed_seq, false) == true)
                {
                    this.isChecked = true

                } else {
                    this.isChecked = false
                }

                if(this.isChecked == true){
                    setOnCheckedChangeListener { compoundButton, b ->
                        if(this.isChecked){
                            onClickFeedLike.OnClickFeed(feed.feed_seq, "true")
                            favCount = feed.like_no.toString()
                            editor.putBoolean("checked" + feed.feed_seq, true)
                            editor.apply()
                        }else{
                            onClickFeedLike.OnClickFeed(feed.feed_seq, "false")
                            favCount = feed.like_no?.minus(1).toString()
                            editor.putBoolean("checked" + feed.feed_seq, false)
                            editor.apply()
                        }
                        favoriteValue.setText(favCount)
                    }
                }else{
                    setOnCheckedChangeListener { compoundButton, b ->
                        if(this.isChecked){
                            onClickFeedLike.OnClickFeed(feed.feed_seq, "true")
                            favCount = feed.like_no?.plus(1).toString()
                            editor.putBoolean("checked" + feed.feed_seq, true)
                            editor.apply()
                        }else{
                            onClickFeedLike.OnClickFeed(feed.feed_seq, "false")
                            favCount = feed.like_no.toString()
                            editor.putBoolean("checked" + feed.feed_seq, false)
                            editor.apply()
                        }
                        favoriteValue.setText(favCount)
                    }
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

                if(this.isChecked == true){
                    setOnCheckedChangeListener { compoundButton, b ->
                        if(this.isChecked){
                            onClickBookMark.OnClickBookMark(m_seq, feed.feed_seq, "true")
                            editor.putBoolean("bookmark_checked" + feed.feed_seq, true)
                            editor.apply()
//                            FEED_SERVICE.addBookMark(m_seq,item.feed_seq)
                        }else{
                            onClickBookMark.OnClickBookMark(m_seq, feed.feed_seq, "false")
                            editor.putBoolean("bookmark_checked" + feed.feed_seq, false)
                            editor.apply()
                        }
                    }
                }else{
                    setOnCheckedChangeListener { compoundButton, b ->
                        if(this.isChecked){
                            onClickBookMark.OnClickBookMark(m_seq, feed.feed_seq, "true")
                            editor.putBoolean("bookmark_checked" + feed.feed_seq, true)
                            editor.apply()
                        }else{
                            onClickBookMark.OnClickBookMark(m_seq, feed.feed_seq, "false")
                            editor.putBoolean("bookmark_checked" + feed.feed_seq, false)
                            editor.apply()
                        }
                    }
                }
            }

            with(recycler_layout){
                setOnClickListener {
                    onClickFeedView.OnClickFeed(
                        item,
                        itemView.tv_m_nick,
                        itemView.img_profile,
                        itemView.favorite_btn,
                        itemView.bookmark,
                        position
                    )
                }
            }

            with(feed_layout){
                setOnClickListener {
                    onClickFeedView.OnClickFeed(
                        item,
                        itemView.tv_m_nick,
                        itemView.img_profile,
                        itemView.favorite_btn,
                        itemView.bookmark,
                        position
                    )
                }
            }

        }

        private object TIME_MAXIMUM {
            const val SEC = 60
            const val MIN = 60
            const val HOUR = 24
            const val DAY = 30
            const val MONTH = 12
        }

        fun calculateTime(date: Date): String? {
            val curTime = System.currentTimeMillis()
            val regTime = date.time
            var diffTime = (curTime - regTime) / 1000
            var msg: String? = null
            if (diffTime < TIME_MAXIMUM.SEC) {
                // sec
                msg = diffTime.toString() + " 초전"
            } else if (TIME_MAXIMUM.SEC.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MIN) {
                // min
                println(diffTime)
                msg = diffTime.toString() + " 분전"
            } else if (TIME_MAXIMUM.MIN.let { diffTime /= it; diffTime } < TIME_MAXIMUM.HOUR) {
                // hour
                msg = diffTime.toString() + " 시간전"
            } else if (TIME_MAXIMUM.HOUR.let { diffTime /= it; diffTime } < TIME_MAXIMUM.DAY) {
                // day
                msg = diffTime.toString() + " 일전"
            } else if (TIME_MAXIMUM.DAY.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MONTH) {
                // day
                msg = diffTime.toString() + " 달전"
            } else {
                msg = diffTime.toString() + " 년전"
            }
            return msg
        }


        // html 태그 제거
        fun stripHtml(html: String) : String{
            return Html.fromHtml(html).toString()
        }

        // html br 포
        fun br2nl(html: String?): String? {
            if (html == null) return html
            val document: Document = Jsoup.parse(html)
            document.outputSettings(Document.OutputSettings().prettyPrint(false)) //makes html() preserve linebreaks and spacing
            document.select("br").append("\n")
//            document.select("p").prepend("\n\n")
            val s: String = document.html().replace("\\\n", "\n")
            return Jsoup.clean(
                s,
                "",
                Whitelist.none(),
                Document.OutputSettings().prettyPrint(false)
            )
        }

    }
    internal class LoadingViewHolder(itemView: View,isLoadingAdded:Boolean):RecyclerView.ViewHolder(itemView){
        lateinit var mFeedCardAdater: FeedCardAdapter
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private var isLoading = isLoadingAdded
        fun bindView() {
            if(isLoading == true){
                progressBar.visibility = View.VISIBLE
            }else{
                progressBar.visibility = View.GONE
            }
        }


    }



    // omeFragment에서 클릭시 뷰모델 사용하여 조회수 올리기위함
    interface OnClickViewListener {
        fun OnClickFeed(
            feed: Feed,
            tv: TextView,
            iv: ImageView,
            cb: CheckBox,
            cb2: CheckBox,
            position: Int
        )
    }
    // omeFragment에서 클릭시 뷰모델 사용하여 좋아 올리기위함
    interface OnClickLikeListener {
        fun OnClickFeed(feed_seq: Int, boolean_value: String)
    }
    // omeFragment에서 클릭시 뷰모델 사용하여 저장하기위
    interface OnClickBookMarkListener {
        fun OnClickBookMark(m_seq: String, feed_seq: Int, boolean_value: String)
    }
    interface OnClickProfileListener{
        fun OnClickProfile(feed: Feed, tv: TextView, iv: ImageView)
    }
    interface OnEndlessScrollListener{
        fun OnEndless(boolean_value: Boolean)
    }

}

