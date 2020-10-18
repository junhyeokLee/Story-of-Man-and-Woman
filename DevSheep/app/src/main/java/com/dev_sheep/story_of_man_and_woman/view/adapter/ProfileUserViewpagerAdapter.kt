package com.dev_sheep.story_of_man_and_woman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.Fragment.*

class ProfileUserViewpagerAdapter(fm: FragmentManager, pageCount: Int,m_seq: String) : FragmentPagerAdapter(fm) {

    var mPageCount : Int? = null
    var m_seq : String ?= null

    init {
        this.mPageCount = pageCount
        this.m_seq = m_seq
    }


    override fun getCount(): Int {
        return mPageCount!!
    }

    override fun getItem(position: Int): Fragment {
        val fragment = when(position)
        {
            0 -> ProfileUserFeedFragment(m_seq!!)
            1 -> ProfileUserSubscriberFragment(m_seq!!)

            else -> null
        }
        return fragment!!
    }

}