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
import androidx.lifecycle.Observer
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
    private var limit: Int = 50
    private var offset: Int = 0
    lateinit var contexts: Context
    private lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var mShimmerViewContainer: ShimmerFrameLayout

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
        mShimmerViewContainer = view.findViewById<View>(R.id.shimmer_view_container_search) as ShimmerFrameLayout

        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            contexts.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
        my_m_seq = preferences.getString("inputMseq", "")

        initData()

        return view
    }


    private fun initData() {

        if(memberViewModel == null) return

        val handlerFeed: Handler = Handler(Looper.myLooper())
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        memberViewModel.getUserSearch(nick_name,offset,limit)
        // UserSearch LiveData
        // 전체보기
        memberViewModel.memberListLivedata.observe(this, Observer {
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
                mShimmerViewContainer.stopShimmerAnimation()
                mShimmerViewContainer.visibility = View.GONE

                recyclerView?.apply {
                    this.layoutManager = linearLayoutManager
                    this.itemAnimator = DefaultItemAnimator()
                    this.adapter = mUserAdapter
                }
            },1000)
        })
    }

    fun EndlessScroll(isLoading: Boolean){
        // 무한스크롤
        recyclerView!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        val single = APIService.MEMBER_SERVICE.getUserSearch(nick_name,offset,addLimit())
                        single.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mUserAdapter.updateList(it)
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
        mShimmerViewContainer.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
    }

    private fun addLimit() : Int{
        limit += 50
        return limit
    }
}

