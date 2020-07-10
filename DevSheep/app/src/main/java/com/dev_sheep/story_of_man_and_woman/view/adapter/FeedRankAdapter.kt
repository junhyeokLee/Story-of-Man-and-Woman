package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.Fragment.HomeRankFragment
import java.util.*


class FeedRankAdapter (
    private val context: Context,
    private var fragmentManager: FragmentManager

) : FragmentPagerAdapter(fragmentManager) {
    lateinit var mcontext: Context
    var mViewPagerState = HashMap<Int, Int>()
    var previousPostition: Int = -1



    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {
                return HomeRankFragment(1)
            }

            1 ->{
                return HomeRankFragment(2)
            }

            2 ->{
                return HomeRankFragment(3)
            }
            3 ->{
                return HomeRankFragment(4)
            }
            4 ->{
                return HomeRankFragment(5)
            }
        }
        return HomeRankFragment(0)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getCount(): Int {
        return 5
    }


}

