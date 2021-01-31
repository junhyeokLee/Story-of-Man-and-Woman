package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.Fragment.*
import com.dev_sheep.story_of_man_and_woman.view.dialog.WriteDialog
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener{
    private val memberViewModel: MemberViewModel by viewModel()

    val homeFragment = HomeFragment()
    val profileFragment = ProfileFragment()
    val userProfileFragment = ProfileUsersFragment()
    val searchFragment = SearchFragment()
    val notificationFragment = NotificationFragment()
    val commentFragment = CommentFragment()
    val reCommentFragment = ReCommentFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 자동로그인 email password 정보 가져와서 m_seq 저장하기
        val auto = getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)
        var loginEmail = auto.getString("inputEmail", null)
        var loginPassword = auto.getString("inputPassword", null)

        if (loginEmail != null && loginPassword != null) {
            memberViewModel.getMemberSeq(loginEmail, loginPassword, this) // viewModel에서 Mseq 정보저장하기
        }


        val bottomNavigation = bottomNavigationView

        // FeedAcitivity or AlarmActivity 에서 유저 클릭시
        if(intent.hasExtra("ProfileUsersFragment")) {

            var checked = intent.getBooleanExtra("ProfileUsersFragment" , false)
            if(checked == true){

                bottomNavigation.selectedItemId = R.id.menuProfile

                if(intent.hasExtra("m_seq")){

                    var m_seq = intent.getStringExtra("m_seq");
                    val arguments = Bundle()
                    arguments.putString("feed_activity_m_seq", m_seq)
                    userProfileFragment.arguments = arguments
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, userProfileFragment)
                        .commit()

                }
            }
        }
        // FeedAcitivity 에서 자신을 클릭시
        else if(intent.hasExtra("ProfileMyFragment")){
            var checked = intent.getBooleanExtra("ProfileMyFragment" , false)

            if(checked == true){
                bottomNavigation.selectedItemId = R.id.menuProfile
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, profileFragment)
                    .commit()
            }
        }else {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, homeFragment).commitAllowingStateLoss();
        }

//        if(intent.hasExtra("CommentFragment")) {
//            var checked = intent.getBooleanExtra("CommentFragment" , false)
//            if(checked == true){
//                if(intent.hasExtra("feed_seq")){
//                    var feed_seq = intent.getStringExtra("feed_seq")
//                    var feed_creater = intent.getStringExtra("feed_creater")
//                    var feed_title = intent.getStringExtra("feed_title")
//                    val arguments = Bundle()
//                    arguments.putString("feed_seq", feed_seq)
//                    arguments.putString("feed_creater",feed_creater)
//                    arguments.putString("feed_title",feed_title)
//                    commentFragment.arguments = arguments
//                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, commentFragment)
//                        .commit()
//                }
//            }
//        }

//        if(intent.hasExtra("ReCommentFragment")) {
//            var checked = intent.getBooleanExtra("ReCommentFragment" , false)
//            if(checked == true){
//                if(intent.hasExtra("comment_seq")){
//                    var comment_seq = intent.getStringExtra("comment_seq")
//                    val arguments = Bundle()
//                    arguments.putString("comment_seq", comment_seq)
//                    reCommentFragment.arguments = arguments
//                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, reCommentFragment)
//                        .commit()
//                }
//            }
//        }



        bottomNavigation.setOnNavigationItemSelectedListener(this)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuHome -> {
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, homeFragment)
                    .commit()
            }
            R.id.menuSearch -> {
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, searchFragment)
                    .commit()
            }
            R.id.menuWrite -> {
                showWrite()
            }
            R.id.menuFavorite -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.frameLayout,
                    notificationFragment
                ).commit()
            }
            R.id.menuProfile -> {
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout, profileFragment)
                    .commit()
            }

        }
        return true
    }



    private fun showWrite() {
        val dialog = WriteDialog()
//        dialog.show(requireFragmentManager(), "")
        dialog.show(supportFragmentManager, dialog.tag)
    }
}
