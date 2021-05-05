package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.AppExecutors
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Search
import com.dev_sheep.story_of_man_and_woman.view.adapter.SearchRecentAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.fragment_search_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchTitleFragment: Fragment() , View.OnClickListener {

    lateinit var recyclerViewTag : RecyclerView
    lateinit var mSearchRecentAdapter: SearchRecentAdapter
    lateinit var et_comment : EditText
    lateinit var iv_search : ImageView
    lateinit var tv_search_result : TextView
    lateinit var tv_search : TextView
    lateinit var layout_recyclerview : NestedScrollView
    lateinit var layout_search : LinearLayout
    lateinit var layout_delete: LinearLayout

    companion object{
        var TAG = "SearchTitleFragment"
    }
    private val feedViewModel: FeedViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_title,null)
        recyclerViewTag = view.findViewById<RecyclerView>(R.id.recyclerView_tag)
        et_comment = view.findViewById<EditText>(R.id.et_comment)
        iv_search = view.findViewById<ImageView>(R.id.iv_search)
        tv_search = view.findViewById<TextView>(R.id.tv_search)
        layout_recyclerview = view.findViewById<NestedScrollView>(R.id.layout_recyclerview)
        layout_search = view.findViewById(R.id.layout_search)
        layout_delete = view.findViewById(R.id.layout_delete)
        val layoutManager_Tag = GridLayoutManager(view.context,2 )
        recyclerViewTag?.layoutManager = layoutManager_Tag
        tv_search_result = view.findViewById<TextView>(R.id.tv_search_result)

        // Local DB 최근검색어 불러오기
        feedViewModel.getSearchList()
            .observe(this, object : Observer<List<Search>> {
                override fun onChanged(t: List<Search>) {
                    mSearchRecentAdapter = SearchRecentAdapter(view.context,t,feedViewModel,activity!!)
                    recyclerView_tag.apply {
                        recyclerView_tag?.adapter = mSearchRecentAdapter
                    }
                }
            })

        layout_search.setOnClickListener(this)
        iv_search.setOnClickListener(this)
        layout_delete.setOnClickListener(this)

        et_comment.requestFocus();
        et_comment.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if( s!!.length >= 1){
                    layout_search.visibility = View.VISIBLE
                    tv_search.text =  "'"+et_comment.text+"'"
                    layout_recyclerview.visibility = View.GONE
                }else if(s!!.length <= 0){
                    layout_search.visibility = View.GONE
                    layout_recyclerview.visibility = View.VISIBLE
                }
            }

        })



        return view
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.layout_search -> {
                val task = Search(et_comment.text.toString())
                // RoomDataBase MainThread 클래스 적용해서 서치목록 등록
                AppExecutors.getInstance().diskIO().execute(Runnable {
                    feedViewModel.addSearch(task)
                })
                val searchDetailFragment = SearchDetailFragment()//The fragment that u want to open for example
                var SearchFragment = activity?.supportFragmentManager
                var fragmentTransaction: FragmentTransaction = SearchFragment!!.beginTransaction()
                var bundle = Bundle()
                bundle.putString("tv_search",et_comment.text.toString())
                searchDetailFragment.arguments = bundle
                fragmentTransaction.setReorderingAllowed(true)
                fragmentTransaction.setCustomAnimations(
                    R.anim.fragment_fade_in,
                    R.anim.fragment_fade_out
                )
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frameLayout, searchDetailFragment,TAG);
                fragmentTransaction.commit()
                
            }

            R.id.iv_search -> {
                val searchDetailFragment = SearchDetailFragment()//The fragment that u want to open for example
                var SearchFragment = activity?.supportFragmentManager
                var fragmentTransaction: FragmentTransaction = SearchFragment!!.beginTransaction()
                var bundle = Bundle()
                bundle.putString("tv_search",et_comment.text.toString())
                searchDetailFragment.arguments = bundle
                fragmentTransaction.setReorderingAllowed(true)
                fragmentTransaction.setCustomAnimations(
                    R.anim.fragment_fade_in,
                    R.anim.fragment_fade_out
                )
//                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frameLayout, searchDetailFragment,TAG);
                fragmentTransaction.commit()
            }

            R.id.layout_search ->{

            }

            R.id.layout_delete ->{
                val builder: AlertDialog.Builder = AlertDialog.Builder(view!!.context)
                val dialog: AlertDialog = builder.setTitle("검색 기록")
            .setMessage("삭제 하시겠습니까?")     // 제목 부분 (직접 작성)
            .setPositiveButton("확인", object: DialogInterface.OnClickListener {      // 버튼1 (직접 작성)
         override fun onClick(dialog: DialogInterface?, which: Int) {
             // RoomDataBase MainThread 클래스 적용해서 서치목록 삭제
             AppExecutors.getInstance().diskIO().execute(Runnable {
                 feedViewModel.deleteSearchAll()
             })
         }
     })
     .setNegativeButton("취소", object :DialogInterface.OnClickListener {
         override fun onClick(dialog: DialogInterface?, which: Int) {
         }     // 버튼2 (직접 작성)
     }).create()

                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

            }

        }
    }


}