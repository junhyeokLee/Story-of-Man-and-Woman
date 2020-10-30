package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_Searchtag_Adapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private var recyclerViewTag : RecyclerView? = null
    private val feedViewModel: FeedViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search,null)
        recyclerViewTag = view.findViewById<View>(R.id.recyclerView_tag) as RecyclerView?

        val layoutManager_Tag = GridLayoutManager(view.context,2 )
        recyclerViewTag?.layoutManager = layoutManager_Tag
//        (recyclerViewTag?.layoutManager as GridLayoutManager).canScrollHorizontally().not()


//        testViewModel.getListPokemon().observe(this, Observer {
//            val pokemons: List<Test> = it
//
//            recyclerViewTag?.adapter = Test_Searchtag_Adapter(pokemons,view.context)
//
//            if (pokemons.isNotEmpty()) {
//                progressBar_tag?.visibility = View.GONE
//            }else {
//                progressBar_tag?.visibility = View.VISIBLE
//            }
//
//        })


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