package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_profile_subscribe.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment :  Fragment()  {

    private val feedViewModel: FeedViewModel by viewModel()
    private var recyclerView: RecyclerView? = null
    private var empty : LinearLayout? = null
    lateinit var mFeedAdapter: FeedAdapter
    lateinit var contexts: Context
    lateinit var m_seq : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification,null)
        // ToolBar를 ActionBar로 설정해줘야 합니다.
//        (activity as AppCompatActivity).setSupportActionBar(app_toolbar)
        contexts = view.context
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView?
        empty = view.findViewById(R.id.empty) as LinearLayout
        initData()
        return view
    }


    private fun initData(){
        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")


        val single = FEED_SERVICE.getListNotificationSubscribe(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.isEmpty()){
                    empty?.visibility = View.VISIBLE
                }

                mFeedAdapter = FeedAdapter(
                    it,
                    contexts,
                    object : FeedAdapter.OnClickViewListener{
                        override fun OnClickFeed(feed: Feed, tv: TextView, iv: ImageView, cb: CheckBox, cb2: CheckBox, position:Int) {
                            feedViewModel.increaseViewCount(feed.feed_seq)

                            val lintent = Intent(context, FeedActivity::class.java)
                            lintent.putExtra("feed_seq", feed.feed_seq)
                            lintent.putExtra("checked" + feed.feed_seq, cb.isChecked)
                            lintent.putExtra("creater_seq", feed.creater_seq)
                            lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                            lintent.putExtra(FeedActivity.EXTRA_POSITION, position)

//                        context.transitionName = position.toString()
                            context!!.startActivity(lintent)
                            (context as Activity).overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)

                        }
                    },
                    object : FeedAdapter.OnClickLikeListener {
                        override fun OnClickFeed(feed_seq: Int, boolean_value: String) {
                            feedViewModel.increaseLikeCount(feed_seq, boolean_value)
                        }

                    },object : FeedAdapter.OnClickBookMarkListener{
                        override fun OnClickBookMark(m_seq: String, feed_seq: Int, boolean_value: String) {
                            feedViewModel.onClickBookMark(m_seq,feed_seq,boolean_value)
                        }

                    }, object : FeedAdapter.OnClickProfileListener{
                        override fun OnClickProfile(feed: Feed, tv: TextView, iv: ImageView) {
                            val trId = ViewCompat.getTransitionName(tv).toString()
                            val trId1 = ViewCompat.getTransitionName(iv).toString()
                            activity?.supportFragmentManager
                                ?.beginTransaction()
                                ?.addSharedElement(tv, trId)
                                ?.addSharedElement(iv, trId1)
                                ?.addToBackStack("ProfileImg")
                                ?.replace(R.id.frameLayout, ProfileUsersFragment.newInstance(feed, trId, trId1))
                                ?.commit()
                        }

                    },object  : FeedAdapter.OnClickDeleteFeedListener{
                        override fun OnClickDeleted(feed_seq: Int) {
                            showDeletePopup(feedViewModel,feed_seq)
                            onResume()

                        }

                    })
                recyclerView?.apply {
                    var linearLayoutMnager = LinearLayoutManager(this.context)
                    this.layoutManager = linearLayoutMnager
                    this.itemAnimator = DefaultItemAnimator()
                    this.adapter = mFeedAdapter
                }
//                if (it.isNotEmpty()) {
//                    progressBar?.visibility = View.GONE
//                } else {
//                    progressBar?.visibility = View.VISIBLE
//                }

            }, {
                Log.e("feed 보기 실패함", "" + it.message)
            })
    }
    private fun showDeletePopup(feedViewmodel: FeedViewModel,feed_seq: Int) {
        val inflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_popup, null)
        val textView: TextView = view.findViewById(R.id.textView)

        textView.text = "이 게시물을 삭제 하시겠습니까?"

        val alertDialog = AlertDialog.Builder(context!!)
            .setTitle("삭제")
            .setPositiveButton("네") { dialog, which ->
                feedViewmodel.deleteFeed(feed_seq)
            }

            .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->

            })
            .create()

        alertDialog.setView(view)
        alertDialog.show()

        val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        if(btn_color != null){
            btn_color.setTextColor(resources.getColor(R.color.main_Accent))
        }
        if(btn_color_cancel != null){
            btn_color_cancel.setTextColor(resources.getColor(R.color.main_Accent))
        }
    }
    override fun onResume() {
        super.onResume()
        initData()

    }
}