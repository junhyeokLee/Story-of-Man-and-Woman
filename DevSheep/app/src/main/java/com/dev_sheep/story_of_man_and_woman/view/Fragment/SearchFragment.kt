package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.SearchCardAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Tag_Select_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tag_select.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.progressBar_tag
import kotlinx.android.synthetic.main.fragment_search.recyclerView_tag
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    lateinit var recyclerViewTag : RecyclerView
    lateinit var et_comment : EditText
    private val feedViewModel: FeedViewModel by viewModel()
    private var layoutManager_Tag : GridLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search,null)
        recyclerViewTag = view.findViewById<RecyclerView>(R.id.recyclerView_tag)
        et_comment = view.findViewById<EditText>(R.id.et_comment)
        layoutManager_Tag = GridLayoutManager(context,2 )
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


        feedViewModel.getTagList()
        //라이브데이터
        feedViewModel.listTagOfFeed.observe(this, Observer(function = fun(tagList: MutableList<Tag>?) {
            tagList?.let {
                if (it.isNotEmpty()) {
                    if(context == null){
                    }else {
                        progressBar_tag?.visibility = View.GONE
                        recyclerViewTag?.layoutManager = layoutManager_Tag
                        recyclerViewTag?.adapter =
                            SearchCardAdapter(it, view.context, feedViewModel)
                    }
                }else {
                    progressBar_tag?.visibility = View.VISIBLE
                }
            }
        }))

        return view
    }


}