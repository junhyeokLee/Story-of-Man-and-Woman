package com.dev_sheep.story_of_man_and_woman.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.view.Fragment.HomeFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.SearchFragment
import com.dev_sheep.story_of_man_and_woman.view.dialog.WriteDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener  {

    val homeFragment = HomeFragment()
    val profileFragment = ProfileFragment()
    val searchFragment = SearchFragment()

    var fab_open: Animation? = null
    var fab_close:Animation? = null
    var isFabOpen : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val bottomNavigation = bottomNavigationView
        val floatButton = floatingActionButton

        fab_open = AnimationUtils.loadAnimation(this.applicationContext,R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(this.applicationContext,R.anim.fab_close)

        floatingActionButton.setOnClickListener(this);


        val fragmentManager : FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction


        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, homeFragment).commitAllowingStateLoss();

        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuHome ->{
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout,homeFragment).commit()
            }
            R.id.menuSearch ->{
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout,searchFragment).commit()
            }
            R.id.menuProfile ->{
                supportFragmentManager.beginTransaction().replace(R.id.frameLayout,profileFragment).commit()
            }

        }
        return true
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.floatingActionButton ->{
//                toggleFab()
                showWrite()
            }
        }

    }

    private fun showWrite() {
        val dialog = WriteDialog()
//        dialog.show(requireFragmentManager(), "")
        dialog.show(supportFragmentManager, dialog.tag)
    }
}
