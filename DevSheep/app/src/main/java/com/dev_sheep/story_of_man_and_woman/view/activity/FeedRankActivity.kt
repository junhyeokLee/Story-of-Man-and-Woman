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
import android.widget.*
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

class FeedRankActivity: AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    CompoundButton.OnCheckedChangeListener {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_rank)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        sr_refresh.setOnRefreshListener(this)
        sr_refresh.setColorSchemeResources(R.color.main_Accent)
        // my_m_seq 가져오기
        val preferences: SharedPreferences = this!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")!!
        my_Age = preferences.getString("inputAge", "")!!

        initData()
    }

    @SuppressLint("CheckResult")
    private fun initData(){

        if(intent.hasExtra("now")) {
            tv_name = intent.getStringExtra("now")!!
            tv_tag_rank_name.text = tv_name
            cb_now.isChecked = true
            cb_now.setTextColor(resources.getColor(R.color.white))
            tagSelectList("지금,")
        }


        cb_now.setOnCheckedChangeListener(this)
        cb_week.setOnCheckedChangeListener(this)
        cb_woman.setOnCheckedChangeListener(this)
        cb_man.setOnCheckedChangeListener(this)
        cb_10.setOnCheckedChangeListener(this)
        cb_20.setOnCheckedChangeListener(this)
        cb_30.setOnCheckedChangeListener(this)
        cb_40.setOnCheckedChangeListener(this)
        cb_50.setOnCheckedChangeListener(this)

    }


    fun tagSelectList(tagName:String){

        // display loading indicator
        val handlerFeed: Handler = Handler(Looper.myLooper()!!)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        if(tagName.equals("지금,")){
        single = FEED_SERVICE.getTodayList(offset,limit)
        }else if(tagName.equals("이번 주,")){
        single = FEED_SERVICE.getWeekList(offset,limit)
        }else if(tagName.equals("여자들의,")){
        single = FEED_SERVICE.getWomanRankList(offset,limit)
        }else if(tagName.equals("남자들의,")){
        single = FEED_SERVICE.getManRankList(offset,limit)
        }else if(tagName.equals("10 대,")){
        single = FEED_SERVICE.get10AgeList(offset,limit)
        }else if(tagName.equals("20 대,")){
        single = FEED_SERVICE.get20AgeList(offset,limit)
        }else if(tagName.equals("30 대,")){
        single = FEED_SERVICE.get30AgeList(offset,limit)
        }else if(tagName.equals("40 대,")){
        single = FEED_SERVICE.get40AgeList(offset,limit)
        }else if(tagName.equals("50 대,")){
        single = FEED_SERVICE.get50AgeList(offset,limit)
        }

        shimmer_view_container.startShimmerAnimation()
        shimmer_view_container.visibility = View.VISIBLE

        single?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                if (it.size > 0) {
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
                            lintent.putExtra("tag_seq",feed.tag_seq)
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
                                EndlessScroll(false,tagName)
                            } else if (boolean_value == true) {
                                EndlessScroll(true,tagName)
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
                }else {
                    recyclerView.adapter = null
                    shimmer_view_container?.visibility = View.GONE
                    Toast.makeText(applicationContext, "이번주 "+tagName+" 피드가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                }

            }, {
                recyclerView.adapter = null
                shimmer_view_container?.visibility = View.GONE
                Toast.makeText(applicationContext, "이번주 "+tagName+" 피드가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()

            })

        }

    fun EndlessScroll(isLoading: Boolean,tagName: String){
        // 무한스크롤

        if(tagName.equals("지금,")) {
            single = FEED_SERVICE.getTodayList(offset,addLimit())
        } else if(tagName.equals("이번 주,")) {
            single = FEED_SERVICE.getWeekList(offset,addLimit())
        } else if(tagName.equals("여자들의,")) {
            single = FEED_SERVICE.getWomanRankList(offset,addLimit())
        } else if(tagName.equals("남자들의,")) {
            single = FEED_SERVICE.getManRankList(offset,addLimit())
        } else if(tagName.equals("10 대,")) {
            single = FEED_SERVICE.get10AgeList(offset,addLimit())
        } else if(tagName.equals("20 대,")) {
            single = FEED_SERVICE.get20AgeList(offset,addLimit())
        } else if(tagName.equals("30 대,")) {
            single = FEED_SERVICE.get30AgeList(offset,addLimit())
        } else if(tagName.equals("40 대,")) {
            single = FEED_SERVICE.get40AgeList(offset,addLimit())
        } else if(tagName.equals("50 대,")) {
            single = FEED_SERVICE.get50AgeList(offset,addLimit())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item!!)
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
        shimmer_view_container.stopShimmerAnimation()
        shimmer_view_container.visibility = View.GONE
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
    }

    private fun addOffset(): Int{
        offset += 10
        return offset
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if(buttonView?.id == R.id.cb_now){
            if(isChecked){
                tagSelectList("지금,")
                tv_tag_rank_name.text = "지금,"
                tv_rank_name.text = "지금,"
                cb_now.setTextColor(resources.getColor(R.color.white))
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_now.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_week){
            if(isChecked){
                tagSelectList("이번 주,")
                tv_tag_rank_name.text = "이번 주,"
                tv_rank_name.text = "이번 주,"
                cb_week.setTextColor(resources.getColor(R.color.white))
                cb_now.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_week.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_woman){
            if(isChecked){
                tagSelectList("여자들의,")
                tv_tag_rank_name.text = "여자들의,"
                tv_rank_name.text = "여자들의,"

                cb_woman.setTextColor(resources.getColor(R.color.white))
                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_woman.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_man){
            if(isChecked){
                tagSelectList("남자들의,")
                tv_tag_rank_name.text = "남자들의,"
                tv_rank_name.text = "남자들의,"

                cb_man.setTextColor(resources.getColor(R.color.white))
                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_man.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_10){
            if(isChecked){
                tagSelectList("10 대,")
                tv_tag_rank_name.text = "10 대,"
                tv_rank_name.text = "10 대,"

                cb_10.setTextColor(resources.getColor(R.color.white))

                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_10.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_20){
            if(isChecked){
                tagSelectList("20 대,")
                tv_tag_rank_name.text = "20 대,"
                tv_rank_name.text = "20 대,"

                cb_20.setTextColor(resources.getColor(R.color.white))

                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_20.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_30){
            if(isChecked){
                tagSelectList("30 대,")
                tv_tag_rank_name.text = "30 대,"
                tv_rank_name.text = "30 대,"
                cb_30.setTextColor(resources.getColor(R.color.white))

                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_40.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_30.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_40){
            if(isChecked){
                tagSelectList("40 대,")
                tv_tag_rank_name.text = "40 대,"
                tv_rank_name.text = "40 대,"
                cb_40.setTextColor(resources.getColor(R.color.white))

                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_50.isChecked = false
            }else{
                cb_40.setTextColor(resources.getColor(R.color.black))

            }
        }else if(buttonView?.id == R.id.cb_50){
            if(isChecked){
                tagSelectList("50 대,")
                tv_tag_rank_name.text = "50 대,"
                tv_rank_name.text = "50 대,"
                cb_50.setTextColor(resources.getColor(R.color.white))

                cb_now.isChecked = false
                cb_week.isChecked = false
                cb_woman.isChecked = false
                cb_man.isChecked = false
                cb_10.isChecked = false
                cb_20.isChecked = false
                cb_30.isChecked = false
                cb_40.isChecked = false
            }else{
                cb_50.setTextColor(resources.getColor(R.color.black))

            }
        }
    }

//    fun getLoad(): Boolean{
//        return isLoading
//    }



}