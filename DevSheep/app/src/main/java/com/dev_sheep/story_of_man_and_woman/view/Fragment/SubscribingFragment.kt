package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribersAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribingAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_subscribing.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SubscribingFragment(val m_seq: String): Fragment() {
    private var rv_subscribers : RecyclerView? = null
    private val memberViewModel: MemberViewModel by viewModel()
    lateinit var BtnBack: ImageButton
    lateinit var mSubscribingAdapter: SubscribingAdapter
    private var limit: Int = 20
    private var offset: Int = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var lastVisibleItemPosition = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var nestedScrollView: NestedScrollView? = null
    private var progressBar : ProgressBar? = null
    private var shimmer_view_container_subscribing: ShimmerFrameLayout? = null
    private var empty : View? = null
    lateinit var tv_empty: TextView
    lateinit var contexts: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subscribing, null)
        rv_subscribers = view.findViewById<View>(R.id.rv_subscribers) as RecyclerView?
        BtnBack = view.findViewById(R.id.im_back) as ImageButton
        contexts = view.context
        empty = view.findViewById(R.id.empty)
        tv_empty = view.findViewById(R.id.emptyText) as TextView
        tv_empty.setText(R.string.empty)
        nestedScrollView = view.findViewById(R.id.nestedScrollView_search)
        progressBar = view.findViewById(R.id.progressBar)
        shimmer_view_container_subscribing = view.findViewById(R.id.shimmer_view_container_subscribing)
//        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
//        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val layoutManager_Tag = GridLayoutManager(view.context, 1)
        rv_subscribers?.layoutManager = layoutManager_Tag

        initData()

        BtnBack.setOnClickListener{
            activity?.onBackPressed()
        }


        return view
    }

    private fun initData(){
        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(contexts)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val single = MEMBER_SERVICE.getSubscribing(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("list member", "" + it)

                if (it.isNotEmpty()) {
                    mSubscribingAdapter = SubscribingAdapter(it, contexts, memberViewModel,m_seq,object :SubscribingAdapter.OnClickProfileListener{
                        override fun OnClickProfile(member: Member, tv: TextView, iv: ImageView) {
                            val trId = ViewCompat.getTransitionName(tv).toString()
                            val trId1 = ViewCompat.getTransitionName(iv).toString()
                            if (member.m_seq == m_seq) {
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
                        shimmer_view_container_subscribing?.stopShimmerAnimation()
                        shimmer_view_container_subscribing?.visibility = View.GONE
                        rv_subscribers?.apply {
                            this.layoutManager = linearLayoutManager
                            this.itemAnimator = DefaultItemAnimator()
                            this.adapter = mSubscribingAdapter
                        }
                    },1000)

                } else {
                    shimmer_view_container_subscribing?.visibility = View.GONE
                    empty!!.visibility = View.VISIBLE
                }
            }, {       shimmer_view_container_subscribing?.visibility = View.GONE
                empty!!.visibility = View.VISIBLE
                Log.d("실패 Get Member", "" + it.message)
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
        linearLayoutManager = LinearLayoutManager(contexts)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        Handler().postDelayed({
            val single = MEMBER_SERVICE.getSubscribing(m_seq,offset,addLimit())
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mSubscribingAdapter = SubscribingAdapter(it, contexts, memberViewModel,m_seq,object :SubscribingAdapter.OnClickProfileListener{
                        override fun OnClickProfile(member: Member, tv: TextView, iv: ImageView) {
                            val trId = ViewCompat.getTransitionName(tv).toString()
                            val trId1 = ViewCompat.getTransitionName(iv).toString()
                            if (member.m_seq == m_seq) {
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
                    rv_subscribers?.apply {
//                            var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutManager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mSubscribingAdapter
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
        shimmer_view_container_subscribing?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        initData()
        shimmer_view_container_subscribing?.startShimmerAnimation()
    }

    private fun addLimit() : Int{
        limit += 20
        return limit
    }

}