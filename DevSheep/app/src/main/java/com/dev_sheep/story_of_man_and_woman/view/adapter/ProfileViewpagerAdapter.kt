package com.dev_sheep.story_of_man_and_woman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileBookMarkFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileFeedFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileScretFragment

class ProfileViewpagerAdapter(fm: FragmentManager,pageCount: Int) : FragmentPagerAdapter(fm) {

    var mPageCount : Int? = null

    init {
        this.mPageCount = pageCount
    }


    override fun getCount(): Int {
        return mPageCount!!
    }

    override fun getItem(position: Int): Fragment {
        val fragment = when(position)
        {
            0 -> ProfileFeedFragment()
            1 -> ProfileBookMarkFragment()
            2 -> ProfileScretFragment()
            else -> null
        }
        return fragment!!
    }

}