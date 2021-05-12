package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.method.TextKeyListener.clear
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.utils.SpacesItemDecoration
import com.dev_sheep.story_of_man_and_woman.view.Assymetric.AsymmetricRecyclerView
import com.dev_sheep.story_of_man_and_woman.view.Assymetric.AsymmetricRecyclerViewAdapter
import com.dev_sheep.story_of_man_and_woman.view.Assymetric.Utils
import com.dev_sheep.story_of_man_and_woman.view.activity.CommentActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedEditActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.adapter_feed.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter(
    private var list: MutableList<Feed>,
    private var context: Context,
    private var feedViewModel: FeedViewModel,
    private val onClickViewListener: OnClickViewListener,
    private val onClickLikeListener: OnClickLikeListener,
    private val onClickBookMarkListener: OnClickBookMarkListener,
    private val onClickProfileListener: OnClickProfileListener,
    private val onClickDeleteFeedListener: OnClickDeleteFeedListener,
    private val onEndlessScrollListener: OnEndlessScrollListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    var mViewPagerState = HashMap<Int, Int>()
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_ITEM = 1
    private val VIEW_TYPE_CARD_ITEM = 2
    private var isLoadingAdded = false

//    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        val view: View?
        return when (viewType) {
            VIEW_TYPE_LOADING ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
                LoadingViewHolder(view,isLoadingAdded)
            }

            VIEW_TYPE_ITEM -> { view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
                FeedHolder(
                    view, isLoadingAdded, onClickViewListener, onClickLikeListener, onClickBookMarkListener, onClickProfileListener, onClickDeleteFeedListener, onEndlessScrollListener
                )
            }
            VIEW_TYPE_CARD_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_card_item, parent, false)
                CardViewHolder(view)
            }

            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)  {

        when(holder.itemViewType){

            VIEW_TYPE_LOADING ->{
                val viewHolder: LoadingViewHolder = holder as LoadingViewHolder
                viewHolder.bindView()
            }

            VIEW_TYPE_ITEM -> {
                val viewHolder: FeedHolder = holder as FeedHolder
                val feed = list[position]
                viewHolder.bindView(feed, position, feedViewModel)
            }

            VIEW_TYPE_CARD_ITEM -> {
                val viewHolder: CardViewHolder = holder as CardViewHolder
                viewHolder.bindView()
            }


        }

    }


    fun updateList(feeds: MutableList<Feed>) {

        this.list.clear()
        this.list.addAll(feeds)
        notifyItemInserted(this.list.size)
        // diif util 리사이클러뷰 재활용 능력 향상시켜줌 깜빡임 현상없어짐

//        feeds?.let {
//            val diffUtil = BaseDiffUtil(feeds, this.list)
//            val diffResult = DiffUtil.calculateDiff(diffUtil)
//
//            this.list.run {
//                clear()
//                addAll(feeds)
//                diffResult.dispatchUpdatesTo(this@FeedAdapter)
//
//            }
//        }

//        val diffUtil = BaseDiffUtil(feeds, this.list)
//        val diffResult = DiffUtil.calculateDiff(diffUtil)
//        this.list.clear()
//        this.list.addAll(feeds)
//
//        diffResult.dispatchUpdatesTo(this)


    }
    fun clearList(){
//        this.list.remove(list)
        this.list.clear()
    }

    override fun getItemViewType(position: Int): Int {

//         var feed = list.get(position)
        return if(position == list.size ) VIEW_TYPE_LOADING
        else {
            VIEW_TYPE_ITEM
        }

//        if (list.get(position) == null){
//
//            return VIEW_TYPE_LOADING
//        }
//
//        var type = 0
//        if(position == 0){
//            type = VIEW_TYPE_CARD_ITEM
//        }else{
//            type = VIEW_TYPE_ITEM
//        }
//
//        return type
    }

    override fun getItemCount(): Int {
        return if(list == null) 0
        else list.size
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    }

    override fun getItemId(position: Int): Long {
        return list.get(position).feed_seq.toLong()
    }

    internal class FeedHolder(
        itemView: View,
        isLoadingAdded: Boolean,
        onClickViewListener: OnClickViewListener,
        onClickLikeListener: OnClickLikeListener,
        onClickBookMarkListener: OnClickBookMarkListener,
        onClickProfileListener: OnClickProfileListener,
        onClickDeleteFeedListener: OnClickDeleteFeedListener,
        onEndlessScrollListener: OnEndlessScrollListener
    ) :  RecyclerView.ViewHolder(itemView){

        private val feed_layout: RelativeLayout = itemView.findViewById(R.id.feed_layout)
        private var favoriteButton: CheckBox = itemView.findViewById(R.id.favorite_btn)
        private val favoriteValue: TextView = itemView.findViewById(R.id.like_count)
        private val bookmarkButton: CheckBox = itemView.findViewById(R.id.bookmark)
        private val layout_comment : LinearLayout = itemView.findViewById(R.id.layout_comment)
        private val layout_view : LinearLayout = itemView.findViewById(R.id.layout_view)
        private val m_nick : TextView = itemView.findViewById(R.id.tv_m_nick)
        private val profile_layout : LinearLayout = itemView.findViewById(R.id.profile_layout)
        private val content : TextView = itemView.findViewById(R.id.tv_content)
        private val recycler_layout : LinearLayout = itemView.findViewById(R.id.recycler_layout)
        private val recycler_frame_layout: FrameLayout = itemView.findViewById(R.id.recycler_frame_layout)
        private val recyclerView : AsymmetricRecyclerView = itemView.findViewById(R.id.recyclerView)
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private val onClickFeedView = onClickViewListener
        private val onClickFeedLike = onClickLikeListener
        private val onClickBookMark = onClickBookMarkListener
        private val onClickProfile = onClickProfileListener
        private val onClickDeleteFeed = onClickDeleteFeedListener
        private val onEndlessScrollListener = onEndlessScrollListener
        lateinit var m_seq : String
        lateinit var my_m_seq:String
        private var isLoadingAdded = isLoadingAdded


        @SuppressLint("Range")
        fun bindView(item: Feed, position: Int, feedViewModel: FeedViewModel) {

            // 자신의 seq 가져오기
            val preferences: SharedPreferences = itemView.context!!.getSharedPreferences(
                "m_seq",
                Context.MODE_PRIVATE
            )
            m_seq = preferences.getString("inputMseq", "")

            ViewCompat.setTransitionName(itemView.tv_m_nick, position.toString() + "Text")
            ViewCompat.setTransitionName(itemView.img_profile, (position).toString() + "Img")

            // 마지막 피드 인지 아닌지 체크
            if(item.last_index.equals("true")){
                isLoadingAdded = false
                onEndlessScrollListener.OnEndless(true)
            }else if(item.last_index.equals("false")) {
                isLoadingAdded = true
                onEndlessScrollListener.OnEndless(false)
            }
            bookmarkButton.setOnCheckedChangeListener(null)
            favoriteButton.setOnCheckedChangeListener(null)
//            favoriteLayout.setOnClickListener(this)
            // 북마크 체크
//            feedViewModel.onCheckedBookMark(m_seq,item.feed_seq,bookmarkButton)

            itemView.tv_m_nick.text = item.creater
            itemView.tv_title.text = item.title
//            content.text = Jsoup.parse(item.content).text()
            itemView.tv_age.text = item.creater_age
            itemView.tag_id.text = "# "+item.tag_name
            itemView.tv_feed_date.text = calculateTime(sdf.parse(item.feed_date))
            itemView.view_count.text = item.view_no.toString()
            itemView.tv_comment_count.text = item.comment_no.toString()
            itemView.like_count.text = item.like_no.toString()
            itemView.tv_gender.text = item.creater_gender

            itemView.setOnClickListener {
                Log.e("click evebt","dwqdqw")
            onClickFeedView.OnClickFeed(item, itemView.tv_m_nick, itemView.img_profile, itemView.favorite_btn, itemView.bookmark, position)
            }

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
                            Toast.makeText(itemView.context, "신고가 접수되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        R.id.item_edit -> {
//                            게시물 수정
                            val intent = Intent(itemView.context, FeedEditActivity::class.java)
                            intent.putExtra("feed_seq", item.feed_seq)
                            intent.putExtra("type", item.type)
                            intent.putExtra("tag_seq",item.tag_seq)
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



            itemView.recyclerView.apply {
                    var adapter = ChildAdapter(item.images, 0, item.images.size,
                    item,itemView.favorite_btn,itemView.bookmark,feedViewModel)
                    val currentOffset = 0
                    var isCol2Avail = false
                    var colSpan = if (Math.random() < 0.2f) 2 else 1
                    if (colSpan == 2 && !isCol2Avail) isCol2Avail =
                        true else if (colSpan == 2 && isCol2Avail) colSpan = 1
                    val rowSpan = colSpan

                    if (item.images.size >= 3 && item.images != null) {
                        setRequestedColumnCount(3)
                        for(i in 0..item.images.size){
                            item.images.get(0).setColumnSpan(2)
                            item.images.get(0).setRowSpan(2);
                            item.images.get(0).setPosition(currentOffset + 0);
                            item.images.get(1).setColumnSpan(1)
                            item.images.get(1).setRowSpan(1);
                            item.images.get(1).setPosition(currentOffset + 1);
                            item.images.get(2).setColumnSpan(1)
                            item.images.get(2).setRowSpan(1);
                            item.images.get(2).setPosition(currentOffset + 2);
                        }
                        requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 1F)
                         adapter = ChildAdapter(item.images, 3, item.images.size,
                             item,itemView.favorite_btn,itemView.bookmark,feedViewModel)

                    } else if (item.images.size == 2 && item.images != null) {
                        setRequestedColumnCount(2)
                        for(i in 0..item.images.size){
                            item.images.get(0).setColumnSpan(1)
                            item.images.get(0).setRowSpan(1);
                            item.images.get(0).setPosition(currentOffset + 0);
                            item.images.get(1).setColumnSpan(1)
                            item.images.get(1).setRowSpan(1);
                            item.images.get(1).setPosition(currentOffset + 1);
                        }
                        requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 1F)
                         adapter = ChildAdapter(item.images, 2, item.images.size,
                             item,itemView.favorite_btn,itemView.bookmark,feedViewModel)

                    } else if (item.images.size == 1 && item.images != null) {
                        setRequestedColumnCount(1)
                        for(i in 0..item.images.size){
                            item.images.get(0).setColumnSpan(1)
                            item.images.get(0).setRowSpan(1);
                            item.images.get(0).setPosition(currentOffset + 0);
                        }

                        requestedHorizontalSpacing = Utils.dpToPx(itemView.context, 1F)
                         adapter = ChildAdapter(item.images, 1, item.images.size,
                             item,itemView.favorite_btn,itemView.bookmark,feedViewModel)

                    }
                    else if (item.images.size == 0 || item.images == null || item.images.isEmpty()) {
                        adapter = ChildAdapter(item.images, 0, item.images.size,
                            item,itemView.favorite_btn,itemView.bookmark,feedViewModel)
                    }

                    this.setAdapter(AsymmetricRecyclerViewAdapter(itemView.context, this, adapter))
                    isDebugging = true
                    addItemDecoration(SpacesItemDecoration(itemView.context.getResources()
                                .getDimensionPixelSize(R.dimen.recycler_padding)))

                }


            setReadMore(itemView.tv_content, content.text.toString(), 3)

//            val color = PokemonColorUtil(itemView.context).getPokemonColor(item.creater_gender)
//            itemView.tv_gender.background.colorFilter =
//                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)

//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)
                Glide.with(itemView.context)
                    .load(item.creater_image_url)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .error(R.drawable.error_loading)
                    .into(itemView.img_profile)


            with(profile_layout){
                setOnClickListener {
                    onClickProfile.OnClickProfile(item, m_nick, itemView.img_profile)
                }
            }

            with(layout_comment){
                setOnClickListener {
//                    val intent = Intent(context, MainActivity::class.java)
                    val intent = Intent(context, CommentActivity::class.java)
                    intent.putExtra("feed_seq", item.feed_seq.toString())
                    intent.putExtra("feed_creater", item.creater_seq)
                    intent.putExtra("feed_title", item.title)
//                    intent.putExtra("CommentFragment", true)
                    context.startActivity(intent)
                }
            }


            with(favoriteButton){
                val feed = item
                var favCount : String
                val preferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
                val editor = preferences.edit()


                Log.e("item checked","feed seq "+feed.feed_seq+"  checked value"+" "+ preferences.getBoolean("checked" + feed.feed_seq, false))

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

//                    setOnCheckedChangeListener { compoundButton, b ->
//                        if(this.isChecked){
//                            onClickBookMark.OnClickBookMark(m_seq, feed.feed_seq, "true")
//                        }else{
//                            onClickBookMark.OnClickBookMark(m_seq, feed.feed_seq, "false")
//                        }
//                    }


                if (preferences.contains("bookmark_checked" + feed.feed_seq) &&
                    preferences.getBoolean("bookmark_checked" + feed.feed_seq, false) == true )
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
            with(recyclerView){
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

    internal class CardViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        lateinit var mFeedCardAdater: FeedCardAdapter
        private val vp_feed_card: ViewPager = itemView.findViewById(R.id.vp_feed_card)


        fun bindView() {
            mFeedCardAdater = FeedCardAdapter(itemView.context)
            vp_feed_card?.apply {
                this.adapter = mFeedCardAdater
                this.setPadding(72, 0, 72, 0);

                this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {

                    }
                    override fun onPageSelected(position: Int) {

                    }
                })
            }

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

    interface OnEndlessScrollListener{
        fun OnEndless(boolean_value: Boolean)
    }
}

