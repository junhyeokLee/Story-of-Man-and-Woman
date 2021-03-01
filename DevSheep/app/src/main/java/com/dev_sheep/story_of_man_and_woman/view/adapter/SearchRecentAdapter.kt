package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.AppExecutors
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Search
import com.dev_sheep.story_of_man_and_woman.view.Fragment.SearchDetailFragment
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.adapter_search_recent.view.*

class SearchRecentAdapter(
    private val context: Context,
    private var mSearch: List<Search>,
    private val feedViewModel: FeedViewModel,
    private val activity: FragmentActivity
)
    : RecyclerView.Adapter<SearchRecentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String


        fun bindView(item: Search,feedViewModel:FeedViewModel,activity:FragmentActivity) {
            with(itemView.tv_recent){
                text = item.title
                setOnClickListener {
                    val searchDetailFragment = SearchDetailFragment()//The fragment that u want to open for example
                    var SearchFragment = activity?.supportFragmentManager
                    var fragmentTransaction: FragmentTransaction = SearchFragment!!.beginTransaction()
                    var bundle = Bundle()
                    bundle.putString("tv_search",text.toString())
                    searchDetailFragment.arguments = bundle
                    fragmentTransaction.setReorderingAllowed(true)
                    fragmentTransaction.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frameLayout, searchDetailFragment);
                    fragmentTransaction.commit()
                }
            }

            with(itemView.iv_delete){
                setOnClickListener {
                    // RoomDataBase MainThread 클래스 적용해서 서치목록 삭제
                    AppExecutors.getInstance().diskIO().execute(Runnable {
                        feedViewModel.deleteSearchId(item.id)
                    })
                }
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_search_recent, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mSearch.get(position)
        holder.bindView(item,feedViewModel,activity)
    }

    override fun getItemCount(): Int {
        return mSearch.size
    }



}