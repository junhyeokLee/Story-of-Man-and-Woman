package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedCardAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedRankAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.FilterDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import eu.micer.circlesloadingindicator.CirclesLoadingIndicator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.relex.circleindicator.CircleIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val feedViewModel: FeedViewModel by viewModel()

    private lateinit var m_seq : String
    private var recyclerView : RecyclerView? = null
    private var recyclerViewTag : RecyclerView? = null
    private var viewpager : ViewPager? = null
    private var viewpager_card : ViewPager? = null
    private var indicators: CircleIndicator? = null
    private var tollbar : androidx.appcompat.widget.Toolbar? = null
    private var progressBar : CirclesLoadingIndicator? = null
    lateinit var contexts : Context
    lateinit var mFeedAdapter: FeedAdapter
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mTagAdapter: Test_tag_Adapter
    private var mRankAdapter: FeedRankAdapter? = null
    lateinit var mFeedCardAdater: FeedCardAdapter
    lateinit var mShimmerViewContainer: ShimmerFrameLayout
    private var limit: Int = 10
    private var offset: Int = 0
    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 2
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0


    private var handler = Handler()
    private var delay : Long = 5000
    private var page : Int = 0
    var scrollCount: Int = 0
    private lateinit var layoutManagers: LinearLayoutManager // 태그 자동스크롤 위한 초기화 제한

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)
        contexts = view.context

        setHasOptionsMenu(true);

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        tollbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar?
        progressBar = view.findViewById<View>(R.id.progressBar) as CirclesLoadingIndicator?
        recyclerViewTag = view.findViewById<View>(R.id.recyclerView_tag) as RecyclerView?
        mSwipeRefreshLayout = view.findViewById<View>(R.id.sr_refresh) as SwipeRefreshLayout
        mShimmerViewContainer = view.findViewById<View>(R.id.shimmer_view_container) as ShimmerFrameLayout
        viewpager = view.findViewById<View>(R.id.vp) as ViewPager?
        viewpager_card = view.findViewById<View>(R.id.vp_feed_card) as ViewPager
        indicators = view.findViewById<CircleIndicator>(R.id.indicator)
        // 프래그먼트에 toolbar 세팅
        (activity as AppCompatActivity).setSupportActionBar(tollbar)

        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.main_Accent
        );


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")


        initData()
    }

    private fun initData(){


//        setTagAdapter()
        // 전체보기
        // display loading indicator
        val handlerFeed: Handler = Handler(Looper.myLooper())

        val single = FEED_SERVICE.getList()
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if(it.size > 0) {

                    mFeedAdapter =
                        FeedAdapter(it, contexts, object : FeedAdapter.OnClickViewListener {
                            override fun OnClickFeed(
                                feed: Feed,
                                tv: TextView,
                                iv: ImageView,
                                cb: CheckBox,
                                cb2: CheckBox,
                                position: Int
                            ) {
                                feedViewModel.increaseViewCount(feed.feed_seq)

                                val lintent = Intent(context, FeedActivity::class.java)
                                lintent.putExtra("feed_seq", feed.feed_seq)
                                lintent.putExtra("checked" + feed.feed_seq, cb.isChecked)
                                lintent.putExtra("creater_seq", feed.creater_seq)
                                lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                                lintent.putExtra(FeedActivity.EXTRA_POSITION, position)
//                        context.transitionName = position.toString()
                                (context as Activity).startActivity(lintent)
                                (context as Activity).overridePendingTransition(
                                    R.anim.fragment_fade_in,
                                    R.anim.fragment_fade_out
                                )

                            }
                        }, object : FeedAdapter.OnClickLikeListener {
                            override fun OnClickFeed(feed_seq: Int, boolean_value: String) {
                                feedViewModel.increaseLikeCount(feed_seq, boolean_value)
                            }

                        }, object : FeedAdapter.OnClickBookMarkListener {
                            override fun OnClickBookMark(
                                m_seq: String,
                                feed_seq: Int,
                                boolean_value: String
                            ) {
                                feedViewModel.onClickBookMark(m_seq, feed_seq, boolean_value)
                            }

                        }, object : FeedAdapter.OnClickProfileListener {
                            override fun OnClickProfile(feed: Feed, tv: TextView, iv: ImageView) {

                                val trId = ViewCompat.getTransitionName(tv).toString()
                                val trId1 = ViewCompat.getTransitionName(iv).toString()

                                if (feed.creater_seq == m_seq) {
                                    activity?.supportFragmentManager
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
                                    activity?.supportFragmentManager
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
                        mShimmerViewContainer.stopShimmerAnimation()
                        mShimmerViewContainer.visibility = View.GONE
//                    progressBar?.visibility = View.GONE
                        recyclerView?.apply {
                            var linearLayoutMnager = LinearLayoutManager(this.context)
                            this.layoutManager = linearLayoutMnager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mFeedAdapter

                            setViewPager(it)

                        }
                    }, 1000)
                }
            }, {
                Log.e("feed 보기 실패함", "" + it.message)
            })

        val single_tag = FEED_SERVICE.getTagList()
        single_tag.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if(it.size > 0) {

                    recyclerViewTag?.apply {
                        var recycler_this = this
                        layoutManagers = object : LinearLayoutManager(context) {
                            override fun smoothScrollToPosition(
                                recyclerView: RecyclerView,
                                state: RecyclerView.State?,
                                position: Int
                            ) {
                                val smoothScroller = object : LinearSmoothScroller(context) {
                                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                                        return 10.0f;
                                    }
                                }
                                smoothScroller.targetPosition = position
                                startSmoothScroll(smoothScroller)
                            }
                        }
                        mTagAdapter = object : Test_tag_Adapter(it, contexts) {
                            override fun load() {
                                if (layoutManagers.findFirstVisibleItemPosition() > 1) {
                                    mTagAdapter?.notifyItemMoved(0, it.size - 1)
                                }
                            }
                        }
                        layoutManagers.orientation = LinearLayoutManager.HORIZONTAL
                        recycler_this.layoutManager = layoutManagers
                        recycler_this.setHasFixedSize(true)
                        recycler_this.setItemViewCacheSize(10)
                        recycler_this.isDrawingCacheEnabled = true
                        recycler_this.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
                        recycler_this.adapter = mTagAdapter
                        autoScroll(it, mTagAdapter)

                    }
                }
            }, {
                Log.e("feed 보기2 실패함", "" + it.message)
            })

//        val call = testService.getList()
//        call.enqueue(object : Callback<List<Feed>?>{
//            override fun onFailure(call: Call<List<Feed>?>, t: Throwable) {
//                Log.e("errors",t.message.toString())
//            }
//
//            override fun onResponse(call: Call<List<Feed>?>, response: Response<List<Feed>?>) {
//                response?.body()?.let { tests: List<Feed> ->
//                    thread {
//                        Log.e("getFeed성공",tests.toString())
//                        Log.e("feed 0 title value = ",tests.get(0).title)
//
//                        mFeedAdapter = FeedAdapter(tests, contexts,childFragmentManager)
//                        recyclerView?.apply {
//                            var linearLayoutMnager = LinearLayoutManager(this.context)
//                            this.layoutManager = linearLayoutMnager
//                            adapter = mFeedAdapter
//                        }
//
//                        if (tests.isNotEmpty()) {
//                            progressBar?.visibility = View.GONE
//
//                        }else {
//                            progressBar?.visibility = View.VISIBLE
//                        }
//
//
//                    }
//                }
//            }
//
//        })

//        feedViewModel.getListFeed().observe(this, Observer {
//            setAdapter(it)
//        })

        // 페이징 처리
//        feedViewModel.getListFirst(limit,offset).observe(this, Observer { test ->
//            setAdapter(test)
//        })

    }

    private fun setViewPager(feed:List<Feed>){

        if(!isAdded()) return

        mRankAdapter = FeedRankAdapter(childFragmentManager)
        viewpager?.apply {
            this.adapter = mRankAdapter

            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    page = position
                }
            })
            indicators?.setViewPager(this)

        }

        mFeedCardAdater = FeedCardAdapter(feed,contexts,feedViewModel)
        viewpager_card?.apply {
            this.adapter = mFeedCardAdater
            this.setPadding(72, 0, 72, 0);

            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {

                }

                override fun onPageSelected(position: Int) {

                }
            })

        }
    }

//    private fun setTagAdapter(){
//
//        val call = testService.getTagList()
//        call.enqueue(object : Callback<List<Tag>?>{
//            override fun onFailure(call: Call<List<Tag>?>, t: Throwable) {
//                Log.e("errors",t.message.toString())
//            }
//
//            override fun onResponse(call: Call<List<Tag>?>, response: Response<List<Tag>?>) {
//                response?.body()?.let { tag: List<Tag> ->
//                    thread {
//                        Log.e("getTag성공",tag.toString())
//                        recyclerViewTag?.apply {
//                            var recycler_this = this
//                            layoutManagers = object : LinearLayoutManager(context) {
//                                override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
//                                    val smoothScroller = object : LinearSmoothScroller(context) {
//                                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
//                                            return 10.0f;
//                                        }
//                                    }
//                                    smoothScroller.targetPosition = position
//                                    startSmoothScroll(smoothScroller)
//                                }
//                            }
//                            mTagAdapter = object : Test_tag_Adapter(tag,contexts) {
//                                override fun load() {
//                                    if (layoutManagers.findFirstVisibleItemPosition() > 1) {
//                                        mTagAdapter?.notifyItemMoved(0, tag.size - 1)
//                                    }
//                                }
//                            }
//                            layoutManagers.orientation = LinearLayoutManager.HORIZONTAL
//                            recycler_this.layoutManager = layoutManagers
//                            recycler_this.setHasFixedSize(true)
//                            recycler_this.setItemViewCacheSize(10)
//                            recycler_this.isDrawingCacheEnabled = true
//                            recycler_this.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_LOW
//                            recycler_this.adapter = mTagAdapter
//                            autoScroll(tag, mTagAdapter)
//
//                        }
//                    }
//                }
//            }
//
//        })
//
//
//    }

    //    private fun setAdapter(feed:List<Feed>) {
//
//
//
////        mTagAdapter = Test_tag_Adapter(feed,view!!.context)
////        mRankAdapter = FeedRankAdapter(contexts,childFragmentManager)
////        mFeedAdapter.notifyDataSetChanged()
//
////        viewpager?.apply {
////            adapter = mRankAdapter
////
////            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
////                override fun onPageScrollStateChanged(state: Int) {
////                }
////                override fun onPageScrolled(
////                    position: Int,
////                    positionOffset: Float,
////                    positionOffsetPixels: Int
////                ) {
////                }
////                override fun onPageSelected(position: Int) {
////                   page = position
////                }
////            })
////            indicators?.setViewPager(this)
////
////        }
//
//        mFeedAdapter = FeedAdapter(feed, contexts,childFragmentManager)
//        recyclerView?.apply {
//            var linearLayoutMnager = LinearLayoutManager(this.context)
//            this.layoutManager = linearLayoutMnager
//            adapter = mFeedAdapter
//        }
//
//
//        if (feed.isNotEmpty()) {
//            progressBar?.visibility = View.GONE
//
//        }else {
//            progressBar?.visibility = View.VISIBLE
//        }
//    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){

//            R.id.filter -> {
//
////                feedViewModel.getFeed()
//                val dialog = FilterDialog()
//                dialog.show(childFragmentManager, dialog.tag)
//
//            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        initData()
        mShimmerViewContainer.startShimmerAnimation()
        handler.postDelayed(runnable, delay)

    }

    override fun onPause() {
        super.onPause()
        mShimmerViewContainer.stopShimmerAnimation()
        handler.removeCallbacks(runnable)
    }

    // 태그 자동스크
    private fun autoScroll(tag: List<Tag>, adapter: Test_tag_Adapter) {
        scrollCount = 0;
        var speedScroll: Long = 1200;
        val runnable = object : Runnable {
            override fun run() {
                if (layoutManagers.findFirstVisibleItemPosition() >= tag.size / 2) {
                    adapter.load()
//                    Log.e(TAG, "run: load $scrollCount")
                }
                recyclerViewTag?.smoothScrollToPosition(scrollCount++)
//                Log.e(TAG, "run: $scrollCount")
                handler.postDelayed(this, speedScroll)
            }
        }
        handler.postDelayed(runnable, speedScroll)
    }

    // autoscroll Viewpager
    var runnable: Runnable = object : Runnable {
        override fun run() {
            if (mRankAdapter!!.getCount() === page) {
                page = 0
            } else {
                page++
            }
            viewpager!!.setCurrentItem(page, true)
            handler.postDelayed(this, delay.toLong())
        }
    }

    override fun onRefresh() {
        initData()
        mSwipeRefreshLayout.setRefreshing(false)
    }



}