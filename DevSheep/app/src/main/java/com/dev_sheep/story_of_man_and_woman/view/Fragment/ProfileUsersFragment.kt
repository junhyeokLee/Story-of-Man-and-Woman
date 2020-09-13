package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.adapter.ProfileUserViewpagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.ImageDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileUsersFragment: Fragment(),View.OnClickListener {

    private var menu : Menu? = null
    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()

    var collapsingToolbar : CollapsingToolbarLayout? = null
    var appBarLayout : AppBarLayout? = null
    var toolbar : Toolbar? = null
    var viewPagerAdapter: ProfileUserViewpagerAdapter? = null
    var messageBtn : ImageView? = null
    var recyclerView : RecyclerView? = null
    var progressBar : ProgressBar? = null
    var layoutManager: GridLayoutManager? = null
    var profileImage: CircleImageView? = null
    var viewpager : ViewPager? = null
    var tablayout: TabLayout? = null

    lateinit var nickname: String
    lateinit var m_nick_name : String
    lateinit var m_seq : String
    lateinit var my_m_seq : String
    lateinit var followChecked : CheckBox
    lateinit var followerCount : TextView
    lateinit var followCount : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_users, null)
        toolbar = view.findViewById(R.id.toolbar) as Toolbar
        appBarLayout = view.findViewById<AppBarLayout>(R.id.app_bar)
        collapsingToolbar = view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        messageBtn = view.findViewById<ImageView>(R.id.message_btn)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        layoutManager = GridLayoutManager(view.context, 1)
        profileImage = view.findViewById<CircleImageView>(R.id.id_Profile_Image)
        viewpager = view.findViewById(R.id.viewPager) as ViewPager
        tablayout = view.findViewById(R.id.tabLayout) as TabLayout
        followChecked = view.findViewById(R.id.check_follow) as CheckBox
        followerCount = view.findViewById(R.id.count_follower) as TextView
        followCount = view.findViewById(R.id.count_follow) as TextView
        recyclerView?.layoutManager = layoutManager
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        setHasOptionsMenu(true)

        collapsingToolbarInit()

        // Adapter에서 user페이지로 이동시 seq 전달
        val arguments = arguments
        m_seq = arguments!!.getString("m_seq")

        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        my_m_seq = preferences.getString("inputMseq", "")

        tablayout?.apply {
            addTab(this.newTab().setIcon(R.drawable.ic_write).setText("나의 이야기"))
            addTab(this.newTab().setIcon(R.drawable.ic_heart_empty).setText("구독자 에게"))

        }

        viewPagerAdapter = ProfileUserViewpagerAdapter(childFragmentManager, tablayout!!.tabCount,m_seq)
        viewpager?.adapter = viewPagerAdapter
        viewpager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

        tablayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager?.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                viewpager?.setCurrentItem(tab!!.position)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0) {
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_heart_empty)
                } else if (tab!!.position == 1) {
                    tablayout?.getTabAt(0)?.setIcon(R.drawable.ic_write_empty)
                    tablayout?.getTabAt(1)?.setIcon(R.drawable.ic_heart)
                }
                viewpager?.setCurrentItem(tab!!.position)
            }
        })

        initData()

        profileImage?.setOnClickListener(this)
        followChecked?.setOnClickListener(this)

        followChecked.apply {
            memberViewModel.memberSubscribeChecked(m_seq,my_m_seq,"checked",this,context);

        }
        memberViewModel.memberMySubscribeCount(m_seq,followCount)
        memberViewModel.memberUserSubscribeCount(m_seq,followerCount)

        return view
    }

    private fun initData(){

        val single = APIService.MEMBER_SERVICE.getMember(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                tv_profile_nick.text = it.nick_name
                nickname = it.nick_name.toString()
                m_nick_name = it.nick_name.toString()
//                id_Profile_Image.http://storymaw.com/data/feed/20200814_113842.jpg
                if (it.profile_img == null) {
                    id_Profile_Image.background = resources.getDrawable(R.drawable.ic_user)
                } else {
                    //"http://www.storymaw.com/data/member/"+nickname+"/"+
                    Glide.with(this)
                        .load(it.profile_img)
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(id_Profile_Image)
                }
                if (it.background_img != null) {
                    Glide.with(this)
                        .load(it.background_img)
                        .placeholder(android.R.color.transparent)
                        .into(id_ProfileBackground_Image)
                }


            }, {
                Log.e("실패 Get Member", "" + it.message)
            })




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
                if (scrollRange == -1) {
                    scrollRange = barLayout?.totalScrollRange!!
                }
                showAndHideOption(R.id.action_info, scrollRange + verticalOffset)
            })
        }
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        inflater.inflate(R.menu.menu_scrolling, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = item.itemId

        if(id == R.id.action_info){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showAndHideOption(id: Int, vertical: Int) {
        if(vertical > 150){
            if(menu != null) {
                collapsingToolbar?.title = ""
                val item = menu!!.findItem(id)
                item.isVisible = false
            }
        }else if(vertical < 150) {
            if (menu != null) {
                collapsingToolbar?.title = nickname
                val item = menu!!.findItem(id)
                item.isVisible = true
            }
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.id_Profile_Image -> {
                ImageDialog(context!!, R.mipmap.user).start("dqwq")
            }
            R.id.check_follow -> {

                if (followChecked.isChecked == true) {
                    followChecked.setTextColor(resources.getColor(R.color.white))
                    followChecked.text = "구독취소"
//                    followerCount.setText("1")
                    memberViewModel.memberSubscribe(m_seq,my_m_seq,"true",followerCount)

                } else {
                    followChecked.setTextColor(resources.getColor(R.color.black))
                    followChecked.text = "구독하기"
//                    followerCount.setText("0")
                    memberViewModel.memberSubscribe(m_seq,my_m_seq,"false",followerCount)
                }


            }
        }


    }
}

