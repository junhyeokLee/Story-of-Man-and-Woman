package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedRankAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFeedFragment: Fragment() {
    private val feedViewModel: FeedViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    lateinit var mFeedAdapter: FeedAdapter
    private var limit: Int = 10
    private var offset: Int = 0
    lateinit var contexts: Context
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_feed, container, false)
        contexts = view.context
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        initData()
        return view
    }

    private fun initData() {
        // 저장된 m_seq 가져오기
        val preferences: SharedPreferences =
            context!!.getSharedPreferences("m_seq", Context.MODE_PRIVATE)
        val m_seq = preferences.getString("inputMseq", "")

        // 전체보기
        val single = FEED_SERVICE.getListMystory(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mFeedAdapter = FeedAdapter(
                    it,
                    contexts,
                    object : FeedAdapter.OnClickViewListener {
                        override fun OnClickFeed(feed_seq: Int) {
                            feedViewModel.increaseViewCount(feed_seq)
                        }
                    },
                    object : FeedAdapter.OnClickLikeListener {
                        override fun OnClickFeed(feed_seq: Int, boolean_value: String) {
                            feedViewModel.increaseLikeCount(feed_seq, boolean_value)
                        }

                    })
                recyclerView?.apply {
                    var linearLayoutMnager = LinearLayoutManager(this.context)
                    this.layoutManager = linearLayoutMnager
                    adapter = mFeedAdapter
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

    override fun onResume() {
        super.onResume()
        initData()
    }
}