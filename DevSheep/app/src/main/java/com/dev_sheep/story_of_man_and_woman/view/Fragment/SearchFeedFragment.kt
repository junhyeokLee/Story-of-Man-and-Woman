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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Comment
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
    private var limit: Int = 50
    private var offset: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var shimmer_view_container_search_feed: ShimmerFrameLayout? = null
    lateinit var contexts: Context
    private var empty : View? = null
    lateinit var tv_empty: TextView
    var title = title
    lateinit var m_seq : String

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
        shimmer_view_container_search_feed = view.findViewById(R.id.shimmer_view_container_search_feed)

        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            contexts.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
             m_seq = preferences.getString("inputMseq", "")

        initData()

        return view
    }


    private fun initData() {

        if(feedViewModel == null){
            return
        }

        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        // 전체보기
        feedViewModel.getFeedSearch(title,offset,limit)
        //라이브데이터
        feedViewModel.listOfFeeds.observe(this, Observer(function = fun(feedList: MutableList<Feed>?) {
            feedList?.let {
                if (it.isNotEmpty()) {
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
                                lintent.putExtra("tag_seq",feed.tag_seq)
                                lintent.putExtra(FeedActivity.EXTRA_POSITION, position)

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

                        }, object : FeedAdapterTag.OnEndlessScrollListener {
                            override fun OnEndless(boolean_value: Boolean) {
                                if (boolean_value == false) {
                                    EndlessScroll(false)
                                } else if (boolean_value == true) {
                                    EndlessScroll(true)
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
            }
        }))
    }
    fun EndlessScroll(isLoading: Boolean){
        // 무한스크롤
        recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        val single = APIService.FEED_SERVICE.getFeedSearch(title,offset,addLimit())
                        single.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mFeedAdapterTag.updateList(it)
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
        shimmer_view_container_search_feed?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
    }
}

