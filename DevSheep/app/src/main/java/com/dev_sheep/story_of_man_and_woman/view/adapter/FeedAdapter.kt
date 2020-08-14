package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.utils.PokemonColorUtil
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MessageActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.SecretStoryActivity
import com.victor.loading.rotate.RotateLoading
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.adapter_feed.view.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import java.text.SimpleDateFormat
import java.util.*

class FeedAdapter(
    private val list: List<Feed>,
    private var context: Context,
    private var fragmentManager: FragmentManager

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
                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
                FeedHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
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


    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when(holder.itemViewType){
            VIEW_TYPE_ITEM -> {
                val viewHolder : FeedHolder = holder as FeedHolder
                val feed = list[position]
                viewHolder.bindView(feed)
            }

            VIEW_TYPE_LOADING -> {
                val viewHolder : LoadingViewHolder = holder as LoadingViewHolder
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


    internal class FeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val feed_layout: RelativeLayout = itemView.findViewById(R.id.feed_layout)
        private val favoriteButton: ImageView = itemView.findViewById(R.id.favorite_btn)
        private val favoriteValue: TextView = itemView.findViewById(R.id.like_count)
        private val img_profile : CircleImageView = itemView.findViewById(R.id.img_profile)
        private val m_nick : TextView = itemView.findViewById(R.id.tv_m_nick)
        private val content : TextView = itemView.findViewById(R.id.tv_content)
        private val content_img : ImageView = itemView.findViewById(R.id.content_img)
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        fun bindView(item: Feed) {
            itemView.tv_m_nick.text = item.creater
            itemView.tv_title.text = item.title
//            content.text = br2nl(item.content)
            content.text = Jsoup.parse(item.content).text()
            itemView.tv_age.text = item.creater_age + " 대"
            itemView.tag_id.text = "# "+item.tag_name
            itemView.tv_feed_date.text = calculateTime(sdf.parse(item.feed_date))
            itemView.view_count.text = item.view_no.toString()
            itemView.comment_count.text = item.comment_seq.toString()
            itemView.like_count.text = item.like_no.toString()
            itemView.tv_gender.text = item.creater_gender
//            Log.e("qdqwd",content.text.toString());
            Log.e("html : ",item.content);
            Log.e("html jsoup : ",br2nl(item.content));
//            itemView.tv_body.text = "아오이게 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 뭐람"
//            var tv_body_string : String = stripHtml(content.html.toString())
            setReadMore(itemView.tv_content,content.text.toString(),3)

            val color = PokemonColorUtil(itemView.context).getPokemonColor(item.creater_gender)
            itemView.tv_gender.background.colorFilter =
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)






//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)
            Glide.with(itemView.context)
                .load(item.creater_image_url)
                .apply(RequestOptions().circleCrop())
                .placeholder(android.R.color.transparent)
                .into(itemView.img_profile)

//            Glide.with(itemView.context)
//                .load(item.creater_image_url)
//                .placeholder(android.R.color.transparent)
//                .into(itemView.content_img)

            with(m_nick){
                setOnClickListener {
                    val dialog = ProfileUsersFragment()//The fragment that u want to open for example
                    var userFragmnet = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction = userFragmnet.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in,R.anim.fragment_fade_out)
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, dialog);
                    fragmentTransaction.commit()
                }
            }
            with(img_profile){
                setOnClickListener {
                    val dialog = ProfileUsersFragment()//The fragment that u want to open for example
                    var userFragmnet = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction = userFragmnet.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(R.anim.fragment_fade_in,R.anim.fragment_fade_out)
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, dialog);
                    fragmentTransaction.commit()
                }
            }

            with(favoriteButton){
                setOnClickListener {
                    val favorited = !item.favorited
                    setFavoriteDrawable(favorited,item)
                }
            }

            with(content){
                setOnClickListener {
                    val lintent = Intent(itemView.context, FeedActivity::class.java)
                    lintent.putExtra("feed_seq",item.feed_seq)
                    itemView.context.startActivity(lintent)

                    Toast.makeText(context, ""+item.feed_seq+" 피드 클릭", Toast.LENGTH_SHORT).show();
                }
            }

            with(itemView.tv_title){
                setOnClickListener {
                    val lintent = Intent(itemView.context, FeedActivity::class.java)
                    lintent.putExtra("feed_seq",item.feed_seq)
                    itemView.context.startActivity(lintent)

                    Toast.makeText(context, ""+item.feed_seq+" 피드 클릭", Toast.LENGTH_SHORT).show();
                }
            }

        }


        private fun setFavoriteDrawable(favorited: Boolean,feed: Feed) {
            val context = itemView.context
            var favCount : String
            val drawable = if (favorited) {
                context.getDrawable(R.drawable.ic_favorite_filled)
            } else {
                context.getDrawable(R.drawable.ic_favorite_hollow)
            }
            if(favorited){
                feed.favorited = favorited
                favCount = "1"
            }else{
                feed.favorited = favorited
                favCount = "0"
            }

            favoriteButton.setImageDrawable(drawable)
            favoriteValue.setText(favCount)
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
        fun stripHtml(html:String) : String{
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
            return Jsoup.clean(s, "", Whitelist.none(), Document.OutputSettings().prettyPrint(false))
        }

    }

    internal class LoadingViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        private val progressBar : RotateLoading = itemView.findViewById(R.id.rotateloading)

        fun bindView() {
            progressBar.start()
        }

    }


    fun addData(item:ArrayList<Test>) {

        var size = item.size
        item.addAll(item)
        var sizeNew = item.size
        notifyItemRangeChanged(size, sizeNew)
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

