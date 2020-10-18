package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribersAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.SubscribingAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_subscribing.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SubscribingFragment(val m_seq: String): Fragment() {
    private var rv_subscribers : RecyclerView? = null
    private val memberViewModel: MemberViewModel by viewModel()
    lateinit var BtnBack: ImageButton
    lateinit var mSubscribingAdapter: SubscribingAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_subscribing, null)
        rv_subscribers = view.findViewById<View>(R.id.rv_subscribers) as RecyclerView?
        BtnBack = view.findViewById(R.id.im_back) as ImageButton

//        (activity as AppCompatActivity).supportActionBar!!.setDisplayShowHomeEnabled(true)
//        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val layoutManager_Tag = GridLayoutManager(view.context, 1)
        rv_subscribers?.layoutManager = layoutManager_Tag

        val single = MEMBER_SERVICE.getSubscribing(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("list member", "" + it)

                if (it.isNotEmpty()) {
                    mSubscribingAdapter = SubscribingAdapter(it, view.context, memberViewModel,m_seq)
                    rv_subscribers?.apply {
//                        this?.layoutManager = layoutManager_Tag
                        this.adapter = mSubscribingAdapter
                    }
                } else {

                }
            }, {
                Log.e("실패 Get Member", "" + it.message)
            })

        BtnBack.setOnClickListener{
            activity?.onBackPressed()
        }


        return view
    }


}