package com.dev_sheep.story_of_man_and_woman.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Comment
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentReAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.OnClickViewListener
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_re_comment.view.*
import kotlinx.android.synthetic.main.fragment_recomment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class ReCommentActivity : AppCompatActivity() {

    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()

    lateinit var contexts : Context
    lateinit var editText : EditText

    private var comment_seq : String? = null
//    private var comment_writer_seq: String? = null
    private var re_comment_seq: String? = null
    private var re_comment_writer: String? = null
    private var re_comment_writer_seq: String? = null
    private var re_comment_text: String? = null

    private var feed_seq: String? = null
    private var group_seq : Int? = null
    private var depth : Int? = null


    lateinit var m_seq : String
    lateinit var recyclerview_recomments: RecyclerView
    lateinit var mCommentAdapter : CommentReAdapter
    lateinit var iv_back : ImageView
    lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private var limit: Int = 50
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recomment)
        contexts = this

        recyclerview_recomments = findViewById(R.id.recyclerview_recomments)
        iv_back = findViewById(R.id.im_back)

//        mSwipeRefreshLayout = findViewById<View>(R.id.sr_refresh) as SwipeRefreshLayout
        mShimmerViewContainer = findViewById<View>(R.id.shimmer_view_container) as ShimmerFrameLayout

        if(intent.hasExtra("comment_seq")) {
            comment_seq = intent.getStringExtra("comment_seq")
        }

        if(intent.hasExtra("re_comment_seq")) {
            re_comment_seq = intent.getStringExtra("re_comment_seq")
        }
        if(intent.hasExtra("re_comment_writer")) {
            re_comment_writer = intent.getStringExtra("re_comment_writer")
        }
        if(intent.hasExtra("re_comment_writer_seq")) {
            re_comment_writer_seq = intent.getStringExtra("re_comment_writer_seq")
        }
        if(intent.hasExtra("re_comment_text")) {
            re_comment_text = intent.getStringExtra("re_comment_text")
        }
        if(intent.hasExtra("feed_seq")){
            feed_seq = intent.getStringExtra("feed_seq")
        }

        val layoutManager = GridLayoutManager(this, 1)
        recyclerview_recomments?.layoutManager = layoutManager

        editText = findViewById(R.id.et_comment)

        editText.requestFocus();


        // my_m_seq 가져오기
        val preferences: SharedPreferences = this.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")!!


        initData()

    }
    private fun initData(){


        val handlerFeed: Handler = Handler(Looper.myLooper()!!)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        feedViewModel.getCommentItem(Integer.parseInt(comment_seq))
        //라이브데이터
        feedViewModel.commentOfFeed.observe(this, androidx.lifecycle.Observer {
            tv_m_nick.text = it.writer
            tv_comment.text = it.comment
            tv_age.text = it.writer_age
            tv_gender.text = it.writer_gender
            tv_feed_date.text = calculateTime(sdf.parse(it.comment_date))
            like_count.text = it.like_no.toString()
            if(re_comment_seq == null || re_comment_writer == null || re_comment_writer_seq == null) {
                editText.setText("@" + it.writer + " ")
                getEditText(it.comment_seq.toString(),it.feed_seq.toString()!!,it.writer_seq!!,it.comment!!)

            }else{
                editText.setText("@" + re_comment_writer + " ")
                getEditText(re_comment_seq.toString(),feed_seq.toString()!!,re_comment_writer_seq!!,re_comment_text!!)

            }
            editText.setSelection(et_comment.text.length)
            feed_seq = it.feed_seq
            group_seq = it.group_seq
            depth= it.depth

            if(!this.isFinishing()) {
                Glide.with(this)
                    .load(it.writer_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(getDrawable(R.drawable.user))
                    .into(img_profile)
            }

            feedViewModel.getReComment(Integer.parseInt(it.feed_seq)!!,it.group_seq!!,offset,limit)
            //라이브데이터
            feedViewModel.listCommentOfFeed.observe(this, androidx.lifecycle.Observer(function = fun(commentList:MutableList<Comment>) {
                if(commentList.size > 0) {
                    mCommentAdapter = CommentReAdapter(commentList, this, feedViewModel,object : CommentReAdapter.OnLastIndexListener{
                        override fun OnLastIndex(last_index: Boolean) {
                            if(last_index == false){
                                EndlessScroll(false,group_seq!!)
                            }else if(last_index == true){
                                EndlessScroll(true,group_seq!!)
                            }
                        }

                    },object : CommentReAdapter.OnClickViewListener{
                        override fun onClickView(comment: Comment) {
                            editText.setText("@" + comment.writer + " ")
                            editText.setSelection(et_comment.text.length)
                            getEditText(comment.comment_seq.toString(),comment.feed_seq.toString(),comment.writer_seq.toString(),comment.comment.toString())
                        }
                    })
                    handlerFeed.postDelayed({
                        // stop animating Shimmer and hide the layout
                        mShimmerViewContainer.stopShimmerAnimation()
                        mShimmerViewContainer.visibility = View.GONE

                        recyclerview_recomments.apply {
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mCommentAdapter
                        }
                    }, 1000)
                }else{
                    mShimmerViewContainer.stopShimmerAnimation()
                    mShimmerViewContainer.visibility = View.GONE
                }
            }))

        })

        iv_back.apply {
            setOnClickListener {
                onBackPressed()
            }
        }
    }

    fun EndlessScroll(isLoading: Boolean,group_seq:Int){
        // 무한스크롤

        recyclerview_recomments!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        val single = APIService.FEED_SERVICE.getReComment(Integer.parseInt(feed_seq),group_seq,offset,addLimit())
                        single.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mCommentAdapter.updateList(it)
                            }, {
                                Log.d("Error MoreData", it.message.toString())
                            })
                    }
                }
            }
        })
    }


    private fun getEditText(comment_seq:String,feed_seq:String,writer_seq:String,comment_text:String){
        editText.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= editText.getRight() - editText.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    feedViewModel.addReComment(
                        Integer.parseInt(comment_seq),
                        Integer.parseInt(feed_seq),
                        m_seq!!,
                        group_seq!!,
                        depth!!,
                        editText.text.toString()
                    )
                    // 댓글단 사람의
                    if(!writer_seq.equals(m_seq)) {
                        memberViewModel.addNotifiaction(m_seq, writer_seq, Integer.parseInt(feed_seq), "대댓글 알림", "님이 '\' " + comment_text + " '\' 에 댓글을 남겼습니다."+ "  -  "+editText.text.toString())
                        memberViewModel.memberPush(writer_seq,m_seq, "feedrecomment")
                    }

                    Toast.makeText(this, "댓글등록.", Toast.LENGTH_SHORT).show();

                    initData()
                    editText.setText("")
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    override fun onResume() {
        super.onResume()
//        initData()
        mShimmerViewContainer?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        mShimmerViewContainer?.startShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
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