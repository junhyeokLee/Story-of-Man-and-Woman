package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.Fragment.SearchTitleFragment.Companion.TAG
import com.dev_sheep.story_of_man_and_woman.view.activity.MessageActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MyMessageActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.ProfileUserViewpagerAdapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.ImageDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.id_ProfileBackground_Image
import kotlinx.android.synthetic.main.fragment_profile_users.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileUsersFragment: Fragment(),View.OnClickListener {

    companion object {

        val USER_ID = "UID"

        fun newInstance(feed: Feed, transitionId : String, transitionId1 : String): ProfileUsersFragment {
            val args = Bundle()
            args.putString("m_seq",feed.creater_seq)
            args.putString("creater_nick", feed.creater)
            args.putString("creater_image", feed.creater_image_url)
            args.putString("trId", transitionId)
            args.putString("trId1", transitionId1)
            val fragment = ProfileUsersFragment()
            fragment.arguments = args
            return fragment
        }
        fun newInstanceMember(member: Member, transitionId : String, transitionId1 : String): ProfileUsersFragment {
            val args = Bundle()
            args.putString("m_seq",member.m_seq)
            args.putString("creater_nick", member.nick_name)
            args.putString("creater_image", member.profile_img)
            args.putString("trId", transitionId)
            args.putString("trId1", transitionId1)
            val fragment = ProfileUsersFragment()
            fragment.arguments = args
            return fragment
        }
    }



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
    var profileImage: ImageView? = null
    var viewpager : ViewPager? = null
    var tablayout: TabLayout? = null
    var profileNickName : TextView? = null

    lateinit var nickname: String
    lateinit var m_nick_name : String
    lateinit var m_seq : String
    lateinit var my_m_seq : String
    lateinit var feed_activity_m_seq: String
    lateinit var followChecked : CheckBox
    lateinit var followerCount : TextView
    lateinit var followCount : TextView
    lateinit var get_creater_nick_name: String
    lateinit var get_creater_img: String
    lateinit var gender : TextView
    lateinit var age : TextView
    lateinit var intro: TextView

    lateinit var profile_img: String
    lateinit var layout_subscriber: LinearLayout
    lateinit var layout_subscribing: LinearLayout
    lateinit var preferecnes_message : ImageView

    val users = FB_User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.activity?.let { ActivityCompat.postponeEnterTransition(it) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = androidx.transition.TransitionInflater.from(context)
                .inflateTransition(android.R.transition.move)
        }
        get_creater_nick_name = arguments?.getString("creater_nick").toString()
        get_creater_img = arguments?.getString("creater_image").toString()
        m_seq = arguments?.getString("m_seq").toString()
        // user FeedActivity 에서 프로필 클릭시 ( MainActivity에서 argument 값 받음 )
        feed_activity_m_seq = arguments?.getString("feed_activity_m_seq").toString()

        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        my_m_seq = preferences.getString("inputMseq", "")


    }

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
        profileImage = view.findViewById<ImageView>(R.id.id_Profile_Image_user)
        profileNickName = view.findViewById<TextView>(R.id.tv_profile_nick_user)
        viewpager = view.findViewById(R.id.viewPager) as ViewPager
        tablayout = view.findViewById(R.id.tabLayout) as TabLayout
        followChecked = view.findViewById(R.id.check_follow) as CheckBox
        followerCount = view.findViewById(R.id.count_follower) as TextView
        followCount = view.findViewById(R.id.count_follow) as TextView
        layout_subscriber = view.findViewById(R.id.layout_subscriber) as LinearLayout
        layout_subscribing = view.findViewById(R.id.layout_subscribing) as LinearLayout
        preferecnes_message = view.findViewById(R.id.preferecnes_message) as ImageView
        gender = view.findViewById(R.id.tv_gender) as TextView
        age = view.findViewById(R.id.tv_age) as TextView
        intro = view.findViewById(R.id.tv_intro) as TextView

        recyclerView?.layoutManager = layoutManager
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        setHasOptionsMenu(true)

        collapsingToolbarInit()


        tablayout?.apply {
            addTab(this.newTab().setIcon(R.drawable.ic_write).setText("나의 이야기"))
            addTab(this.newTab().setIcon(R.drawable.ic_heart_empty).setText("구독자 에게"))

        }
        Log.e("m_seq userfragment",""+m_seq)
        Log.e("feed_activity_m_seq userfragment",""+feed_activity_m_seq)


        if(m_seq == "null") {
            viewPagerAdapter =
                ProfileUserViewpagerAdapter(childFragmentManager, tablayout!!.tabCount, feed_activity_m_seq)
            followChecked.apply {
                memberViewModel.memberSubscribeChecked(feed_activity_m_seq,my_m_seq,"checked",this,context);
                memberViewModel.memberMySubscribeCount(feed_activity_m_seq,followCount)
                memberViewModel.memberUserSubscribeCount(feed_activity_m_seq,followerCount)
            }
        }else{
            viewPagerAdapter =
                ProfileUserViewpagerAdapter(childFragmentManager, tablayout!!.tabCount, m_seq)
            followChecked.apply {
                memberViewModel.memberSubscribeChecked(m_seq,my_m_seq,"checked",this,context);
                memberViewModel.memberMySubscribeCount(m_seq,followCount)
                memberViewModel.memberUserSubscribeCount(m_seq,followerCount)

            }
        }
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
        layout_subscribing?.setOnClickListener(this)
        layout_subscriber?.setOnClickListener(this)
        preferecnes_message?.setOnClickListener(this)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tv_profile_nick_user.transitionName = arguments?.getString("trId")
            id_Profile_Image_user.transitionName = arguments?.getString("trId1")
        }

    }

    private fun initData(){

        Log.e("m_seq",m_seq)
        Log.e("activity_m_seq",feed_activity_m_seq)
        Log.e("creater Image",get_creater_img)

        if(m_seq == "null") {

            val single = APIService.MEMBER_SERVICE.getMember(feed_activity_m_seq)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if(it.profile_img == null){

                        profile_img = "http://storymaw.com/data/member/user.png"

                        Glide.with(this)
                            .load(profile_img)
                            .apply(RequestOptions().circleCrop())
                            .placeholder(android.R.color.transparent)
                            .into(profileImage!!)
                    }else {
                        profile_img = it.profile_img!!
                        Glide.with(this)
                            .load(profile_img)
                            .apply(RequestOptions().circleCrop())
                            .placeholder(android.R.color.transparent)
                            .into(profileImage!!)
                    }
                    profileNickName!!.text = it.nick_name.toString()
                    nickname = it.nick_name.toString()
                    m_nick_name = it.nick_name.toString()
                    gender.text = it.gender.toString()
                    age.text = it.age.toString()
                     if(it.memo.toString().equals("null")){
                                intro.text = ""
                            }else{
                                intro.text = it.memo.toString()
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
        }else {

            profile_img = get_creater_img
            if(profile_img == "null"){
                profile_img = "http://storymaw.com/data/member/user.png"
                Glide.with(this)
                    .load(profile_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .into(profileImage!!)
            }else {
                Glide.with(this)
                    .load(profile_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .into(profileImage!!)
            }
            profileNickName!!.text = get_creater_nick_name
            nickname = get_creater_nick_name
            m_nick_name = get_creater_nick_name

            val single = APIService.MEMBER_SERVICE.getMember(m_seq)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    gender.text = it.gender.toString()
                    age.text = it.age.toString()
                     if(it.memo.toString().equals("null")){
                                intro.text = ""
                            }else{
                                intro.text = it.memo.toString()
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
        when(item.itemId){
            R.id.action_info ->{
                val fromId = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

                val database: FirebaseDatabase = FirebaseDatabase.getInstance();
                val myRef: DatabaseReference = database.getReference("users");

                myRef.orderByChild("username").equalTo(nickname).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("메세지창 에러",error.message.toString())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childDataSnapshot in snapshot.getChildren()) {
                            Log.e(SearchTitleFragment.TAG, "PARENT: " + childDataSnapshot.key)
                            Log.e(SearchTitleFragment.TAG, "유저네임" + childDataSnapshot.child("username").value)
                            val user: FB_User? = childDataSnapshot.getValue(FB_User::class.java)

                            // 파이어베이스 username과 유저프로필의 닉네임이 같으면 메세지보내기로 이동( 파이어베이스 유저아이디값 제공 )
                            if(childDataSnapshot.child("username").value == nickname) {
                                val lintent = Intent(context, MessageActivity::class.java)
                                lintent.putExtra(ProfileUsersFragment.USER_ID, user)
                                context!!.startActivity(lintent)
                                (context as Activity).overridePendingTransition(
                                    R.anim.fragment_fade_in,
                                    R.anim.fragment_fade_out
                                )
                            }

                        }
                    }

                })
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showAndHideOption(id: Int, vertical: Int) {
        if(vertical > 240){
            if(menu != null) {
                collapsingToolbar?.title = ""
                val item = menu!!.findItem(id)
                item.isVisible = false
            }
        }else if(vertical < 240) {
            if (menu != null) {
                collapsingToolbar?.title = nickname
                val item = menu!!.findItem(id)
                item.isVisible = true
            }
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.id_Profile_Image_user -> {
                ImageDialog(context!!,profile_img).start("")
            }
            R.id.check_follow -> {

                if(m_seq == "null") {
                    if (followChecked.isChecked == true) {
                        followChecked.setTextColor(resources.getColor(R.color.white))
                        followChecked.text = "구독취소"
//                    followerCount.setText("1")
                        memberViewModel.memberSubscribe(feed_activity_m_seq, my_m_seq, "true", followerCount)

                    } else {
                        followChecked.setTextColor(resources.getColor(R.color.black))
                        followChecked.text = "구독하기"
//                    followerCount.setText("0")
                        memberViewModel.memberSubscribe(feed_activity_m_seq, my_m_seq, "false", followerCount)
                    }
                }else{
                    if (followChecked.isChecked == true) {
                        followChecked.setTextColor(resources.getColor(R.color.white))
                        followChecked.text = "구독취소"
//                    followerCount.setText("1")
                        memberViewModel.memberSubscribe(m_seq, my_m_seq, "true", followerCount)

                    } else {
                        followChecked.setTextColor(resources.getColor(R.color.black))
                        followChecked.text = "구독하기"
//                    followerCount.setText("0")
                        memberViewModel.memberSubscribe(m_seq, my_m_seq, "false", followerCount)
                    }
                }
            }

            R.id.layout_subscribing ->{
                if(m_seq == "null") {
                    val subscribingFragment =
                        SubscribingFragment(feed_activity_m_seq)//The fragment that u want to open for example
                    var SubscribeFragmnet = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction =
                        SubscribeFragmnet.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, subscribingFragment);
                    fragmentTransaction.commit()
                } else{
                    val subscribingFragment =
                        SubscribingFragment(m_seq)//The fragment that u want to open for example
                    var SubscribeFragmnet = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction =
                        SubscribeFragmnet.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, subscribingFragment);
                    fragmentTransaction.commit()
                }
            }
            R.id.layout_subscriber ->{
                if(m_seq == "null") {
                    val subscribersFragment =
                        SubscribersFragment(feed_activity_m_seq)//The fragment that u want to open for example
                    var SubscribeFragmnet = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction =
                        SubscribeFragmnet.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, subscribersFragment);
                    fragmentTransaction.commit()

                }else{
                    val subscribersFragment =
                        SubscribersFragment(m_seq)//The fragment that u want to open for example
                    var SubscribeFragmnet = (context as AppCompatActivity).supportFragmentManager
                    var fragmentTransaction: FragmentTransaction =
                        SubscribeFragmnet.beginTransaction()
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, subscribersFragment);
                    fragmentTransaction.commit()
                }
            }

            R.id.preferecnes_message ->{
                val fromId = FirebaseAuth.getInstance().uid
                val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

            val database:FirebaseDatabase = FirebaseDatabase.getInstance();
             val myRef: DatabaseReference = database.getReference("users");

                myRef.orderByChild("username").equalTo(nickname).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("메세지창 에러",error.message.toString())
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childDataSnapshot in snapshot.getChildren()) {
                            Log.e(TAG, "PARENT: " + childDataSnapshot.key)
                            Log.e(TAG, "유저네임" + childDataSnapshot.child("username").value)
                            val user: FB_User? = childDataSnapshot.getValue(FB_User::class.java)

                 // 파이어베이스 username과 유저프로필의 닉네임이 같으면 메세지보내기로 이동( 파이어베이스 유저아이디값 제공 )
                if(childDataSnapshot.child("username").value == nickname) {
                    val lintent = Intent(context, MessageActivity::class.java)
                    lintent.putExtra(USER_ID, user)
                    context!!.startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                }

                        }
                    }

                })





            }
        }


    }
}

