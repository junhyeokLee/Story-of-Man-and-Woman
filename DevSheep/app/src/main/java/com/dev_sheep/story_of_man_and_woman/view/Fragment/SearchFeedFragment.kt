package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapterTag
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFeedFragment(title:String) : Fragment() {


    private val feedViewModel: FeedViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    lateinit var mFeedAdapterTag: FeedAdapterTag
    private var limit: Int = 20
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var nestedScrollView: NestedScrollView? = null
    private var progressBar : ProgressBar? = null
    private var shimmer_view_container_search_feed: ShimmerFrameLayout? = null
    lateinit var contexts: Context
    private var empty : View? = null
    lateinit var tv_empty: TextView
    var title = title
    lateinit var m_seq : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_feed, container, false)
        contexts = view.context
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        empty = view.findViewById(R.id.empty)
        tv_empty = view.findViewById(R.id.emptyText) as TextView
        tv_empty.setText(R.string.empty)
        nestedScrollView = view.findViewById(R.id.nestedScrollView_search)
        progressBar = view.findViewById(R.id.progressBar)
        shimmer_view_container_search_feed = view.findViewById(R.id.shimmer_view_container_search_feed)

        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            contexts.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
             m_seq = preferences.getString("inputMseq", "")

        initData()

        return view
    }


    private fun initData() {

        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        // 전체보기
        val single = APIService.FEED_SERVICE.getFeedSearch(title,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if(it.size > 0 || it.isNotEmpty()) {
                    empty!!.visibility = View.GONE
                    mFeedAdapterTag = FeedAdapterTag(
                        it,
                        contexts,
                        object : FeedAdapterTag.OnClickViewListener {
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
                                lintent.putExtra("feed_title",feed.title)
                                lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                                lintent.putExtra(FeedActivity.EXTRA_POSITION, position)

//                        context.transitionName = position.toString()
                                context!!.startActivity(lintent)
                                (context as Activity).overridePendingTransition(
                                    R.anim.fragment_fade_in,
                                    R.anim.fragment_fade_out
                                )

                            }
                        },
                        object : FeedAdapterTag.OnClickLikeListener {
                            override fun OnClickFeed(feed_seq: Int, boolean_value: String) {
                                feedViewModel.increaseLikeCount(feed_seq, boolean_value)
                            }

                        }, object : FeedAdapterTag.OnClickBookMarkListener {
                            override fun OnClickBookMark(
                                m_seq: String,
                                feed_seq: Int,
                                boolean_value: String
                            ) {
                                feedViewModel.onClickBookMark(m_seq, feed_seq, boolean_value)
                            }

                        }, object : FeedAdapterTag.OnClickProfileListener {
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
                        shimmer_view_container_search_feed?.stopShimmerAnimation()
                        shimmer_view_container_search_feed?.visibility = View.GONE
                        recyclerView?.apply {
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mFeedAdapterTag
                        }
                    },1000)
                }else{
                    shimmer_view_container_search_feed?.visibility = View.GONE
                    empty!!.visibility = View.VISIBLE
                }

            }, {
                shimmer_view_container_search_feed?.visibility = View.GONE
                empty!!.visibility = View.VISIBLE
                Log.d("feed 보기 실패함", "" + it.message)
            })

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
                    progressBar?.visibility = View.VISIBLE

                    if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight() && scrollY > oldScrollY) {
                        visibleItemCount = linearLayoutManager.getChildCount()
                        totalItemCount = linearLayoutManager.getItemCount()
                        lastVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                        if (visibleItemCount + lastVisibleItemPosition >= totalItemCount) {
//                                Handler().postDelayed({
                            LoadMoreData()
//                                },1000)

                        }else{
                            progressBar?.visibility = View.GONE
                        }
                    }
                }
            }

        })

    }

    private fun LoadMoreData() {
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        Handler().postDelayed({
            val single = APIService.FEED_SERVICE.getFeedSearch(title,offset,addLimit())
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFeedAdapterTag = FeedAdapterTag(
                        it,
                        contexts,
                        object : FeedAdapterTag.OnClickViewListener {
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
                                lintent.putExtra("feed_title",feed.title)
                                lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                                lintent.putExtra(FeedActivity.EXTRA_POSITION, position)

//                        context.transitionName = position.toString()
                                context!!.startActivity(lintent)
                                (context as Activity).overridePendingTransition(
                                    R.anim.fragment_fade_in,
                                    R.anim.fragment_fade_out
                                )

                            }
                        },
                        object : FeedAdapterTag.OnClickLikeListener {
                            override fun OnClickFeed(feed_seq: Int, boolean_value: String) {
                                feedViewModel.increaseLikeCount(feed_seq, boolean_value)
                            }

                        }, object : FeedAdapterTag.OnClickBookMarkListener {
                            override fun OnClickBookMark(
                                m_seq: String,
                                feed_seq: Int,
                                boolean_value: String
                            ) {
                                feedViewModel.onClickBookMark(m_seq, feed_seq, boolean_value)
                            }

                        }, object : FeedAdapterTag.OnClickProfileListener {
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
                    recyclerView?.apply {
//                            var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutManager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mFeedAdapterTag
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
        shimmer_view_container_search_feed?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        initData()
        shimmer_view_container_search_feed?.startShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 20
        return limit
    }
}

