package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapterTag
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribersAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribingAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_subscribers.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscribersFragment(val m_seq: String) : Fragment() {

    private var rv_subscribers : RecyclerView? = null
    private val memberViewModel: MemberViewModel by viewModel()
    lateinit var BtnBack: ImageButton
    lateinit var mSubscribersAdapter: SubscribersAdapter
    lateinit var my_m_seq:String
    private var limit: Int = 50
    private var offset: Int = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var shimmer_view_container_subscribe: ShimmerFrameLayout? = null

    lateinit var contexts: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subscribers, null)
        rv_subscribers = view.findViewById<View>(R.id.rv_subscribers) as RecyclerView?
        BtnBack = view.findViewById(R.id.im_back) as ImageButton
        contexts = view.context
        shimmer_view_container_subscribe = view.findViewById(R.id.shimmer_view_container_subscribe)

        val layoutManager_Tag = GridLayoutManager(view.context, 1)
        rv_subscribers?.layoutManager = layoutManager_Tag
        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        my_m_seq = preferences.getString("inputMseq", "")


        initData()

        BtnBack.setOnClickListener{
            activity?.onBackPressed()
        }
        return view
    }

    private fun initData(){

        if(memberViewModel == null){
            return
        }

        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(contexts)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        memberViewModel.getSubsribers(m_seq,offset,limit)
        memberViewModel.memberListLivedata.observe(this, Observer {
            if (it.isNotEmpty()) {
                mSubscribersAdapter = SubscribersAdapter(it, contexts, memberViewModel,object :
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

                },object :SubscribersAdapter.OnEndlessScrollListener{
                    override fun OnEndless(boolean_value: Boolean) {
                        if (boolean_value == false) {
                            EndlessScroll(false)
                        } else if (boolean_value == true) {
                            EndlessScroll(true)
                        }
                    }

                })
                handlerFeed.postDelayed({
                    shimmer_view_container_subscribe?.stopShimmerAnimation()
                    shimmer_view_container_subscribe?.visibility = View.GONE
                    rv_subscribers?.apply {
                        this.layoutManager = linearLayoutManager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mSubscribersAdapter
                    }
                },1000)

            } else {
                shimmer_view_container_subscribe?.visibility = View.GONE
            }
        })
    }

    fun EndlessScroll(isLoading: Boolean){
        // 무한스크롤
        rv_subscribers!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        val single_subscribers = MEMBER_SERVICE.getSubsribers(m_seq,offset,addLimit())
                        single_subscribers.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mSubscribersAdapter.updateList(it)
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
        shimmer_view_container_subscribe?.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
    }

}