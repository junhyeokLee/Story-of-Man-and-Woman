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
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribersAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchUserFragment(nick_name:String) : Fragment() {


    private val memberViewModel: MemberViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    lateinit var mUserAdapter: SubscribersAdapter
    private var empty : View? = null
    lateinit var tv_empty: TextView
    private var limit: Int = 20
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    lateinit var contexts: Context
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var nestedScrollView: NestedScrollView? = null
    private var progressBar : ProgressBar? = null
    var nick_name = nick_name
    lateinit var my_m_seq : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        contexts = view.context
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        empty = view.findViewById(R.id.empty)
        tv_empty = view.findViewById(R.id.emptyText) as TextView
        tv_empty.setText(R.string.empty)
        nestedScrollView = view.findViewById(R.id.nestedScrollView_search)
        progressBar = view.findViewById(R.id.progressBar)

        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            contexts.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
        my_m_seq = preferences.getString("inputMseq", "")

        initData()

        return view
    }


    private fun initData() {
        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        // 전체보기
        val single = APIService.MEMBER_SERVICE.getUserSearch(nick_name,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    mUserAdapter = SubscribersAdapter(it, contexts, memberViewModel,object :
                        SubscribersAdapter.OnClickProfileListener{
                        override fun OnClickProfile(member: Member, tv: TextView, iv: ImageView) {
                            val trId = ViewCompat.getTransitionName(tv).toString()
                            val trId1 = ViewCompat.getTransitionName(iv).toString()
                            if (member.m_seq == my_m_seq) {
                                activity?.supportFragmentManager
                                    ?.beginTransaction()
                                    ?.addSharedElement(tv, trId)
                                    ?.addSharedElement(iv, trId1)
                                    ?.addToBackStack("ProfileImg")
                                    ?.replace(
                                        R.id.frameLayout,
                                        ProfileFragment.newInstanceMember(member, trId, trId1)
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
                                        ProfileUsersFragment.newInstanceMember(member, trId, trId1)
                                    )
                                    ?.commit()
                            }
                        }

                    })

                    handlerFeed.postDelayed({
                        recyclerView?.apply {
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mUserAdapter
                        }
                    },1000)
               }
                ,{
                    Log.e("실패 Get Member", "" + it.message)

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
            val single = APIService.MEMBER_SERVICE.getUserSearch(nick_name,offset,addLimit())
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mUserAdapter = SubscribersAdapter(it, view!!.context, memberViewModel,object :
                        SubscribersAdapter.OnClickProfileListener{
                        override fun OnClickProfile(member: Member, tv: TextView, iv: ImageView) {
                            val trId = ViewCompat.getTransitionName(tv).toString()
                            val trId1 = ViewCompat.getTransitionName(iv).toString()
                            if (member.m_seq == my_m_seq) {
                                activity?.supportFragmentManager
                                    ?.beginTransaction()
                                    ?.addSharedElement(tv, trId)
                                    ?.addSharedElement(iv, trId1)
                                    ?.addToBackStack("ProfileImg")
                                    ?.replace(
                                        R.id.frameLayout,
                                        ProfileFragment.newInstanceMember(member, trId, trId1)
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
                                        ProfileUsersFragment.newInstanceMember(member, trId, trId1)
                                    )
                                    ?.commit()
                            }
                        }

                    })
                    recyclerView?.apply {
//                            var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutManager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mUserAdapter
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

    }

    override fun onPause() {
        super.onPause()
        initData()
    }

    private fun addLimit() : Int{
        limit += 20
        return limit
    }
}

