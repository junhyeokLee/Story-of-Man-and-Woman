package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFeedFragment: Fragment() {
    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    lateinit var mFeedAdapter: FeedAdapter
    private var limit: Int = 50
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    lateinit var contexts: Context
    private var empty : View? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var shimmer_view_container_profile_feed: ShimmerFrameLayout? = null
    lateinit var tv_empty: TextView
    lateinit var m_seq : String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_feed, container, false)
        contexts = view.context
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        empty = view.findViewById(R.id.empty)
        tv_empty = view.findViewById(R.id.emptyText) as TextView
        tv_empty.setText(R.string.empty)
        shimmer_view_container_profile_feed = view.findViewById(R.id.shimmer_view_container_profile_feed)

        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            context!!.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
             m_seq = preferences.getString("inputMseq", "")!!

        initData()
        return view
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initData() {
        if(feedViewModel == null || memberViewModel == null) return

        val handlerFeed: Handler = Handler(Looper.myLooper()!!)
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        feedViewModel.getListMystory(m_seq,offset,limit)
        //라이브 데이터
        feedViewModel.listOfFeeds.observe(this, Observer(function = fun(feedList: MutableList<Feed>?) {
            feedList?.let {
                if (it.size > 0) {
                    empty!!.visibility = View.GONE
                    mFeedAdapter = FeedAdapter(
                        it,
                        contexts,
                        feedViewModel,
                        object : FeedAdapter.OnClickViewListener {
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
                                lintent.putExtra("feed_title", feed.title)
                                lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                                lintent.putExtra("tag_seq",feed.tag_seq)
                                lintent.putExtra(FeedActivity.EXTRA_POSITION, position)

//                        context.transitionName = position.toString()
                                context!!.startActivity(lintent)
                                (context as Activity).overridePendingTransition(
                                    R.anim.fragment_fade_in,
                                    R.anim.fragment_fade_out
                                )

                            }
                        },
                        object : FeedAdapter.OnClickLikeListener {
                            override fun OnClickFeed(feed: Feed, boolean_value: String) {
                                feedViewModel.increaseLikeCount(feed.feed_seq, boolean_value)
                                if (boolean_value.equals("true")) {
                                    if(!feed.creater_seq.equals(m_seq)) {
                                         memberViewModel.addNotifiaction(m_seq, feed.creater_seq!!, feed.feed_seq, "피드알림", "님이 '\' " + feed.title +" '\' 를 좋아합니다.")
                                        memberViewModel.memberPush(feed.creater_seq!!, m_seq, "feedlike")
                                    }
                                }
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

                        }, object : FeedAdapter.OnClickDeleteFeedListener {
                            override fun OnClickDeleted(feed_seq: Int) {
                                showDeletePopup(feedViewModel, feed_seq)
                            }

                        }, object : FeedAdapter.OnEndlessScrollListener {
                            override fun OnEndless(boolean_value: Boolean) {
                                if (boolean_value == false) {
                                    EndlessScroll(false)
                                } else if (boolean_value == true) {
                                    EndlessScroll(true)
                                }
                            }
                        })
                    handlerFeed.postDelayed({
                        shimmer_view_container_profile_feed?.stopShimmerAnimation()
                        shimmer_view_container_profile_feed?.visibility = View.GONE
                        recyclerView?.apply {
//                            var linearLayoutMnager = LinearLayoutManager(this.context)
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mFeedAdapter
                        }
                    }, 1000)
                }else{
                    shimmer_view_container_profile_feed?.visibility = View.GONE
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
                        val single = FEED_SERVICE.getListMystory(m_seq, offset, addLimit())
                        single.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mFeedAdapter.updateList(it)
                            }, {
                                Log.d("Error MoreData", it.message.toString())
                            })
                    }
                }
            }
        })
    }


    private fun showDeletePopup(feedViewmodel: FeedViewModel,feed_seq: Int) {
        val fragment = ProfileFragment()
        val inflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup, null)
        val textView: TextView = view.findViewById(R.id.textView)

        textView.text = "이 게시물을 삭제 하시겠습니까?"

        val alertDialog = AlertDialog.Builder(context!!)
            .setTitle("삭제")
            .setPositiveButton("네") { dialog, which ->
                feedViewmodel.deleteFeed(feed_seq)
                mFeedAdapter.notifyDataSetChanged()
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.replace(
                        R.id.frameLayout,
                        fragment
                    )
                    ?.commit()
            }

            .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->

            })
            .create()

        alertDialog.setView(view)
        alertDialog.show()

        val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        if(btn_color != null){
            btn_color.setTextColor(resources.getColor(R.color.main_Accent))
        }
        if(btn_color_cancel != null){
            btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent))
        }
    }
    override fun onResume() {
        super.onResume()
        initData()
        shimmer_view_container_profile_feed?.startShimmerAnimation()
    }

    override fun onPause() {
        super.onPause()
        initData()
        shimmer_view_container_profile_feed?.startShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
    }
}