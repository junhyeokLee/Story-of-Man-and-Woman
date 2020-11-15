package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_Searchtag_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    lateinit var recyclerViewTag : RecyclerView
    lateinit var et_comment : EditText
    private val feedViewModel: FeedViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search,null)
        recyclerViewTag = view.findViewById<RecyclerView>(R.id.recyclerView_tag)
        et_comment = view.findViewById<EditText>(R.id.et_comment)

        val layoutManager_Tag = GridLayoutManager(view.context,2 )
        recyclerViewTag?.layoutManager = layoutManager_Tag

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


        val single_tag = APIService.FEED_SERVICE.getTagList()
        single_tag.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (it.isNotEmpty()) {
                    progressBar_tag?.visibility = View.GONE
                    recyclerViewTag?.layoutManager = layoutManager_Tag
                    recyclerViewTag?.adapter = Test_Searchtag_Adapter(it,view.context)
                }else {
                    progressBar_tag?.visibility = View.VISIBLE
                }
//                recyclerViewTag?.layoutManager = layoutManager_Tag
//                recyclerViewTag?.adapter = Test_Searchtag_Adapter(it,view.context)
//                mTagAdapter = object : Test_tag_Adapter(it,contexts)

            }
                ,{

                })

        return view
    }


}