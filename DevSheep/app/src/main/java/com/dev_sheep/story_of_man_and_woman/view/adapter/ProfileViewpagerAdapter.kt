package com.dev_sheep.story_of_man_and_woman.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileBookMarkFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileFeedFragment

class ProfileViewpagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    val PAGE_MAX_CNT = 2

    override fun getCount(): Int {
        return PAGE_MAX_CNT
    }

    override fun getItem(position: Int): Fragment {
        val fragment = when(position)
        {
            0 -> ProfileFeedFragment()
            1 -> ProfileBookMarkFragment()
            else -> ProfileFeedFragment()
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val title = when (position)
        {
            0 -> "Feed"
            1 -> "BookMark"
            else -> "null"
        }
        return title
    }
}