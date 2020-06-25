package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.utils.PokemonColorUtil
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.progress_loading.view.*
import java.util.*

class FeedAdapter(
    private val list: List<Test>,
    private val context: Context,
    private var fragmentManager: FragmentManager

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    var mViewPagerState = HashMap<Int, Int>()
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
//        val view: View?
//        val viewHolder: RecyclerView.ViewHolder
//        var viewHolder: RecyclerView.ViewHolder? = null
//        when(viewType){
//            VIEW_TYPE_ITEM ->{
//                val viewLoading =
//                    LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
//                    viewHolder = LoadingViewHolder(viewLoading)
//            }
//            VIEW_TYPE_LOADING ->{
//                val viewItem =
//                    LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
//                viewHolder = FeedHolder(viewItem)
//            }
//        }
//        return viewHolder!!

        if(viewType == VIEW_TYPE_ITEM) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.adapter_feed, parent, false)
            return FeedHolder(view)
        }
        else{
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
            return LoadingViewHolder(view)
        }


    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

//        if(holder is FeedHolder){
//            val list = list[position]
//            holder.bindView(list)
//        }
//        else if(holder is LoadingViewHolder){
//            holder.progressBar.isIndeterminate = true
//        }

//        val obj = list[position]
//
        val viewHolder: FeedHolder = holder as FeedHolder
        val item = list[position] // 배너에서 시작을 0 부터 했기때문에 피드가 1부터 시작하는걸 -1 시켜서 0부터 보여지게
        viewHolder.bindView(item)


    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {

    }

    internal class FeedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val favoriteButton: ImageView = itemView.findViewById(R.id.favorite_btn)
        private val name : TextView = itemView.findViewById(R.id.textViewName)
        private val id : TextView = itemView.findViewById(R.id.textViewID)
        private val body: TextView = itemView.findViewById(R.id.tv_body)
        private val favoriteValue: TextView = itemView.findViewById(R.id.like_count)

        fun bindView(item: Test) {
            itemView.textViewName.text = item.name
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

            itemView.favorite_btn.setOnClickListener{
                val favorited = !item.favorited
                setFavoriteDrawable(favorited,item)
            }
        }


        private fun setFavoriteDrawable(favorited: Boolean,test: Test) {
            val context = itemView.context
            var favCount : String
            val drawable = if (favorited) {
                context.getDrawable(R.drawable.ic_favorite_filled)
            } else {
                context.getDrawable(R.drawable.ic_favorite_hollow)
            }
            if(favorited){
                test.favorited = favorited
                favCount = "1"
            }else{
                test.favorited = favorited
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
    }

    internal class LoadingViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var progressBar = itemView.progressbar_loading
    }

    fun setLoad(){

    }

    fun addData(item:ArrayList<Test>) {

        var size = item.size
        item.addAll(item)
        var sizeNew = item.size
        notifyItemRangeChanged(size, sizeNew)
    }

}

