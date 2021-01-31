package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.utils.PokemonColorUtil
import com.dev_sheep.story_of_man_and_woman.utils.SpacesItemDecoration
import com.dev_sheep.story_of_man_and_woman.view.Assymetric.AsymmetricRecyclerView
import com.dev_sheep.story_of_man_and_woman.view.Assymetric.AsymmetricRecyclerViewAdapter
import com.dev_sheep.story_of_man_and_woman.view.Assymetric.Utils
import com.dev_sheep.story_of_man_and_woman.view.activity.CommentActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedEditActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.LoginActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.github.florent37.fiftyshadesof.FiftyShadesOf
import com.victor.loading.rotate.RotateLoading
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_feed.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter(
    private val list: List<Feed>,
    private var context: Context,
    private var feedViewModel: FeedViewModel,
    private val onClickViewListener: OnClickViewListener,
    private val onClickLikeListener: OnClickLikeListener,
    private val onClickBookMarkListener: OnClickBookMarkListener,
    private val onClickProfileListener: OnClickProfileListener,
    private val onClickDeleteFeedListener : OnClickDeleteFeedListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    var mViewPagerState = HashMap<Int, Int>()
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context

        val view: View?

        if(list == null){
            VIEW_TYPE_LOADING

        }else{
            VIEW_TYPE_ITEM
        }

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
                FeedHolder(
                    view,
                    list,
                    onClickViewListener,
                    onClickLikeListener,
                    onClickBookMarkListener,
                    onClickProfileListener,
                    onClickDeleteFeedListener
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
                var feedViewModel = feedViewModel
                viewHolder.bindView(feed, position,feedViewModel)

            }

            VIEW_TYPE_LOADING -> {
                val viewHolder: LoadingViewHolder = holder as LoadingViewHolder
                viewHolder.bindView()
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
        list: List<Feed>,
        onClickViewListener: OnClickViewListener,
        onClickLikeListener: OnClickLikeListener,
        onClickBookMarkListener: OnClickBookMarkListener,
        onClickProfileListener: OnClickProfileListener,
        onClickDeleteFeedListener: OnClickDeleteFeedListener
    ) :  RecyclerView.ViewHolder(itemView){
        private val feed_layout: RelativeLayout = itemView.findViewById(R.id.feed_layout)
        private var favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_btn)
        private val favoriteValue: TextView = itemView.findViewById(R.id.like_count)
        private val bookmarkButton: CheckBox = itemView.findViewById(R.id.bookmark)
        private val iv_comment : ImageView = itemView.findViewById(R.id.iv_comment)
        private val layout_comment : LinearLayout = itemView.findViewById(R.id.layout_comment)
        private val layout_view : LinearLayout = itemView.findViewById(R.id.layout_view)
        private val layout_favorite : LinearLayout = itemView.findViewById(R.id.layout_favorite)
        private val layout_bookmark : LinearLayout = itemView.findViewById(R.id.layout_bookmark)
        private val tv_comment_count : TextView = itemView.findViewById(R.id.tv_comment_count)
        private val img_profile : ImageView = itemView.findViewById(R.id.img_profile)
        private val m_nick : TextView = itemView.findViewById(R.id.tv_m_nick)
        private val profile_layout : RelativeLayout = itemView.findViewById(R.id.profile_layout)
        private val content : TextView = itemView.findViewById(R.id.tv_content)
        private val recycler_layout : LinearLayout = itemView.findViewById(R.id.recycler_layout)
        private val ib_menu : ImageButton = itemView.findViewById(R.id.ib_menu)
        //        private val content_img : ImageView = itemView.findViewById(R.id.content_img)
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private val onClickFeedView = onClickViewListener
        private val onClickFeedLike = onClickLikeListener
        private val onClickBookMark = onClickBookMarkListener
        private val onClickProfile = onClickProfileListener
        private val onClickDeleteFeed = onClickDeleteFeedListener
        private val recyclerView : AsymmetricRecyclerView = itemView.findViewById(R.id.recyclerView)
        lateinit var m_seq : String
        private val listImg = list


        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int,feedViewModel: FeedViewModel) {

            ViewCompat.setTransitionName(itemView.tv_m_nick, position.toString() + "Text")
            ViewCompat.setTransitionName(itemView.img_profile, (position).toString() + "Img")
            itemView.tv_m_nick.text = item.creater
            itemView.tv_title.text = item.title
//            content.text = br2nl(item.content)
            content.text = Jsoup.parse(item.content).text()
            itemView.tv_age.text = item.creater_age
            itemView.tag_id.text = "# "+item.tag_name
            itemView.tv_feed_date.text = calculateTime(sdf.parse(item.feed_date))
            itemView.view_count.text = item.view_no.toString()
            itemView.tv_comment_count.text = item.comment_no.toString()
            itemView.like_count.text = item.like_no.toString()
            itemView.tv_gender.text = item.creater_gender
            itemView.ib_menu.setOnClickListener {
                val popupMenu = PopupMenu(itemView.context, itemView.ib_menu, Gravity.START)
                popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)

                if(m_seq == item.creater_seq) {
                    popupMenu.menu.findItem(R.id.item_alarm).setVisible(false) // 자신의 피드일경우 신고하기 안보이기
                    popupMenu.menu.findItem(R.id.item_edit).setVisible(true)  // 자신의 피드일경우 삭제하기 보이기
                    popupMenu.menu.findItem(R.id.item_delete).setVisible(true)  // 자신의 피드일경우 삭제하기 보이기

                }else{
                    popupMenu.menu.findItem(R.id.item_alarm).setVisible(true)
                    popupMenu.menu.findItem(R.id.item_edit).setVisible(false)
                    popupMenu.menu.findItem(R.id.item_delete).setVisible(false)

                }
                popupMenu.setOnMenuItemClickListener { item_menu: MenuItem ->
                    when(item_menu?.itemId){
                        R.id.item_alarm -> {
                            feedViewModel.increaseComplain(item.feed_seq)
                            Toast.makeText(itemView.context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        R.id.item_edit -> {
//                            게시물 수정
                            val intent = Intent(itemView.context, FeedEditActivity::class.java)
                            intent.putExtra("feed_seq", item.feed_seq)
                            intent.putExtra("type",item.type)
                            (itemView.context as Activity).startActivity(intent)
                            (itemView.context as Activity).overridePendingTransition(
                                R.anim.fragment_fade_in,
                                R.anim.fragment_fade_out
                            )
//                            activity.startActivity(intent)
//                            overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                        }
                        R.id.item_delete -> {
//                            게시물 삭제
                            onClickDeleteFeed.OnClickDeleted(item.feed_seq)
                        }

                    }
                    false
                }
                popupMenu.show()
            }


            if(listImg.get(position).images != null) {
                itemView.recyclerView.apply {
                    val currentOffset = 0
                    var isCol2Avail = false
                    var colSpan = if (Math.random() < 0.2f) 2 else 1
                    if (colSpan == 2 && !isCol2Avail) isCol2Avail =
                        true else if (colSpan == 2 && isCol2Avail) colSpan = 1
                    val rowSpan = colSpan

                    var feed: Feed = listImg.get(position)

                    if (feed.images.size >= 3) {
                        setRequestedColumnCount(3)
                        for(i in 0..feed.images.size){
                            feed.images.get(0).setColumnSpan(2)
                            feed.images.get(0).setRowSpan(2);
                            feed.images.get(0).setPosition(currentOffset + 0);
                            feed.images.get(1).setColumnSpan(1)
                            feed.images.get(1).setRowSpan(1);
                            feed.images.get(1).setPosition(currentOffset + 1);
                            feed.images.get(2).setColumnSpan(1)
                            feed.images.get(2).setRowSpan(1);
                            feed.images.get(2).setPosition(currentOffset + 2);
                        }
                        requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 1F)
                        val adapter = ChildAdapter(feed.images, 3, feed.images.size)
                        this.setAdapter(
                            AsymmetricRecyclerViewAdapter(
                                itemView.context,
                                this,
                                adapter
                            )
                        )
                    } else if (feed.images.size == 2) {
                        setRequestedColumnCount(2)
                        for(i in 0..feed.images.size){
                            feed.images.get(0).setColumnSpan(1)
                            feed.images.get(0).setRowSpan(1);
                            feed.images.get(0).setPosition(currentOffset + 0);
                            feed.images.get(1).setColumnSpan(1)
                            feed.images.get(1).setRowSpan(1);
                            feed.images.get(1).setPosition(currentOffset + 1);
                        }


                        requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 1F)
                        val adapter = ChildAdapter(feed.images, 2, feed.images.size)
                        this.setAdapter(
                            AsymmetricRecyclerViewAdapter(
                                itemView.context,
                                this,
                                adapter
                            )
                        )
                    } else if (feed.images.size == 1) {
                        setRequestedColumnCount(1)
                        for(i in 0..feed.images.size){
                            feed.images.get(0).setColumnSpan(1)
                            feed.images.get(0).setRowSpan(1);
                            feed.images.get(0).setPosition(currentOffset + 0);
                        }

                        requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 1F)
                        val adapter = ChildAdapter(feed.images, 1, feed.images.size)
                        this.setAdapter(
                            AsymmetricRecyclerViewAdapter(
                                itemView.context,
                                this,
                                adapter
                            )
                        )
                    } else if (feed.images.size == 0) {

                    }

                    isDebugging = true
                    addItemDecoration(
                        SpacesItemDecoration(
                            itemView.context.getResources()
                                .getDimensionPixelSize(R.dimen.recycler_padding)
                        )
                    )



                }
            }

//            Log.e("qdqwd",content.text.toString());
            setReadMore(itemView.tv_content, content.text.toString(), 3)

//            val color = PokemonColorUtil(itemView.context).getPokemonColor(item.creater_gender)
//            itemView.tv_gender.background.colorFilter =
//                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)

//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)

            Glide.with(itemView.context)
                .load(item.creater_image_url)
                .apply(RequestOptions().circleCrop())
                .placeholder(android.R.color.transparent)
                .into(itemView.img_profile)





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

            with(layout_comment){
                setOnClickListener {
//                    val intent = Intent(context, MainActivity::class.java)
                    val intent = Intent(context, CommentActivity::class.java)
                    intent.putExtra("feed_seq",item.feed_seq.toString())
                    intent.putExtra("feed_creater",item.creater_seq)
                    intent.putExtra("feed_title",item.title)
//                    intent.putExtra("CommentFragment", true)
                    context.startActivity(intent)
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
                            onClickFeedLike.OnClickFeed(feed, "true")
                            favCount = feed.like_no.toString()
                            editor.putBoolean("checked" + feed.feed_seq, true)
                            editor.apply()
                        }else{
                            onClickFeedLike.OnClickFeed(feed, "false")
                            favCount = feed.like_no?.minus(1).toString()
                            editor.putBoolean("checked" + feed.feed_seq, false)
                            editor.apply()
                        }
                        favoriteValue.setText(favCount)
                    }
                }else{
                    setOnCheckedChangeListener { compoundButton, b ->
                        if(this.isChecked){
                            onClickFeedLike.OnClickFeed(feed, "true")
                            favCount = feed.like_no?.plus(1).toString()
                            editor.putBoolean("checked" + feed.feed_seq, true)
                            editor.apply()
                        }else{
                            onClickFeedLike.OnClickFeed(feed, "false")
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

            with(layout_view){
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



        private fun setReadMore(view: TextView, text: String, maxLine: Int) {
            val context = view.context
            val expanedText = " ... 더보기"
            if (view.tag != null && view.tag == text) { //Tag로 전값 의 text를 비교하여똑같으면 실행하지 않음.
                return
            }
            view.tag = text //Tag에 text 저장
            view.text = text // setText를 미리 하셔야  getLineCount()를 호출가능

            view.post {
                if (view.lineCount >= maxLine) { //Line Count가 설정한 MaxLine의 값보다 크다면 처리시작
                    val lineEndIndex =
                        view.layout.getLineVisibleEnd(maxLine - 1) //Max Line 까지의 text length
                    val split =
                        text.split("\n").toTypedArray() //text를 자름
                    var splitLength = 0
                    var lessText = ""
                    for (item in split) {
                        splitLength += item.length + 1
                        if (splitLength >= lineEndIndex) { //마지막 줄일때!
                            lessText += if (item.length >= expanedText.length) {
                                item.substring(
                                    0,
                                    item.length - expanedText.length
                                ) + expanedText
                            } else {
                                item + expanedText
                            }
                            break //종료
                        }
                        lessText += """
                    $item

                    """.trimIndent()
                    }
                    val spannableString = SpannableString(lessText)
                    spannableString.setSpan(
                        object : ClickableSpan() {
                            //클릭이벤트
                            override fun onClick(v: View) {
//                                view.text = text
                                Toast.makeText(context, "ㄷㅓ보기 클릭", Toast.LENGTH_SHORT).show();
                            }

                            override fun updateDrawState(ds: TextPaint) { //컬러 처리
                                ds.color = ContextCompat.getColor(context, R.color.gray)
                            }
                        },
                        spannableString.length - expanedText.length,
                        spannableString.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    view.text = spannableString
                    view.movementMethod = LinkMovementMethod.getInstance()
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
            FiftyShadesOf.with(itemView.context)
                .on(R.id.recyclerView)
                .start();
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
    interface OnClickDeleteFeedListener{
        fun OnClickDeleted(feed_seq: Int)
    }
    // omeFragment에서 클릭시 뷰모델 사용하여 좋아 올리기위함
    interface OnClickLikeListener {
        fun OnClickFeed(feed_seq: Feed, boolean_value: String)
    }
    // omeFragment에서 클릭시 뷰모델 사용하여 저장하기위
    interface OnClickBookMarkListener {
        fun OnClickBookMark(m_seq: String, feed_seq: Int, boolean_value: String)
    }
    interface OnClickProfileListener{
        fun OnClickProfile(feed: Feed, tv: TextView, iv: ImageView)
    }




}

/** 몇분전, 방금 전,  */

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
