package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_profile_subscribe.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment :  Fragment()  {

    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()

    private var recyclerView: RecyclerView? = null
    private var empty : LinearLayout? = null
    lateinit var mFeedAdapter: FeedAdapter
    lateinit var contexts: Context
    lateinit var m_seq : String
    lateinit var mProgressBar: ProgressBar
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    private var limit: Int = 10
    private var offset: Int = 0
    private var mNestedScrollView : NestedScrollView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager // 태그 자동스크롤 위한 초기화 제한

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification,null)
        // ToolBar를 ActionBar로 설정해줘야 합니다.
//        (activity as AppCompatActivity).setSupportActionBar(app_toolbar)
        contexts = view.context
        mProgressBar = view.findViewById(R.id.progressBar_tag) as ProgressBar
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        mNestedScrollView = view.findViewById(R.id.nestedScrollView) as NestedScrollView

        empty = view.findViewById(R.id.empty) as LinearLayout
            initData()

        return view
    }


    private fun initData(){


        if(feedViewModel == null || memberViewModel == null){
            return
        }

        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")


        val single = FEED_SERVICE.getListNotificationSubscribe(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.isEmpty()) {
                    empty?.visibility = View.VISIBLE
                }
                if (context != null) {

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
                        object : FeedAdapter.OnClickLikeListener {
                                               override fun OnClickFeed(feed: Feed, boolean_value: String) {
                                feedViewModel.increaseLikeCount(feed.feed_seq, boolean_value)
                                if(boolean_value.equals("true")) {
                                    memberViewModel.addNotifiaction(
                                        m_seq,
                                        feed.creater_seq!!,
                                        feed.feed_seq,
                                        "피드알림",
                                        "님이 '\' "
                                                + feed.title +
                                                " '\' 를 좋아합니다."
                                    )
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

                        }, object : FeedAdapter.OnClickDeleteFeedListener {
                            override fun OnClickDeleted(feed_seq: Int) {
                                showDeletePopup(feedViewModel, feed_seq)
                                onResume()

                            }

                        },object :FeedAdapter.OnEndlessScrollListener{
                            override fun OnEndless(boolean_value: Boolean) {
                                if(boolean_value == false){
                                    mProgressBar.visibility = View.VISIBLE
                                    EndlessScroll(false)
                                }else if(boolean_value == true){
                                    mProgressBar.visibility = View.GONE
                                    EndlessScroll(true)
                                }
                            }

                        })

                recyclerView?.apply {
                    var linearLayoutMnager = LinearLayoutManager(this.context)
                    this.layoutManager = linearLayoutMnager
                    this.itemAnimator = DefaultItemAnimator()
                    this.adapter = mFeedAdapter
                }
            }
//                if (it.isNotEmpty()) {
//                    progressBar?.visibility = View.GONE
//                } else {
//                    progressBar?.visibility = View.VISIBLE
//                }

            }, {
                Log.e("feed 보기 실패함", "" + it.message)
            })
    }

    fun EndlessScroll(isLoading : Boolean){
        // 무한스크롤
        mNestedScrollView!!.setOnScrollChangeListener(object :
            NestedScrollView.OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView?,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {

                if (v?.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY >= v.getChildAt(v.getChildCount() - 1)
                            .getMeasuredHeight() - v.getMeasuredHeight() &&
                        scrollY > oldScrollY
                    ) {
                        visibleItemCount = linearLayoutManager.getChildCount()
                        totalItemCount = linearLayoutManager.getItemCount()
                        lastVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
//                        if (isLoadData()) {
                        if (visibleItemCount + lastVisibleItemPosition >= totalItemCount) {

                            if (isLoading == false) {

                                mProgressBar.visibility = View.VISIBLE
                                // 마지막 스크롤에 프로그래스바 보여주기 및 무한스크롤
//                            LoadMoreData()
                                val single = FEED_SERVICE.getListNotificationSubscribe(m_seq,offset, addLimit())
                                single.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        mFeedAdapter.updateList(it)

                                    }, {
                                        Log.d("Error MoreData", it.message.toString())
                                    })
                            } else if(isLoading == true){
                                mProgressBar.visibility = View.GONE

                            }
                        }
                    }
                }
            }
        })
    }
    private fun showDeletePopup(feedViewmodel: FeedViewModel,feed_seq: Int) {
        val inflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup, null)
        val textView: TextView = view.findViewById(R.id.textView)

        textView.text = "이 게시물을 삭제 하시겠습니까?"

        val alertDialog = AlertDialog.Builder(context!!)
            .setTitle("삭제")
            .setPositiveButton("네") { dialog, which ->
                feedViewmodel.deleteFeed(feed_seq)
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

    }

    private fun addLimit() : Int{
        limit += 10
        return limit
    }

}