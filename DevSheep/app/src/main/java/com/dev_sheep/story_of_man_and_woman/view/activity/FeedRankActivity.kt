package com.dev_sheep.story_of_man_and_woman.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapterRank
import com.dev_sheep.story_of_man_and_woman.view.adapter.Tag_Select_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed_rank.*
import kotlinx.android.synthetic.main.activity_feed_rank.sr_refresh
import kotlinx.android.synthetic.main.activity_feed_rank.toolbar
import kotlinx.android.synthetic.main.activity_feed_rank.shimmer_view_container
import kotlinx.android.synthetic.main.activity_feed_rank.recyclerView
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.sign

class FeedRankActivity: AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener{

    //    lateinit var tag_seq: String
    lateinit var tv_name: String
    lateinit var mFeedAdapterRank: FeedAdapterRank
    private val feedViewModel: FeedViewModel by viewModel()
    private lateinit var m_seq : String
    private lateinit var my_Age : String
    private var single : Single<MutableList<Feed>>? = null
    private var limit: Int = 50
    private var offset: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager

    var CHECKED_TAG_SEQ = ""
    var CHECKED_TAG_NAME = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_rank)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        sr_refresh.setOnRefreshListener(this)
        sr_refresh.setColorSchemeResources(
            R.color.main_Accent
        )
        // my_m_seq 가져오기
        val preferences: SharedPreferences = this!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")
        my_Age = preferences.getString("inputAge", "")

        if(intent.hasExtra("now")) {
            tv_name = intent.getStringExtra("now")
            tv_tag_rank_name.text = tv_name
        }

        initData()
    }

    @SuppressLint("CheckResult")
    private fun initData(){


//        val layoutManager_Tag = GridLayoutManager(this, 11)
//        layoutManager_Tag.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                val gridPosition = position % 5
//                when (gridPosition) {
//                    0, 1, 2 -> return 4
//                    3, 4 -> return 5
//                }
//                return 0
//            }
//        })
//        recyclerView_tag.layoutManager = layoutManager_Tag
//        feedViewModel.getTagList()
//        //라이브데이터
//        feedViewModel.listTagOfFeed.observe(this, Observer(function = fun(tagList: MutableList<Tag>?) {
//            tagList?.let {
//                if (it.isNotEmpty()) {
//                    recyclerView_tag.layoutManager = layoutManager_Tag
//                    recyclerView_tag.adapter = Tag_Select_Adapter(
//                        it,
//                        this,
//                        object : Tag_Select_Adapter.OnTagCheckedSeq {
//                            override fun getTagCheckedSeq(tag_seq: String, tag_name: String) {
//                                CHECKED_TAG_SEQ = tag_seq
//                                CHECKED_TAG_NAME = tag_name
//                            }
//
//                        })
//                } else {
////                        progressBar_tag.visibility = View.VISIBLE
//                }
//            }
//        }))


        // display loading indicator
        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        // tag_search
        Log.e("tv_name",""+tv_name)

//        if(tv_name.equals("지금,")) {
//            single = FEED_SERVICE.getTodayList(offset,limit)
//        } else if(tv_name.equals(my_Age+" 여성들이 좋아하는")) {
//         single = FEED_SERVICE.getAgeWomanRecommendList(my_Age,offset,limit)
//        } else if(tv_name.equals(my_Age+" 남성들이 좋아하는")) {
//        single = FEED_SERVICE.getAgeManRecommendList(my_Age,offset,limit)
//        } else if(tv_name.equals("가장 많이 읽은 카드")) {
//            single = FEED_SERVICE.getViewRecommendList(offset,limit)
//        } else if(tv_name.equals("가장 많이 좋아한 카드")) {
//            single = FEED_SERVICE.getLikeRecommendList(offset,limit)
//        }
        single = FEED_SERVICE.getTodayList(offset,limit)
        single?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if (it.size == 0) {
                    shimmer_view_container?.visibility = View.GONE
                } else {
                    Log.e("FeedList", "" + it.toString())

                    mFeedAdapterRank = FeedAdapterRank(it, this, object : FeedAdapterRank.OnClickViewListener {
                        override fun OnClickFeed(
                            feed: Feed,
                            tv: TextView,
                            iv: ImageView,
                            cb: CheckBox,
                            cb2: CheckBox,
                            position: Int
                        ) {
                            feedViewModel.increaseViewCount(feed.feed_seq)

                            val lintent = Intent(applicationContext, FeedActivity::class.java)
                            lintent.putExtra("feed_seq", feed.feed_seq)
                            lintent.putExtra("checked" + feed.feed_seq, cb.isChecked)
                            lintent.putExtra("creater_seq", feed.creater_seq)
                            lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                            lintent.putExtra(FeedActivity.EXTRA_POSITION, position)
//                        context.transitionName = position.toString()
                            startActivity(lintent)
                            overridePendingTransition(
                                R.anim.fragment_fade_in,
                                R.anim.fragment_fade_out
                            )

                        }
                    }, object : FeedAdapterRank.OnClickLikeListener {
                        override fun OnClickFeed(feed_seq: Int, boolean_value: String) {
                            feedViewModel.increaseLikeCount(feed_seq, boolean_value)
                        }

                    }, object : FeedAdapterRank.OnClickBookMarkListener {
                        override fun OnClickBookMark(
                            m_seq: String,
                            feed_seq: Int,
                            boolean_value: String
                        ) {
                            feedViewModel.onClickBookMark(m_seq, feed_seq, boolean_value)
                        }

                    },object :FeedAdapterRank.OnEndlessScrollListener{
                        override fun OnEndless(boolean_value: Boolean) {
                            if (boolean_value == false) {
                                EndlessScroll(false)
                            } else if (boolean_value == true) {
                                EndlessScroll(true)
                            }
                        }

                    })

                    handlerFeed.postDelayed({
                        // stop animating Shimmer and hide the layout
                        shimmer_view_container.stopShimmerAnimation()
                        shimmer_view_container.visibility = View.GONE
                        recyclerView?.apply {
                            linearLayoutManager = LinearLayoutManager(this.context)
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mFeedAdapterRank

                        }
                    }, 1000)
                }

            }, {
                Log.e("feed 보기 실패함", "" + it.message)
            })


    }

    fun EndlessScroll(isLoading: Boolean){
        // 무한스크롤
        if(tv_name.equals("오늘의 관심사")) {
            single = FEED_SERVICE.getTodayList(offset,addLimit())
        } else if(tv_name.equals(my_Age+" 여성들이 좋아하는")) {
            single = FEED_SERVICE.getAgeWomanRecommendList(my_Age,offset,addLimit())
        } else if(tv_name.equals(my_Age+" 남성들이 좋아하는")) {
            single = FEED_SERVICE.getAgeManRecommendList(my_Age,offset,addLimit())
        } else if(tv_name.equals("가장 많이 읽은 카드")) {
            single = FEED_SERVICE.getViewRecommendList(offset,addLimit())
        } else if(tv_name.equals("가장 많이 좋아한 카드")) {
            single = FEED_SERVICE.getLikeRecommendList(offset,addLimit())
        }

        recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        single?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.subscribe({
                                mFeedAdapterRank.updateList(it)
                            }, {
                                Log.d("Error MoreData", it.message.toString())
                            })
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        when(item?.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
        }
        return true
    }

    override fun onRefresh() {
        initData()
        sr_refresh.setRefreshing(false)
    }

    override fun onResume() {
        super.onResume()
        initData()
        shimmer_view_container.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        initData()
        shimmer_view_container.stopShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
    }

    private fun addOffset(): Int{
        offset += 10
        return offset
    }

//    fun getLoad(): Boolean{
//        return isLoading
//    }



}