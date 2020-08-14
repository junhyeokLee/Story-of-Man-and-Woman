package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.testService
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedRankAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.FilterDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import com.facebook.drawee.gestures.GestureDetector.newInstance
import com.victor.loading.book.BookLoading
import com.victor.loading.newton.NewtonCradleLoading
import com.victor.loading.rotate.RotateLoading
import eu.micer.circlesloadingindicator.CirclesLoadingIndicator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import me.relex.circleindicator.CircleIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Array.newInstance
import kotlin.concurrent.thread


class HomeFragment : Fragment() {


    private val testViewModel: TestViewModel by viewModel()


    private var recyclerView : RecyclerView? = null
    private var recyclerViewTag : RecyclerView? = null
    private var viewpager : ViewPager? = null
    private var indicators: CircleIndicator? = null
    private var tollbar : androidx.appcompat.widget.Toolbar? = null
    private var progressBar : CirclesLoadingIndicator? = null
    lateinit var contexts : Context
    lateinit var mFeedAdapter: FeedAdapter
    lateinit var mTagAdapter: Test_tag_Adapter
    lateinit var mRankAdapter: FeedRankAdapter
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,null)
        contexts = view.context

        setHasOptionsMenu(true);

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        tollbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar?
        progressBar = view.findViewById<View>(R.id.progressBar) as CirclesLoadingIndicator?
        recyclerViewTag = view.findViewById<View>(R.id.recyclerView_tag) as RecyclerView?
        viewpager = view.findViewById<View>(R.id.vp) as ViewPager?
        indicators = view.findViewById<CircleIndicator>(R.id.indicator)
        // 프래그먼트에 toolbar 세팅
        (activity as AppCompatActivity).setSupportActionBar(tollbar)



        initData()

        return view
    }
    private fun initData(){

        setViewPager()
//        setTagAdapter()
        // 전체보기
        val single = testService.getList()
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mFeedAdapter = FeedAdapter(it, contexts,childFragmentManager)
                recyclerView?.apply {
                    var linearLayoutMnager = LinearLayoutManager(this.context)
                    this.layoutManager = linearLayoutMnager
                    adapter = mFeedAdapter
                }
                if (it.isNotEmpty()) {
                    progressBar?.visibility = View.GONE

                }else {
                    progressBar?.visibility = View.VISIBLE
                }
            },{

            })

        val single_tag = testService.getTagList()
        single_tag.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                recyclerViewTag?.apply {
                    var recycler_this = this
                    layoutManagers = object : LinearLayoutManager(context) {
                        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
                            val smoothScroller = object : LinearSmoothScroller(context) {
                                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                                    return 10.0f;
                                }
                            }
                            smoothScroller.targetPosition = position
                            startSmoothScroll(smoothScroller)
                        }
                    }
                    mTagAdapter = object : Test_tag_Adapter(it,contexts) {
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
                ,{

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

//        testViewModel.getListFeed().observe(this, Observer {
//            setAdapter(it)
//        })

        // 페이징 처리
//        testViewModel.getListFirst(limit,offset).observe(this, Observer { test ->
//            setAdapter(test)
//        })

    }

    private fun setViewPager(){
        mRankAdapter = FeedRankAdapter(contexts,childFragmentManager)
        viewpager?.apply {
            adapter = mRankAdapter

            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
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
        inflater.inflate(R.menu.menu_search,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){

            R.id.filter -> {

//                testViewModel.getFeed()
                val dialog = FilterDialog()
                dialog.show(childFragmentManager, dialog.tag)

            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable,delay)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    // 태그 자동스크
    private fun autoScroll(tag : List<Tag>,adapter: Test_tag_Adapter) {
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
            if (mRankAdapter.getCount() === page) {
                page = 0
            } else {
                page++
            }
            viewpager!!.setCurrentItem(page, true)
            handler.postDelayed(this, delay.toLong())
        }
    }
}
