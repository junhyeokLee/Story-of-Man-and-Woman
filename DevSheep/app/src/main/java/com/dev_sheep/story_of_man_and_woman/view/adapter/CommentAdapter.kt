package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Comment
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(private val commentList: List<Comment>,
                     private val context: Context,
                     private val feedViewModel: FeedViewModel
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        fun bindView(item: Comment,feedViewModel:FeedViewModel,context: Context) {

            // 저장된 m_seq 가져오기
            val getM_seq = context.getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
            my_m_seq = getM_seq.getString("inputMseq", null)

            itemView.tv_m_nick.text = item.writer
            itemView.tv_comment.text = item.comment
            itemView.tv_age.text = item.writer_age
            itemView.tv_gender.text = item.writer_gender
            itemView.tv_feed_date.text = calculateTime(sdf.parse(item.comment_date))
            itemView.like_count.text = item.like_no.toString()

            with(itemView.layout_nick){
                setOnClickListener {
                    // 자신을 클릭시
                    if (item.writer_seq == my_m_seq) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("ProfileMyFragment", true)
                        (context as Activity).startActivity(intent)
                        (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    }
                    // 유저 클릭시
                    else {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("m_seq", item.writer_seq)
                        intent.putExtra("ProfileUsersFragment", true)
                        (context as Activity).startActivity(intent)
                        (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    }
                }
            }

            with(itemView.img_profile) {
                Glide.with(itemView.context)
                    .load(item.writer_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .into(itemView.img_profile)

                setOnClickListener {
                    // 자신을 클릭시
                    if (item.writer_seq == my_m_seq) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("ProfileMyFragment", true)
                        (context as Activity).startActivity(intent)
                        (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    }
                    // 유저 클릭시
                    else {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("m_seq", item.writer_seq)
                        intent.putExtra("ProfileUsersFragment", true)
                        (context as Activity).startActivity(intent)
                        (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    }
                }
            }

            with(itemView.tv_re_cemment){
                setOnClickListener {
                    val lintent = Intent(context, MainActivity::class.java)
                    lintent.putExtra("comment_seq" , item.comment_seq.toString())
                    lintent.putExtra("ReCommentFragment", true)

                    lintent.putExtra(FeedActivity.EXTRA_POSITION, position)
//                        context.transitionName = position.toString()
                    (context as Activity).startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )

                }
            }
            with(itemView.rv_recomment){
                val layoutManager = GridLayoutManager(itemView.context, 1)
                this?.layoutManager = layoutManager
                val single_recomment = FEED_SERVICE.getReComment(Integer.parseInt(item.feed_seq!!),item.group_seq!!)
                single_recomment.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        if(it.size > 0) {

                            this.adapter =  CommentReAdapter(it, context!!, feedViewModel)
                            Log.e("eqeq","ddddd")
                        }

                    },{
                        Log.e("get comment 실패 = ",it.message.toString())
                    })

            }


            var favCount : String
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()

            with(itemView.favorite_btn) {

                if (preferences.contains("checked" + item.comment_seq) &&
                    preferences.getBoolean("checked" +item.comment_seq, false) == true)
                {
                    this.isChecked = true

                } else {
                    this.isChecked = false
                }

                if (this.isChecked == true) {
                    setOnCheckedChangeListener { compoundButton, b ->
                        if (this.isChecked) {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "true")
                            favCount = item.like_no.toString()
                            editor.putBoolean("checked" + item.comment_seq, true)
                            editor.apply()
                        } else {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "false")
                            favCount = item.like_no?.minus(1).toString()
                            editor.putBoolean("checked" + item.comment_seq, false)
                            editor.apply()
                        }
                        itemView.like_count.text = favCount
                    }
                } else {
                    setOnCheckedChangeListener { compoundButton, b ->
                        if (this.isChecked) {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "true")
                            favCount = item.like_no?.plus(1).toString()
                            editor.putBoolean("checked" + item.comment_seq, true)
                            editor.apply()
                        } else {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "false")
                            favCount = item.like_no.toString()
                            editor.putBoolean("checked" + item.comment_seq, false)
                            editor.apply()
                        }
                        itemView.like_count.text = favCount
                    }
                }
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_comment, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = commentList[position]
        val viewModel = feedViewModel
        holder.bindView(item,viewModel,context)


    }

    override fun getItemCount(): Int {

        return commentList.size   // 7개 까지만
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

}