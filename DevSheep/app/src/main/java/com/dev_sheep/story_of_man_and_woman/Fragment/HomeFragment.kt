package com.dev_sheep.story_of_man_and_woman.Fragment

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.adapter.TestAdapter
import com.dev_sheep.story_of_man_and_woman.adapter.Test_tag_Adapter
import com.dev_sheep.story_of_man_and_woman.data.Test
import com.dev_sheep.story_of_man_and_woman.viewmodel.TestViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(){
    
    private val testViewModel: TestViewModel by viewModel()
    private var recyclerView : RecyclerView? = null
    private var recyclerViewTag : RecyclerView? = null
    private var tollbar : androidx.appcompat.widget.Toolbar? = null
    private var progressBar : ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home,null)
        setHasOptionsMenu(true);

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        tollbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar?
        progressBar = view.findViewById<View>(R.id.progressBar) as ProgressBar?
        recyclerViewTag = view.findViewById<View>(R.id.recyclerView_tag) as RecyclerView?

        // 프래그먼트에 toolbar 세팅
        (activity as AppCompatActivity).setSupportActionBar(tollbar)

        val layoutManager = GridLayoutManager(view.context, 1)
        val layoutManager_Tag = GridLayoutManager(view.context,4 )

        recyclerView?.layoutManager = layoutManager
        recyclerViewTag?.layoutManager = layoutManager_Tag

        val manager: FragmentManager? = activity?.supportFragmentManager

        testViewModel.getListPokemon().observe(this, Observer {
            val pokemons: List<Test> = it
            recyclerView?.adapter = TestAdapter(pokemons, view.context,childFragmentManager)
            recyclerViewTag?.adapter = Test_tag_Adapter(pokemons,view.context)

            if (pokemons.isNotEmpty()) {
                progressBar?.visibility = View.GONE
            }

        })


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.search -> {
                Toast.makeText(getActivity(), "search", Toast.LENGTH_SHORT).show();
            }
        }
        return true
    }
}
