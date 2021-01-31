package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
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
    private var nestedScrollView: NestedScrollView? = null
    private var progressBar : ProgressBar? = null

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
        nestedScrollView = findViewById(R.id.nestedScrollView_comment)
        progressBar = findViewById(R.id.progressBar)
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
                        "님이 '\'" + feed_title + " '\' 에 댓글을 남겼습니다."
                    )

                    Toast.makeText(this, "댓글등록.", Toast.LENGTH_SHORT).show();

                    this.onBackPressed()
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
                    mCommentAdapter = CommentAdapter(it, this, feedViewModel,object :CommentAdapter.OnLastIndexListener{
                        override fun OnLastIndex(last_index: Boolean) {
                            InfinityScroll(last_index)
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


    private fun InfinityScroll(last_index:Boolean){
        // 무한스크롤
        nestedScrollView?.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (v?.getChildAt(v.getChildCount() - 1) != null) {

                    if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight() && scrollY > oldScrollY
                        && last_index == false) {
                        visibleItemCount = linearLayoutManager.getChildCount()
                        totalItemCount = linearLayoutManager.getItemCount()
                        lastVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                        if (visibleItemCount + lastVisibleItemPosition >= totalItemCount)
                        {
                            progressBar?.visibility = View.VISIBLE
                            LoadMoreData()

                        }

                    } else if(last_index == true){
                        progressBar?.visibility = View.GONE
                    }
                }
            }

        })
    }

    private fun LoadMoreData() {
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        Handler().postDelayed({
            val single = APIService.FEED_SERVICE.getComment(Integer.parseInt(feed_seq),offset,addLimit())
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    mCommentAdapter = CommentAdapter(it, this, feedViewModel,object :CommentAdapter.OnLastIndexListener{
                        override fun OnLastIndex(last_index: Boolean) {
                            InfinityScroll(last_index)
                        }
                    })

                    recyclerview_comments?.apply {
                        this.layoutManager = linearLayoutManager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mCommentAdapter
                    }

                }, {
                    Log.d("스크롤 보기 실패함", "" + it.message)
                })
            progressBar?.visibility = View.GONE
        }, 1000)
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