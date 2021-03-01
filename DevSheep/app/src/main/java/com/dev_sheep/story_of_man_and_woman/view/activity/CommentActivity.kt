package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel

class CommentActivity : AppCompatActivity(){

    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()

    lateinit var contexts : Context
    lateinit var editText : EditText
    lateinit var feed_seq : String
    lateinit var feed_creater: String
    lateinit var feed_title: String
    lateinit var m_seq : String
    lateinit var recyclerview_comments: RecyclerView
    lateinit var mCommentAdapter : CommentAdapter
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mShimmerViewContainer: ShimmerFrameLayout
    lateinit var iv_back : ImageView
    private var empty : View? = null
    private var limit: Int = 15
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    lateinit var tv_empty: TextView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var shimmer_view_container_comment: ShimmerFrameLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)


        if(intent.hasExtra("feed_seq")) {
            feed_seq = intent.getStringExtra("feed_seq")
        }
        if(intent.hasExtra("feed_creater")) {
            feed_creater = intent.getStringExtra("feed_creater")
        }
        if(intent.hasExtra("feed_title")) {
            feed_title = intent.getStringExtra("feed_title")
        }

        recyclerview_comments = findViewById(R.id.recyclerview_comments)
        iv_back = findViewById(R.id.im_back)

        empty = findViewById(R.id.empty)
        tv_empty = findViewById(R.id.emptyText) as TextView
        shimmer_view_container_comment = findViewById(R.id.shimmer_view_container_comment)
        tv_empty.setText(R.string.empty)

        val layoutManager = GridLayoutManager(this, 1)
        recyclerview_comments?.layoutManager = layoutManager

        editText = findViewById(R.id.et_comment)
        editText.requestFocus();

        // my_m_seq 가져오기
        val preferences: SharedPreferences = this.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")



        initData()

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
                    feedViewModel.addComment(
                        m_seq,
                        Integer.parseInt(feed_seq),
                        editText.text.toString()
                    )
                    memberViewModel.addNotifiaction(
                        m_seq,
                        feed_creater,
                        Integer.parseInt(feed_seq),
                        "댓글알림",
                        "님이 '\' " + feed_title + " '\' 에 댓글을 남겼습니다."+
                                "  -  "+editText.text.toString()
                    )
                    Log.e("이모지 데이터",""+editText.toString())
                    Toast.makeText(this, "댓글등록.", Toast.LENGTH_SHORT).show();

                    initData()
                    editText.setText("")

                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun initData(){


        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val single = APIService.FEED_SERVICE.getComment(Integer.parseInt(feed_seq),offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.size > 0) {
                    mCommentAdapter = CommentAdapter(it, this, feedViewModel,memberViewModel,object :CommentAdapter.OnLastIndexListener{
                        override fun OnLastIndex(last_index: Boolean) {
                            if(last_index == false){
                                EndlessScroll(false)
                            }else if(last_index == true){
                                EndlessScroll(true)
                            }

                        }

                    })
                    handlerFeed.postDelayed({
                        shimmer_view_container_comment?.stopShimmerAnimation()
                        shimmer_view_container_comment?.visibility = View.GONE
                        recyclerview_comments?.apply {
//                            var linearLayoutMnager = LinearLayoutManager(this.context)
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mCommentAdapter
                        }
                    },1000)

                }else{
                    shimmer_view_container_comment?.stopShimmerAnimation()
                    shimmer_view_container_comment?.visibility = View.GONE
                    empty!!.visibility = View.VISIBLE
                }

            },{
                shimmer_view_container_comment?.visibility = View.GONE
                empty!!.visibility = View.VISIBLE
                Log.e("get comment 실패 = ",it.message.toString())

            })


        iv_back.apply {
            setOnClickListener {
                onBackPressed()
            }
        }
    }
    fun EndlessScroll(isLoading: Boolean){
        // 무한스크롤

        recyclerview_comments!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        val single = APIService.FEED_SERVICE.getComment(Integer.parseInt(feed_seq),offset,addLimit())
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



    override fun onResume() {
        super.onResume()
        initData()
        shimmer_view_container_comment?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        initData()
        shimmer_view_container_comment?.startShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 15
        return limit
    }

}