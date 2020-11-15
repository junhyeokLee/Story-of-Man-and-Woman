package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribersAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchUserFragment(nick_name:String) : Fragment() {


    private val memberViewModel: MemberViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    lateinit var mUserAdapter: SubscribersAdapter
    private var limit: Int = 10
    private var offset: Int = 0
    lateinit var contexts: Context
    var nick_name = nick_name

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
        initData()

        return view
    }


    private fun initData() {
        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            context!!.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
        val my_m_seq = preferences.getString("inputMseq", "")
        // 전체보기
        val single = APIService.MEMBER_SERVICE.getUserSearch(nick_name)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("list member",""+it.toString())
                if (it.isNotEmpty()) {
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
//                        this?.layoutManager = layoutManager_Tag
                        this.adapter = mUserAdapter
                    }
                } else {

                }

            }
                ,{
                    Log.e("실패 Get Member", "" + it.message)

                })

    }


    override fun onResume() {
        super.onResume()
        initData()
    }
}

