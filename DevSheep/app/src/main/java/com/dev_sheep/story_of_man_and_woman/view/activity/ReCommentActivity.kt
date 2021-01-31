package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
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
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentReAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.calculateTime
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recomment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat

class ReCommentActivity : AppCompatActivity() {

    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()

    lateinit var contexts : Context
    lateinit var editText : EditText
    lateinit var comment_seq : String
    private var feed_seq : Int? = null
    //    private var writer_seq : String? = null
    private var group_seq : Int? = null
    private var depth : Int? = null


    lateinit var m_seq : String
    lateinit var recyclerview_recomments: RecyclerView
    lateinit var mCommentAdapter : CommentReAdapter
    lateinit var iv_back : ImageView
    lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private var limit: Int = 10
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var nestedScrollView: NestedScrollView? = null
    private var progressBar : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recomment)

        recyclerview_recomments = findViewById(R.id.recyclerview_recomments)
        iv_back = findViewById(R.id.im_back)

//        mSwipeRefreshLayout = findViewById<View>(R.id.sr_refresh) as SwipeRefreshLayout
        mShimmerViewContainer = findViewById<View>(R.id.shimmer_view_container) as ShimmerFrameLayout
        nestedScrollView = findViewById(R.id.nestedScrollView_comment)
        progressBar = findViewById(R.id.progressBar)

        if(intent.hasExtra("comment_seq")) {
            comment_seq = intent.getStringExtra("comment_seq")
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
                    feedViewModel.addReComment(
                        Integer.parseInt(comment_seq),
                        feed_seq!!,
                        m_seq!!,
                        group_seq!!,
                        depth!!,
                        editText.text.toString()
                    )
                    Toast.makeText(this, "완료.", Toast.LENGTH_SHORT).show();

                    onBackPressed()
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
        var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val single = APIService.FEED_SERVICE.getCommentItem(Integer.parseInt(comment_seq))
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                tv_m_nick.text = it.writer
                tv_comment.text = it.comment
                tv_age.text = it.writer_age
                tv_gender.text = it.writer_gender
                tv_feed_date.text = calculateTime(sdf.parse(it.comment_date))
                like_count.text = it.like_no.toString()


                feed_seq = Integer.parseInt(it.feed_seq)
                group_seq = it.group_seq
                depth= it.depth

                editText.setText("@"+it.writer+" ")
                editText.setSelection(et_comment.text.length)

                Glide.with(this)
                    .load(it.writer_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .into(img_profile)


                val single_recomment = APIService.FEED_SERVICE.getReComment(Integer.parseInt(it.feed_seq!!),it.group_seq!!,offset,limit)
                single_recomment.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        if(it.size > 0) {
                            mCommentAdapter = CommentReAdapter(it, this, feedViewModel,object : CommentReAdapter.OnLastIndexListener{
                                override fun OnLastIndex(last_index: Boolean) {
                                    InfinityScroll(last_index)
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

                    },{
                        mShimmerViewContainer.visibility = View.GONE
                        Log.e("get comment 실패 = ",it.message.toString())

                    })

            },{
                mShimmerViewContainer.visibility = View.GONE
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

            Log.e("more feed_seq!!!",""+feed_seq)
            Log.e("more group_seq!!!",""+group_seq)

            val single_recomment = APIService.FEED_SERVICE.getReComment(feed_seq!!,group_seq!!,offset,addLimit())

            single_recomment.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mCommentAdapter = CommentReAdapter(it, this, feedViewModel,object :CommentReAdapter.OnLastIndexListener{
                        override fun OnLastIndex(last_index: Boolean) {
                            InfinityScroll(last_index)
                        }

                    })

                    recyclerview_recomments?.apply {
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
        mShimmerViewContainer?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        initData()
        mShimmerViewContainer?.startShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 15
        return limit
    }

}