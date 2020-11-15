package com.dev_sheep.story_of_man_and_woman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.Fragment.*

class SearchViewpagerAdapter(fm: FragmentManager, pageCount: Int,search:String) : FragmentPagerAdapter(fm) {

    var mPageCount : Int? = null
    var search : String ?= null


    init {
        this.mPageCount = pageCount
        this.search = search

    }


    override fun getCount(): Int {
        return mPageCount!!
    }

    override fun getItem(position: Int): Fragment {
        val fragment = when(position)
        {
            0 -> SearchUserFragment(search!!)
            1 -> SearchFeedFragment(search!!)

            else -> null
        }
        return fragment!!
    }

}