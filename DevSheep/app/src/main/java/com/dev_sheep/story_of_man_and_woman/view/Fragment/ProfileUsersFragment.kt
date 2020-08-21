package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.adapter.ProfileViewpagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.ImageDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileUsersFragment: Fragment(),View.OnClickListener {

    private var menu : Menu? = null
    private val testViewModel: TestViewModel by viewModel()

    var collapsingToolbar : CollapsingToolbarLayout? = null
    var appBarLayout : AppBarLayout? = null
    var toolbar : Toolbar? = null
    var viewPagerAdapter: ProfileViewpagerAdapter? = null
    var messageBtn : ImageView? = null
    var recyclerView : RecyclerView? = null
    var progressBar : ProgressBar? = null
    var layoutManager: GridLayoutManager? = null
    var profileImage: com.mikhaellopez.circularimageview.CircularImageView? = null
    var viewpager : ViewPager? = null
    var tablayout: TabLayout? = null

    lateinit var followChecked : CheckBox
    lateinit var followerCount : TextView
    lateinit var followCount : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_users,null)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar)
        collapsingToolbar = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        messageBtn = view.findViewById<ImageView>(R.id.message_btn)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        layoutManager = GridLayoutManager(view.context, 1)
        profileImage = view.findViewById<com.mikhaellopez.circularimageview.CircularImageView>(R.id.id_Profile_Image)
        viewpager = view.findViewById(R.id.viewPager) as ViewPager
        tablayout = view.findViewById(R.id.tabLayout) as TabLayout
        followChecked = view.findViewById(R.id.check_follow) as CheckBox
        followerCount = view.findViewById(R.id.count_follower) as TextView
        followCount = view.findViewById(R.id.count_follow) as TextView
        recyclerView?.layoutManager = layoutManager
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        collapsingToolbarInit()

        tablayout?.apply {
            addTab(this.newTab().setIcon(R.drawable.ic_write).setText("나 이야기"))
            addTab(this.newTab().setIcon(R.drawable.ic_heart_empty).setText("구독자 에게"))


        }

        viewPagerAdapter = ProfileViewpagerAdapter(childFragmentManager,tablayout!!.tabCount)
        viewpager?.adapter = viewPagerAdapter
        viewpager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

        tablayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager?.setCurrentItem(tab!!.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                viewpager?.setCurrentItem(tab!!.position)
            }
            override fun onTabSelected(tab: TabLayout.Tab?)
            {
                if(tab!!.position == 0){
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_heart_empty)
                }else if(tab!!.position == 1){
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write_empty)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_heart)
                }
                viewpager?.setCurrentItem(tab!!.position)
            }
        })

        profileImage?.setImageResource(R.mipmap.user)
        profileImage?.setOnClickListener(this)
        followChecked?.setOnClickListener(this)
//        val manager: FragmentManager? = activity?.supportFragmentManager

//        testViewModel.getListPokemon().observe(this, Observer {
//            val pokemons: List<Test> = it
//            recyclerView?.adapter = TestAdapter(pokemons, view.context,childFragmentManager)
//
//            if (pokemons.isNotEmpty()) {
//                progressBar?.visibility = View.GONE
//            }else {
//                progressBar?.visibility = View.VISIBLE
//            }
//
//        })

        return view
    }


    private fun collapsingToolbarInit(){

        collapsingToolbar.apply {
            this?.setExpandedTitleColor((activity as AppCompatActivity).getColor(R.color.color_primary_text))
            this?.setCollapsedTitleTextColor((activity as AppCompatActivity).getColor(R.color.color_primary_text))
            this?.setCollapsedTitleTextAppearance(R.style.CollapsingTitleStyle)
            this?.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
        }

        appBarLayout.apply {
            var isShow = true
            var scrollRange = -1
            this?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
                if (scrollRange == -1){
                    scrollRange = barLayout?.totalScrollRange!!
                }
                showAndHideOption(R.id.action_info,scrollRange + verticalOffset)
            })
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.menu_scrolling,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId

        if(id == R.id.action_info){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showAndHideOption(id: Int,vertical: Int) {
        if(vertical > 150){
            if(menu != null) {
                collapsingToolbar?.title = ""
                val item = menu!!.findItem(id)
                item.isVisible = false
            }
        }else if(vertical < 150) {
            if (menu != null) {
                collapsingToolbar?.title = "junhyeoklee616"
                val item = menu!!.findItem(id)
                item.isVisible = true
            }
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.id_Profile_Image -> {
                ImageDialog(context!!,R.mipmap.user).start("dqwq")
            }
            R.id.check_follow -> {

                if(followChecked.isChecked == true){
                    followChecked.setTextColor(resources.getColor(R.color.white))
                    followChecked.text = "구독취소"
                    followerCount.setText("1")
                }else{
                    followChecked.setTextColor(resources.getColor(R.color.black))
                    followChecked.text = "구독하기"
                    followerCount.setText("0")
                }


            }
        }


    }
}

