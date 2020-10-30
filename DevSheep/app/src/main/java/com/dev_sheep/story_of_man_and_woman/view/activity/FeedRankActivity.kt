package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapterRank
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapterTag
import com.dev_sheep.story_of_man_and_woman.view.dialog.FilterDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed_rank.*
import kotlinx.android.synthetic.main.activity_feed_search.*
import kotlinx.android.synthetic.main.activity_feed_search.recyclerView
import kotlinx.android.synthetic.main.activity_feed_search.shimmer_view_container
import kotlinx.android.synthetic.main.activity_feed_search.sr_refresh
import kotlinx.android.synthetic.main.activity_feed_search.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedRankActivity: AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener{

    //    lateinit var tag_seq: String
    lateinit var tv_name: String
    lateinit var mFeedAdapterRank: FeedAdapterRank
    private val feedViewModel: FeedViewModel by viewModel()
    private lateinit var m_seq : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_rank)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "#"
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        sr_refresh.setOnRefreshListener(this)
        sr_refresh.setColorSchemeResources(
            R.color.main_Accent
        );

        // my_m_seq 가져오기
        val preferences: SharedPreferences = this!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")

//        if(intent.hasExtra("tag_seq")) {
//            tag_seq = intent.getStringExtra("tag_seq")
//            Log.e("tag_seq", tag_seq)
//        }
        if(intent.hasExtra("tv_title")) {
            tv_name = intent.getStringExtra("tv_title")
            Log.e("title", tv_name)
            tv_tag_rank_name.text = tv_name
        }

        initData()
    }



    private fun initData(){
        // display loading indicator
        val handlerFeed: Handler = Handler(Looper.myLooper())
        // tag_search
        val single = APIService.FEED_SERVICE.getList()
//        val single = APIService.FEED_SERVICE.getTagSearch(Integer.parseInt(tag_seq))
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.size == 0) {

                } else {
                    Log.e("FeedList", "" + it.get(0).content)

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

                    }, object : FeedAdapterRank.OnClickProfileListener {
                        override fun OnClickProfile(feed: Feed, tv: TextView, iv: ImageView) {

                            val trId = ViewCompat.getTransitionName(tv).toString()
                            val trId1 = ViewCompat.getTransitionName(iv).toString()

                            if (feed.creater_seq == m_seq) {
                                this@FeedRankActivity?.supportFragmentManager
                                    ?.beginTransaction()
                                    ?.addSharedElement(tv, trId)
                                    ?.addSharedElement(iv, trId1)
                                    ?.addToBackStack("ProfileImg")
                                    ?.replace(
                                        R.id.frameLayout,
                                        ProfileFragment.newInstance(feed, trId, trId1)
                                    )
                                    ?.commit()
                            } else {
                                this@FeedRankActivity?.supportFragmentManager
                                    ?.beginTransaction()
                                    ?.addSharedElement(tv, trId)
                                    ?.addSharedElement(iv, trId1)
                                    ?.addToBackStack("ProfileImg")
                                    ?.replace(
                                        R.id.frameLayout,
                                        ProfileUsersFragment.newInstance(feed, trId, trId1)
                                    )
                                    ?.commit()
                            }
                        }
                    })

                    handlerFeed.postDelayed({
                        // stop animating Shimmer and hide the layout
                        shimmer_view_container.stopShimmerAnimation()
                        shimmer_view_container.visibility = View.GONE
//                    progressBar?.visibility = View.GONE
                        recyclerView?.apply {
                            var linearLayoutMnager = LinearLayoutManager(this.context)
                            this.layoutManager = linearLayoutMnager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mFeedAdapterRank

                        }
                    }, 1000)
                }

            }, {
                Log.e("feed 보기 실패함", "" + it.message)
            })

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        super.onCreateOptionsMenu(menu)
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu_search, menu)
//        return true
//    }

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
}