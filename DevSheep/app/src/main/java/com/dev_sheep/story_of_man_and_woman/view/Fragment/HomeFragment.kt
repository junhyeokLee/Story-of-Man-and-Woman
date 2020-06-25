package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.utils.EndlessRecyclerViewScrollListener
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedRankAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import kotlinx.android.synthetic.main.adapter_rank.view.*
import me.relex.circleindicator.CircleIndicator
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {


    private val testViewModel: TestViewModel by viewModel()
    private var recyclerView : RecyclerView? = null
    private var recyclerViewTag : RecyclerView? = null
    private var viewpager : ViewPager? = null
    private var indicators: CircleIndicator? = null

    private var tollbar : androidx.appcompat.widget.Toolbar? = null
    private var progressBar : ProgressBar? = null
    var items: List<Test> = ArrayList()
    lateinit var mFeedAdapter: FeedAdapter
    lateinit var mTagAdapter: Test_tag_Adapter
    lateinit var mRankAdapter: FeedRankAdapter
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    private var limit: Int = 10
    private var offset: Int = 0

    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 2
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,null)

        setHasOptionsMenu(true);

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        tollbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar?
        progressBar = view.findViewById<View>(R.id.progressBar) as ProgressBar?
        recyclerViewTag = view.findViewById<View>(R.id.recyclerView_tag) as RecyclerView?
        viewpager = view.findViewById<View>(R.id.vp) as ViewPager?
        indicators = view.findViewById<CircleIndicator>(R.id.indicator)
        // 프래그먼트에 toolbar 세팅
        (activity as AppCompatActivity).setSupportActionBar(tollbar)



        initData()

        return view
    }
    private fun initData(){

        // 전체보기
//        testViewModel.getListPokemon().observe(this, Observer { test ->
//            setAdapter(test)
//        })

        // 페이징 처리
        testViewModel.getListFirst(limit,offset).observe(this, Observer { test ->
            setAdapter(test)
        })

    }

    private fun setAdapter(test:List<Test>) {
        mFeedAdapter = FeedAdapter(test,view!!.context,childFragmentManager)
        mTagAdapter = Test_tag_Adapter(test,view!!.context)
        mRankAdapter = FeedRankAdapter(view!!.context,childFragmentManager)
//        mFeedAdapter.notifyDataSetChanged()

        viewpager?.apply {
            adapter = mRankAdapter
            this.autoScroll(3000)
            indicators?.setViewPager(this)

        }

        recyclerView?.apply {
            var linearLayoutMnager = LinearLayoutManager(this.context)
            this.layoutManager = linearLayoutMnager
            adapter = mFeedAdapter

        }

        recyclerViewTag?.apply {
            val layoutManager_Tag = GridLayoutManager(view!!.context,2 )
            layoutManager_Tag.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = layoutManager_Tag
            adapter =  mTagAdapter
        }



        if (test.isNotEmpty()) {
            progressBar?.visibility = View.GONE
        }else {
            progressBar?.visibility = View.VISIBLE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){

            R.id.filter -> {
                Toast.makeText(getActivity(), "filter", Toast.LENGTH_SHORT).show();
            }
        }
        return true
    }

    fun ViewPager.autoScroll(interval: Long) {

        val handler = Handler()
        var scrollPosition = 0
        val runnable = object : Runnable {
            override fun run() {

                val count = adapter?.count ?: 0
                setCurrentItem(scrollPosition++ % count, true)
                handler.postDelayed(this, interval)

            }
        }
        handler.post(runnable)
    }


}
