package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedRankActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.TodayMainAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.iv_alarm
import kotlinx.android.synthetic.main.fragment_home.iv_alarm_dot
import kotlinx.android.synthetic.main.fragment_home3.*
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
    private val memberViewModel: MemberViewModel by viewModel()

    private lateinit var m_seq : String
    private var recyclerView : RecyclerView? = null
    private var rv_today : RecyclerView? = null
    private var tollbar : androidx.appcompat.widget.Toolbar? = null
    lateinit var contexts : Context
    lateinit var mFeedAdapter: FeedAdapter
    lateinit var mFeedTodayAdapter: TodayMainAdapter
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mShimmerViewContainer: ShimmerFrameLayout
    lateinit var mShimmerViewContainer_Today : ShimmerFrameLayout

    private var limit: Int = 50
    private var offset: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager // 태그 자동스크롤 위한 초기화 제한
    private lateinit var linearLayoutManagerToday: LinearLayoutManager // 태그 자동스크롤 위한 초기화 제한
    val firebaseMessaging = FirebaseMessaging.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home3, null)
        contexts = view.context


        setHasOptionsMenu(true);

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        rv_today = view.findViewById(R.id.rv_today) as RecyclerView?
        tollbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar?
        mSwipeRefreshLayout = view.findViewById<View>(R.id.sr_refresh) as SwipeRefreshLayout
        mShimmerViewContainer = view.findViewById<View>(R.id.shimmer_view_container) as ShimmerFrameLayout
        mShimmerViewContainer_Today = view.findViewById(R.id.shimmer_view_container_today) as ShimmerFrameLayout
        // 프래그먼트에 toolbar 세팅
        (activity as AppCompatActivity).setSupportActionBar(tollbar)
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_Accent)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
        m_seq = preferences.getString("inputMseq", "")!!
        memberViewModel.getNotificationCount(m_seq, iv_alarm, iv_alarm_dot, contexts)
        if(contexts != null) {
            initData()
            getPreference()
        }
    }

    // 첫 실행 화면
    private fun initData(){
        if(feedViewModel == null || memberViewModel == null) return

        val handlerFeed = Handler(Looper.myLooper()!!)
        linearLayoutManager = LinearLayoutManager(contexts)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManagerToday = LinearLayoutManager(context)
        linearLayoutManagerToday.orientation = LinearLayoutManager.VERTICAL


            feedViewModel.getTodayFeedList()
            //라이브데이터
            feedViewModel.listOfTodayFeeds.observe(this, Observer {

            if(it.size > 0) {
                mFeedTodayAdapter = TodayMainAdapter(it)


                rv_today?.apply {
                    this.layoutManager = linearLayoutManagerToday
                    this.itemAnimator = DefaultItemAnimator()
                    this.adapter = mFeedTodayAdapter
                }
                tv_totalList.setOnClickListener {
                        val lintent = Intent(context, FeedRankActivity::class.java)
                        lintent.putExtra("now", "지금,")
                        (context as Activity).startActivity(lintent)

                }

            }

            })

        feedViewModel.getInitFeedList(offset,limit)
        //라이브 데이터
        feedViewModel.listOfFeeds.observe(this!!, Observer(function = fun(feedList: MutableList<Feed>?) {
            feedList?.let {
                if(it.size > 0) {
                    mFeedAdapter = FeedAdapter(feedList, contexts,
                        feedViewModel,
                        object : FeedAdapter.OnClickViewListener {
                            override fun OnClickFeed(feed: Feed, tv: TextView, iv: ImageView, cb: CheckBox, cb2: CheckBox, position: Int) {
                                feedViewModel.increaseViewCount(feed.feed_seq)

                                val lintent = Intent(context, FeedActivity::class.java)
                                lintent.putExtra("feed_seq", feed.feed_seq)
                                lintent.putExtra("checked" + feed.feed_seq, cb.isChecked)
                                lintent.putExtra("creater_seq", feed.creater_seq)
                                lintent.putExtra("feed_title", feed.title)
                                lintent.putExtra("tag_seq",feed.tag_seq)
                                lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                                lintent.putExtra(FeedActivity.EXTRA_POSITION, position)
//                        context.transitionName = position.toString()
                                (context as Activity).startActivity(lintent)
                                (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)

                            }
                        },
                        object : FeedAdapter.OnClickLikeListener {
                            override fun OnClickFeed(feed: Feed, boolean_value: String) {
                                feedViewModel.increaseLikeCount(feed.feed_seq, boolean_value)
                                if (boolean_value.equals("true")) {
                                    if(!feed.creater_seq.equals(m_seq)) {
                                        memberViewModel.addNotifiaction(m_seq, feed.creater_seq!!, feed.feed_seq, "피드알림", "님이 '\' " + feed.title + " '\' 를 좋아합니다.")
                                        memberViewModel.memberPush(feed.creater_seq!!, m_seq, "feedlike")
                                    }
                                }
                            }

                        },
                        object : FeedAdapter.OnClickBookMarkListener {
                            override fun OnClickBookMark(m_seq: String, feed_seq: Int, boolean_value: String) {
                                feedViewModel.onClickBookMark(m_seq, feed_seq, boolean_value)
                            }
                        },
                        object : FeedAdapter.OnClickProfileListener {
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
                        },
                        object : FeedAdapter.OnClickDeleteFeedListener {
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
                    mFeedAdapter.setHasStableIds(true)
                    handlerFeed.postDelayed({
                        // stop animating Shimmer and hide the layout
                        mShimmerViewContainer.stopShimmerAnimation()
                        mShimmerViewContainer.visibility = View.GONE
                        mShimmerViewContainer_Today.stopShimmerAnimation()
                        mShimmerViewContainer_Today.visibility = View.GONE
                        recyclerView?.apply {
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mFeedAdapter
                        }
                    }, 1000)

                }else {
                    mShimmerViewContainer?.visibility = View.VISIBLE
                    mShimmerViewContainer_Today.visibility = View.VISIBLE
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
                        val single = FEED_SERVICE.getListScroll(offset, addLimit())
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

    private fun getPreference(){
        val sharedPref =
            PreferenceManager.getDefaultSharedPreferences(context)
        //설정 불러오기

        if (sharedPref.getBoolean("boolean2", true) == true) {
            firebaseMessaging.subscribeToTopic("subscriber")
        } else {
            firebaseMessaging.unsubscribeFromTopic("subscriber")
        }
        if (sharedPref.getBoolean("boolean3", true) == true) {
            firebaseMessaging.subscribeToTopic("feedlike")
        } else {
            firebaseMessaging.unsubscribeFromTopic("feedlike")
        }
        if (sharedPref.getBoolean("boolean3", true) == true) {
            firebaseMessaging.subscribeToTopic("feedcomment")
        } else {
            firebaseMessaging.unsubscribeFromTopic("feedcomment")
        }
        if (sharedPref.getBoolean("boolean3", true) == true) {
            firebaseMessaging.subscribeToTopic("feedRecomment")
        } else {
            firebaseMessaging.unsubscribeFromTopic("feedRecomment")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_search, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){

        }
        return true
    }

    override fun onResume() {
        super.onResume()
        mShimmerViewContainer.startShimmerAnimation()
        mShimmerViewContainer_Today.startShimmerAnimation()
//        initData()

    }

    override fun onPause() {
        super.onPause()
        mShimmerViewContainer.stopShimmerAnimation()
        mShimmerViewContainer_Today.stopShimmerAnimation()

        //        initData()
    }



    private fun showDeletePopup(feedViewmodel: FeedViewModel, feed_seq: Int) {
        val fragment = HomeFragment()

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
    override fun onRefresh() {
        initData()
        mSwipeRefreshLayout.setRefreshing(false)
    }


    private fun addLimit() : Int{
        limit += 50
        return limit
    }

    private fun addOffset(): Int{
        offset += 10
        return offset
    }


}