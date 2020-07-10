package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedRankAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFeedFragment: Fragment() {
    private val testViewModel: TestViewModel by viewModel()
    private var recyclerViewTag : RecyclerView? = null
    lateinit var mTagAdapter: Test_tag_Adapter
    private var limit: Int = 10
    private var offset: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_feed,container,false)
        recyclerViewTag = view.findViewById<View>(R.id.recyclerView_tag) as RecyclerView?
        initData()
        return view
    }

    private fun initData(){

        // 전체보기
//        testViewModel.getListPokemon().observe(this, Observer { test ->
//            setAdapter(test)
//        })

        // 페이징 처리
//        testViewModel.getListFirst(limit,offset).observe(this, Observer { test ->
//            setAdapter(test)
//        })

    }
//    private fun setAdapter(test:List<Test>) {
//        mTagAdapter = Test_tag_Adapter(test,view!!.context)
//
//
//        recyclerViewTag?.apply {
//            val layoutManager_Tag = LinearLayoutManager(view!!.context )
//            layoutManager = layoutManager_Tag
//            adapter =  mTagAdapter
//        }
//
//    }

}