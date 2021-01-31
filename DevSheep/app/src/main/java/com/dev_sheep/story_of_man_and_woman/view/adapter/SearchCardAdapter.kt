package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedSearchActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_feed_card_item.view.*
import kotlinx.android.synthetic.main.adapter_search_card.view.*
import kotlinx.android.synthetic.main.adpater_tag.view.tag_name

class SearchCardAdapter(
    private val list: List<Tag>,
    private val context: Context,
    private val feedViewModel: FeedViewModel
) : RecyclerView.Adapter<SearchCardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View,feedViewModel: FeedViewModel) : RecyclerView.ViewHolder(itemView) {
        lateinit var mSearchCardItemAdater: SearchCardItemAdapter

        fun bindView(item: Tag, feedViewModel: FeedViewModel,context: Context) {

            itemView.tag_name.text = "# "+item.tag_name+"   "
       if(item.tag_name.toString().equals("여자 이야기")){
                itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.main_Accent))
            }else if(item.tag_name.toString().equals("남자 이야기")){
           itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.main_Accent3))
       }else if(item.tag_name.toString().equals("이별")){
           itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.white))
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.qan_background))
       }
       else if(item.tag_name.toString().equals("남과 여")){

           val paint =  itemView.tag_name.paint
           val width = paint.measureText(itemView.tag_name.text.toString())
           val textShader: Shader = LinearGradient(0f, 0f, width, itemView.tag_name.textSize, intArrayOf(
               Color.parseColor("#ec6674"),
//                Color.parseColor("#429BED"),
//                Color.parseColor("#64B678"),
//                Color.parseColor("#478AEA"),
               Color.parseColor("#7A5DC7")
           ), null, Shader.TileMode.REPEAT)

           itemView.tag_name.paint.shader = textShader
       }
       else if(item.tag_name.toString().equals("사랑 이야기")){
           itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.white))
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.love_background))
       }
       else if(item.tag_name.toString().equals("아무 이야기")){
           itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.white))
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.anything_background))
       }
       else if(item.tag_name.toString().equals("연애 이야기")){
           itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.white))
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.love_story_background))
       }
       else if(item.tag_name.toString().equals("고민 있어요")){
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.qna_background))
       }
       else if(item.tag_name.toString().equals("잡담")){
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.question_background))
       }

       else if(item.tag_name.toString().equals("사랑과 전쟁")){
           itemView.tag_name.setTextColor(ContextCompat.getColor(context, R.color.white))
           itemView.layout_card_background.setBackground(ContextCompat.getDrawable(context, R.drawable.man_woman_background))
       }



            val single = FEED_SERVICE.getSearchCard(item.tag_seq)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mSearchCardItemAdater = SearchCardItemAdapter(it,itemView.context,itemView.tag_name)
                    itemView.recyclerview?.apply {
                        var linearLayoutMnager = LinearLayoutManager(itemView.context)
                        this.layoutManager = linearLayoutMnager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mSearchCardItemAdater
                        this.setOnClickListener {
                            val intent = Intent(itemView.context, FeedSearchActivity::class.java)
                            intent.putExtra("tag_seq",item.tag_seq.toString())
                            intent.putExtra("tag_name",item.tag_name)
                            itemView.context.startActivity(intent)
                        }

                    }

                },{

                })

            itemView.card_view.setOnClickListener {
                val intent = Intent(itemView.context, FeedSearchActivity::class.java)
                intent.putExtra("tag_seq",item.tag_seq.toString())
                intent.putExtra("tag_name",item.tag_name)
//                itemView.context.startActivity(intent)
                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity,
                        itemView.tag_name as TextView, "RankName")
                itemView.context.startActivity(intent,options.toBundle())
            }





//1 . # 모든사연
//2 . # 잡담
//3 . # 남과 여
//4 . # 고민/질문
//5 . # 남자 이야기
//6 . # 여자 이야기
//7 . # Yes or No

//            val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius)



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_search_card, parent, false)
        val feedViewModel = feedViewModel
        return ViewHolder(view,feedViewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val feedViewModel = feedViewModel
        holder.bindView(item,feedViewModel,context)
    }

    override fun getItemCount(): Int {
        return list.size   // 7개 까지만
    }


}
