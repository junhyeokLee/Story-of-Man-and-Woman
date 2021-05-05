package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.adapter.SearchViewpagerAdapter
import com.google.android.material.tabs.TabLayout


class SearchDetailFragment: Fragment() {

    lateinit var et_comment : EditText
    var viewPagerAdapter: SearchViewpagerAdapter? = null
    var viewpager : ViewPager? = null
    var tablayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_detail,null)

        et_comment = view.findViewById<EditText>(R.id.et_comment)
        viewpager = view.findViewById(R.id.viewPager) as ViewPager
        tablayout = view.findViewById(R.id.tabLayout) as TabLayout

        var tv_search = arguments?.getString("tv_search")
        et_comment.setText(tv_search)


        et_comment.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event!!.getAction() == MotionEvent.ACTION_DOWN){

                    val searchFragment = SearchTitleFragment()//The fragment that u want to open for example
                    var SearchFragment = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction = SearchFragment.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, searchFragment)
                    fragmentTransaction.commit()
                }

                return false;
            }
        })

        tablayout?.apply {
            addTab(this.newTab().setIcon(R.drawable.icons_user).setText("친구찾기"))
            addTab(this.newTab().setIcon(R.drawable.ic_write_empty).setText("사연찾기"))

        }

        viewPagerAdapter = SearchViewpagerAdapter(childFragmentManager, tablayout!!.tabCount,et_comment.text.toString())
        viewpager?.adapter = viewPagerAdapter
        viewpager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        viewpager?.currentItem = 1

        tablayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager?.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                viewpager?.setCurrentItem(tab!!.position)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0) {
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.icons_user)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_write_empty)
                } else if (tab!!.position == 1) {
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.icons_user)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_write)
                }
                viewpager?.setCurrentItem(tab!!.position)
            }
        })


        return view
    }


}