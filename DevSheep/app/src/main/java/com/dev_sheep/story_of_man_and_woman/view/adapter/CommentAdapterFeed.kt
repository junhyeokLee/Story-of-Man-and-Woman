package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Comment
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.utils.BaseDiffUtil
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MyMessageActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.ReCommentActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_comment.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapterFeed(private val commentList: MutableList<Comment>,
                         private val context: Context,
                         private val feedViewModel: FeedViewModel,
                         private val memberViewModel: MemberViewModel,
                         private val onLastIndexListener: OnLastIndexListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var mcontext: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_ITEM = 1
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        val view: View?
        return when (viewType) {
            VIEW_TYPE_LOADING ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
                LoadingViewHolder(view, isLoadingAdded)
            }

            VIEW_TYPE_ITEM -> { view = LayoutInflater.from(context).inflate(R.layout.adapter_comment_feed, parent, false)
                ViewHolder(view,isLoadingAdded,onLastIndexListener)
            }

            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            VIEW_TYPE_LOADING ->{
                val viewHolder: LoadingViewHolder = holder as LoadingViewHolder
                viewHolder.bindView()
            }

            VIEW_TYPE_ITEM -> {
                val viewHolder: ViewHolder = holder as ViewHolder
                val item = commentList[position]
                val viewModel = feedViewModel
                viewHolder.bindView(item,viewModel,memberViewModel,context,position)
            }

        }
    }

    fun updateList(comments: MutableList<Comment>) {
        this.commentList.clear()
        this.commentList.addAll(comments)
        notifyItemInserted(this.commentList.size)

    }

    override fun getItemViewType(position: Int): Int {
        return if(position == commentList.size ) VIEW_TYPE_LOADING
        else {
            VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return if(commentList == null) 0
        else commentList.size
    }

    class ViewHolder(itemView: View, isLoadingAdded: Boolean,  onLastIndexListener: OnLastIndexListener) : RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private lateinit var linearLayoutManager: LinearLayoutManager
        lateinit var mCommentReAdapter : CommentReAdapter
        private val onLastIndexListener = onLastIndexListener
        private var isLoadingAdded = isLoadingAdded

        fun bindView(item: Comment,feedViewModel:FeedViewModel,memberViewModel: MemberViewModel,context: Context,position: Int) {

            if (item.last_index == "true"){
                isLoadingAdded = false
                onLastIndexListener.OnLastIndex(true)
            }else if(item.last_index == "false"){
                isLoadingAdded = true
                onLastIndexListener.OnLastIndex(false)
            }

            // 저장된 m_seq 가져오기
            val getM_seq = context.getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
            my_m_seq = getM_seq.getString("inputMseq", null)


                itemView.tv_m_nick.text = item.writer
                itemView.tv_comment.text = item.comment
                itemView.tv_age.text = item.writer_age
                itemView.tv_gender.text = item.writer_gender
                itemView.tv_feed_date.text = calculateTime(sdf.parse(item.comment_date))
                itemView.like_count.text = item.like_no.toString()
                linearLayoutManager = LinearLayoutManager(context)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL


            val single_recomment = FEED_SERVICE.getReComment(Integer.parseInt(item.feed_seq!!),item.group_seq!!,0,4)
            single_recomment.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    var commentList = it
                    if(commentList.size > 0) {
                        if(commentList.size >= 3){

                            Log.e("feed_seq Recomment = ",""+item.feed_seq)
                            itemView.layout_more_comment.visibility = View.VISIBLE
                            // 답글 갯수 가져오기
                            var single_recomment_count = FEED_SERVICE.getReCommentCount(Integer.parseInt(item.feed_seq!!),item.group_seq!!)
                                single_recomment_count.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        var reCommentCount = it.toString()
                                        itemView.tv_more_comment.text = ""+reCommentCount
                                    },{})
                            }else{
                                itemView.layout_more_comment.visibility = View.GONE
                            }

                            mCommentReAdapter = CommentReAdapter(
                                commentList,
                                context!!,
                                feedViewModel,
                                object : CommentReAdapter.OnLastIndexListener {
                                    override fun OnLastIndex(last_index: Boolean) {
                                    }
                                },
                                object : CommentReAdapter.OnClickViewListener {
                                    override fun onClickView(comment: Comment) {
                                        val lintent = Intent(context, ReCommentActivity::class.java)
                                        lintent.putExtra("comment_seq", item.comment_seq.toString())
//                                    lintent.putExtra("comment_writer_seq" , item.writer_seq.toString())
                                        lintent.putExtra("feed_seq", comment.feed_seq.toString())
                                        lintent.putExtra(
                                            "re_comment_seq",
                                            comment.comment_seq.toString()
                                        )
                                        lintent.putExtra(
                                            "re_comment_writer",
                                            comment.writer.toString()
                                        )
                                        lintent.putExtra(
                                            "re_comment_writer_seq",
                                            comment.writer_seq.toString()
                                        )
                                        lintent.putExtra(
                                            "re_comment_text",
                                            comment.comment.toString()
                                        )

                                        (context as Activity).startActivity(lintent)
                                        (context as Activity).overridePendingTransition(
                                            R.anim.fragment_fade_in,
                                            R.anim.fragment_fade_out
                                        )
                                    }
                                })

                            itemView.rv_recomment.apply {
                                this.layoutManager = linearLayoutManager
//                            this.itemAnimator = DefaultItemAnimator()
                                this.adapter = mCommentReAdapter
                            }
                        }

                },{
                    Log.d("get comment 실패 = ",it.message.toString())
                })


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
                    val lintent = Intent(context, ReCommentActivity::class.java)
                    lintent.putExtra("comment_seq" , item.comment_seq.toString())
                    lintent.putExtra("feed_seq",item.feed_seq.toString())

//                    lintent.putExtra("re_comment_writer", item.writer.toString())
//                    lintent.putExtra("re_comment_writer_seq", item.writer_seq.toString())
//                    lintent.putExtra("re_comment_text",item.comment.toString())

//                    lintent.putExtra("ReCommentFragment", true)
//                    lintent.putExtra(FeedActivity.EXTRA_POSITION, position)
//                        context.transitionName = position.toString()
                    (context as Activity).startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )

                }
            }

            with(itemView.tv_comment_button){
                setOnClickListener {
                    val lintent = Intent(context, ReCommentActivity::class.java)
                    lintent.putExtra("comment_seq" , item.comment_seq.toString())
                    lintent.putExtra("feed_seq",item.feed_seq.toString())
                    (context as Activity).startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )

                }
            }


            var favCount : String
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()

            with(itemView.favorite_btn) {

                if (preferences.contains("checked_comment" + item.comment_seq) &&
                    preferences.getBoolean("checked_comment" +item.comment_seq, false) == true)
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
                            editor.putBoolean("checked_comment" + item.comment_seq, true)
                            editor.apply()
                        } else {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "false")
                            favCount = item.like_no?.minus(1).toString()
                            editor.putBoolean("checked_comment" + item.comment_seq, false)
                            editor.apply()
                        }
                        itemView.like_count.text = favCount
                    }
                } else {
                    setOnCheckedChangeListener { compoundButton, b ->
                        if (this.isChecked) {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "true")
                            favCount = item.like_no?.plus(1).toString()
                            editor.putBoolean("checked_comment" + item.comment_seq, true)
                            editor.apply()
                        } else {
                            feedViewModel.increaseLikeCommentCount(item.comment_seq, "false")
                            favCount = item.like_no.toString()
                            editor.putBoolean("checked_comment" + item.comment_seq, false)
                            editor.apply()
                        }
                        itemView.like_count.text = favCount
                    }
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


    }

    internal class LoadingViewHolder(itemView: View,isLoadingAdded:Boolean):RecyclerView.ViewHolder(itemView){
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



    interface OnLastIndexListener{
        fun OnLastIndex(last_index: Boolean)
    }

    private fun showDeletePopup(m_seq: String){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup,null)
        val textView: TextView = view.findViewById(R.id.textView)

        textView.text = "대화방에서 나가시겠습니까?"

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("대화방 삭제")
            .setPositiveButton("네"){
                    dialog,which -> Toast.makeText(context,"삭제하기", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->

            })
            .create()

        // remaining_time - 00:00~~10:00 ,  elapsed - 10:00 ~~ 00:00
        // p1PlayTime = 00:00~ 시작, elapsed

        alertDialog.setView(view)
        alertDialog.show()



        val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        if(btn_color != null){
            btn_color.setTextColor(context.resources.getColor(R.color.main_Accent))
        }
        if(btn_color_cancel != null){
            btn_color_cancel.setTextColor(context.resources.getColor(R.color.main_Accent))

        }

    }


}