package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.NotificationAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_alarm.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class AlarmActivity : AppCompatActivity() {
    lateinit var mNotificationAdapter: NotificationAdapter
    private val memberViewModel: MemberViewModel by viewModel()
    private val feedViewModel: FeedViewModel by viewModel()
    lateinit var m_seq : String // 자신의 seq
    lateinit var context: Context
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var limit: Int = 10
    private var offset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        context = this

        initData()

        iv_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initData(){

        // my_m_seq 가져오기
        val preferences: SharedPreferences = this!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )

        m_seq = preferences.getString("inputMseq", "")

        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        val preferences_like_check = PreferenceManager.getDefaultSharedPreferences(context)
        var likeCheck: Boolean? = null
        var bookMarkCheck: Boolean? = null

        val single = MEMBER_SERVICE.getNotification(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    mNotificationAdapter = NotificationAdapter(it,this,memberViewModel,object :NotificationAdapter.OnClickSubscribeListener{
                        override fun OnClickSubscribe(m_seq: String) {
                            val intent = Intent(context, MainActivity::class.java)
                            intent.putExtra("m_seq", m_seq)
                            intent.putExtra("ProfileUsersFragment", true)
                            startActivity(intent)
                            overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                            finish()
                        }
                    },object : NotificationAdapter.OnClickFeedViewListener{
                        override fun OnClickFeedView(feed_seq: Int) {
                            // 피드 좋아요 및 북마크 체크
                            if (preferences_like_check.contains("checked" + feed_seq) && preferences_like_check.getBoolean("checked" + feed_seq, false) == true) {
                                likeCheck = true
                            } else {
                                likeCheck = false
                            }
                            if (preferences_like_check.contains("bookmark_checked" + feed_seq) && preferences_like_check.getBoolean("bookmark_checked" + feed_seq, false) == true) {
                                bookMarkCheck = true
                            } else {
                                bookMarkCheck = false
                            }
                            // 피드 조회수 증가
                            feedViewModel.increaseViewCount(feed_seq)
                            // 피드 타이틀 가져와서 넘겨주기
                            FEED_SERVICE.getFeedTitle(feed_seq)
                            .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    var feed_title: String
                                    feed_title = it.toString()
                                    val intent = Intent(context, FeedActivity::class.java)
                                    intent.putExtra("feed_seq", feed_seq)
                                    intent.putExtra("feed_title",feed_title)
                                    intent.putExtra("creater_seq", m_seq)
                                    intent.putExtra("checked" + feed_seq, likeCheck!!)
                                    intent.putExtra("bookmark_checked" + feed_seq, bookMarkCheck!!)
                                    intent.putExtra(FeedActivity.EXTRA_POSITION, 0)
                                    startActivity(intent)
                                    overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                                    finish()
                                },{
                                    Log.d("get Feed Title Failed = ",it.message.toString())

                                })


                        }

                    }, object : NotificationAdapter.OnEndlessScrollListener {
                        override fun OnEndless(boolean_value: Boolean) {
                            if (boolean_value == false) {
                                EndlessScroll(false)
                            } else if (boolean_value == true) {
                                EndlessScroll(true)
                            }
                        }

                    })

                    recyclerview_alarm?.apply {
                        this.layoutManager = linearLayoutManager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mNotificationAdapter
                    }

            },{
                Log.e("Notification 보기 실패함", "" + it.message)
            })

    }

    fun EndlessScroll(isLoading: Boolean){
        // 무한스크롤
        recyclerview_alarm!!.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isLoading == false) {
                        val single = MEMBER_SERVICE.getNotification(m_seq,offset,addLimit())
                        single.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                mNotificationAdapter.updateList(it)
                            }, {
                                Log.d("Error MoreData", it.message.toString())
                            })
                    }
                }
            }
        })
    }


    private fun addLimit() : Int{
        limit += 10
        return limit
    }
}