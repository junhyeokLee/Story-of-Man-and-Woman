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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.victor.loading.rotate.RotateLoading
import kotlinx.android.synthetic.main.adapter_feed.view.bookmark
import kotlinx.android.synthetic.main.adapter_feed.view.favorite_btn
import kotlinx.android.synthetic.main.adapter_feed.view.img_profile
import kotlinx.android.synthetic.main.adapter_feed.view.like_count
import kotlinx.android.synthetic.main.adapter_feed.view.tv_m_nick
import kotlinx.android.synthetic.main.adapter_feed.view.tv_title
import kotlinx.android.synthetic.main.adapter_feed.view.view_count
import kotlinx.android.synthetic.main.adapter_feed_tag.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapterTag(
    private val list: List<Feed>,
    private var context: Context,
    private val onClickViewListener: OnClickViewListener,
    private val onClickLikeListener: OnClickLikeListener,
    private val onClickBookMarkListener: OnClickBookMarkListener,
    private val onClickProfileListener: OnClickProfileListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    var mViewPagerState = HashMap<Int, Int>()
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context

        val view: View?
//        val viewHolder: RecyclerView.ViewHolder
//        var viewHolder: RecyclerView.ViewHolder? = null

        if(list == null){
            VIEW_TYPE_LOADING
        }else{
            VIEW_TYPE_ITEM
        }

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.adapter_feed_tag,
                    parent,
                    false
                )
                FeedHolder(
                    view,
                    list,
                    onClickViewListener,
                    onClickLikeListener,
                    onClickBookMarkListener,
                    onClickProfileListener
                )
            }
            VIEW_TYPE_LOADING -> {
                view = LayoutInflater.from(parent.context).inflate(
                    R.layout.progress_loading,
                    parent,
                    false
                )
                LoadingViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }


//        if(viewType == VIEW_TYPE_ITEM) {
//            val view =
//                LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
//            return FeedHolder(view)
//        }
//        else {
//            val view =
//                LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
//            return LoadingViewHolder(view)
//        }

        fun notification(){
            this.notifyDataSetChanged()
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


//        if(holder is FeedHolder){
//            val list = list[position]
//            holder.bindView(list)
//        }
//        else if(holder is LoadingViewHolder){
//            val list = list[position]
//            holder.bindView(list)
//        }

//        val viewHolder: FeedHolder = holder as FeedHolder
//        val item = list[position] // 배너에서 시작을 0 부터 했기때문에 피드가 1부터 시작하는걸 -1 시켜서 0부터 보여지게
//        viewHolder.bindView(item)


    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    }



    internal class FeedHolder(
        itemView: View,
        list: List<Feed>,
        onClickViewListener: OnClickViewListener,
        onClickLikeListener: OnClickLikeListener,
        onClickBookMarkListener: OnClickBookMarkListener,
        onClickProfileListener: OnClickProfileListener
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
        private val listImg = list

        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int) {

            ViewCompat.setTransitionName(itemView.tv_m_nick, position.toString() + "Text")
            ViewCompat.setTransitionName(itemView.img_profile, (position).toString() + "Img")

            itemView.tv_m_nick.text = item.creater
            itemView.tv_title.text = item.title
//            content.text = Jsoup.parse(item.content).text()
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
            m_seq = preferences.getString("inputMseq", "")

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

    internal class LoadingViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val progressBar : RotateLoading = itemView.findViewById(R.id.rotateloading)

        fun bindView() {
            progressBar.start()
        }

    }


    fun addData(item: ArrayList<Test>) {

        var size = item.size
        item.addAll(item)
        var sizeNew = item.size
        notifyItemRangeChanged(size, sizeNew)
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


}

