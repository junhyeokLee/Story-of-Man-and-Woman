package com.dev_sheep.story_of_man_and_woman.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.Fragment.Home_Rank_Fragment
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.Test
import com.dev_sheep.story_of_man_and_woman.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.adapter_feed.view.*
import me.relex.circleindicator.CircleIndicator
import java.lang.RuntimeException
import java.util.*

class TestAdapter(
    private val list: List<Test>,
    private val context: Context,
    private var fragmentManager: FragmentManager

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mViewPagerState = HashMap<Int, Int>()
    private val LAYOUT_ONE = 0
    private val LAYOUT_TWO = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
//        val viewHolder: RecyclerView.ViewHolder

        return when (viewType) {
            LAYOUT_ONE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
                BannerItem(view)
            }
            LAYOUT_TWO -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
                FeedHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }

//        if(viewType == LAYOUT_ONE) {
//            view = LayoutInflater.from(context).inflate(R.layout.row, parent, false)
//            viewHolder = BannerItem(view)
//        }else{
//            view = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)
//            viewHolder = FeedHolder(view)
//        }
//        return viewHolder
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

//        val obj = list[position]
//
        when (holder.itemViewType){
            LAYOUT_ONE -> {
                val viewHolder: BannerItem = holder as BannerItem
                    val bannerPagerAdapter = BannerPagerAdapter(fragmentManager)
                    viewHolder.vp.setAdapter(bannerPagerAdapter)
                    viewHolder.vp.autoScroll(5000)
                    viewHolder.indicator.setViewPager(viewHolder.vp)
//            viewHolder.vp.setId(position + 1)
                    if (mViewPagerState.containsKey(position)) {
                        mViewPagerState.get(position)?.let { viewHolder.vp.setCurrentItem(it) }
                    }

            }

            LAYOUT_TWO -> {
            val viewHolder: FeedHolder = holder as FeedHolder
            val item = list[position.minus(1)] // 배너에서 시작을 0 부터 했기때문에 피드가 1부터 시작하는걸 -1 시켜서 0부터 보여지게
            viewHolder.bindView(item)

            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {

        if(holder.itemViewType == LAYOUT_ONE) {
            val viewHolder: BannerItem = holder as BannerItem
            mViewPagerState[viewHolder.adapterPosition] = viewHolder.vp.getCurrentItem()
            mViewPagerState.put(viewHolder.adapterPosition,viewHolder.vp.currentItem)
            super.onViewRecycled(viewHolder)
        }

    }

    override fun getItemViewType(position: Int): Int {

        if(position == 0){
            return LAYOUT_ONE

        }else{
            return LAYOUT_TWO
        }

    }


    inner class FeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(item: Test) {
            itemView.textViewName.text = item.name
            itemView.textViewID.text = item.id
//            itemView.tv_body.text = "아오이게 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어  ㅁ으아어어어어어바어으어 뭐람 아오이게 뭐람 아오이게 뭐람 아오이게 뭐람"
            var tv_body_string : String = itemView.tv_body.text.toString()

            setReadMore(itemView.tv_body,tv_body_string,2)

            val color = PokemonColorUtil(itemView.context).getPokemonColor(item.typeofpokemon)
            itemView.textViewType3.background.colorFilter =
                PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)

            item.typeofpokemon?.getOrNull(0).let { firstType ->
                itemView.textViewType3.text = firstType
                itemView.textViewType3.isVisible = firstType != null
            }


//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)
            Glide.with(itemView.context)
                .load(item.imageurl)
                .apply(RequestOptions().circleCrop())
                .placeholder(android.R.color.transparent)
                .into(itemView.imageView)
        }

        fun setReadMore(view: TextView, text: String, maxLine: Int) {
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
                                view.text = text
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
    }

    inner class BannerItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var vp = itemView.findViewById<View>(R.id.vp) as ViewPager
//        val indicator = itemView.findViewById<View>(R.id.indicator) as CircleIndicator
        var vp: ViewPager = itemView.findViewById(R.id.vp)
        var indicator: CircleIndicator = itemView.findViewById(R.id.indicator)

    }

   inner class BannerPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            return Home_Rank_Fragment.newInstance(position)
        }

        override fun getCount(): Int {
            return 5
        }
    }

    fun ViewPager.autoScroll(interval: Long) {

        val handler = Handler()
        var scrollPosition = 0

        val runnable = object : Runnable {

            override fun run() {

                /**
                 * Calculate "scroll position" with
                 * adapter pages count and current
                 * value of scrollPosition.
                 */
                val count = adapter?.count ?: 0
                setCurrentItem(scrollPosition++ % count, true)

                handler.postDelayed(this, interval)
            }
        }

        handler.post(runnable)
    }




}

