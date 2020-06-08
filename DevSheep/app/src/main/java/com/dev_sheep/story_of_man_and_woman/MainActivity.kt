package com.dev_sheep.story_of_man_and_woman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.dev_sheep.story_of_man_and_woman.Fragment.HomeFragment
import com.dev_sheep.story_of_man_and_woman.adapter.TestAdapter
import com.dev_sheep.story_of_man_and_woman.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.data.Test
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.progressBar
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    val homeFragment = HomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = bottomNavigationView
        val floatButton = floatingActionButton

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

        }
        return true
    }
}
