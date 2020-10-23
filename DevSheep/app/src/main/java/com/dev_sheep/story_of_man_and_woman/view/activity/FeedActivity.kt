package com.dev_sheep.story_of_man_and_woman.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.Fragment.HomeFragment
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class FeedActivity : AppCompatActivity() ,View.OnClickListener{


    companion object {
        const val EXTRA_POSITION = "extra_position"
        const val DEFAULT_POSITION = -1
    }

    private val feedViewModel: FeedViewModel by viewModel()
    private val memberViewModel: MemberViewModel by viewModel()
    lateinit var m_seq : String
    lateinit var my_m_seq: String
    var position : Int? = null
    var checked : Boolean? = false
    var checked_bookmark: Boolean? = false
    var feed_seq: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)


        position = intent.getIntExtra(
            EXTRA_POSITION, DEFAULT_POSITION
        )
        if (position == DEFAULT_POSITION) {
            // EXTRA_POSITION not found in intent
            closeOnError()
            return
        }

        write_content.setInputEnabled(false)

        write_content
            .setEditorFontSize(16)
            .setEditorPadding(8, 16, 8, 8)

        im_back.setOnClickListener { onBackPressed() }


//        write_content.isHorizontalScrollBarEnabled.not
//        write_content.isVerticalScrollBarEnabled.not()
//        write_content.requestDisallowInterceptTouchEvent(true)
//        write_content.setOnTouchListener { v, event ->
//             event.action == MotionEvent.ACTION_MOVE
//        }

        getIntents()
        getFeed()
    }

    fun getIntents(){
        if(intent.hasExtra("creater_seq")) {
            m_seq = intent.getStringExtra("creater_seq")
            // 저장된 m_seq 가져오기
            val getM_seq = getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
            my_m_seq = getM_seq.getString("inputMseq", null)
        }

        if(intent.hasExtra("feed_seq")) {
            feed_seq = intent.getIntExtra("feed_seq", 0)
        }

        if(intent.hasExtra("checked" + feed_seq)) {
            checked = intent.getBooleanExtra("checked" + feed_seq, false)
        }

        if(intent.hasExtra("bookmark_checked" + feed_seq)) {
            checked_bookmark = intent.getBooleanExtra("bookmark_checked" + feed_seq, false)
        }
    }

    fun getFeed(){
        // 자신이 생성자이면 수정하기 버튼 활성화

        if(m_seq == my_m_seq){
            check_edit.apply {
                this.visibility = View.VISIBLE
                check_follow.visibility = View.GONE
            }
        }else{
            check_follow.apply {
                this.visibility = View.VISIBLE
                check_edit.visibility = View.GONE

                memberViewModel.memberSubscribeChecked(
                    m_seq,
                    my_m_seq,
                    "checked",
                    this,
                    context
                );
            }
        }

        profile_layout.setOnClickListener(this)
        var favCount : String
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()


        val single = FEED_SERVICE.getFeed(feed_seq!!)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                content_layout.visibility = View.VISIBLE
                progressbar_layout.visibility = View.GONE
            }
            .subscribe({

                tv_creater.text = it.creater
                tv_tag_name.text = "# " + it.tag_name
                write_headline.text = it.title
                write_content.html = it.content
                tv_feed_date.text = it.feed_date!!.substring(0, 10);
                view_count.text = it.view_no.toString()
                like_count.text = it.like_no.toString()

                check_follow.setOnClickListener(this)
                check_edit.setOnClickListener(this)
                img_profile.setOnClickListener(this)

                img_profile.transitionName = position.toString()
                Glide.with(this)
                    .load(it.creater_image_url)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .error(R.drawable.error_loading)
                    .into(img_profile)

                with(favorite_btn) {
                    this.isChecked = checked!!

                    if (checked == true) {
                        setOnCheckedChangeListener { compoundButton, b ->
                            if (this.isChecked) {
                                feedViewModel.increaseLikeCount(feed_seq!!, "true")
                                favCount = it.like_no.toString()
                                editor.putBoolean("checked" + feed_seq, true)
                                editor.apply()
                            } else {
                                feedViewModel.increaseLikeCount(feed_seq!!, "false")
                                favCount = it.like_no?.minus(1).toString()
                                editor.putBoolean("checked" + feed_seq, false)
                                editor.apply()
                            }
                            like_count.text = favCount
                        }
                    } else {
                        setOnCheckedChangeListener { compoundButton, b ->
                            if (this.isChecked) {
                                feedViewModel.increaseLikeCount(feed_seq!!, "true")
                                favCount = it.like_no?.plus(1).toString()
                                editor.putBoolean("checked" + feed_seq, true)
                                editor.apply()
                            } else {
                                feedViewModel.increaseLikeCount(feed_seq!!, "false")
                                favCount = it.like_no.toString()
                                editor.putBoolean("checked" + feed_seq, false)
                                editor.apply()
                            }
                            like_count.text = favCount
                        }
                    }
                }

                with(bookmark) {

                    this.isChecked = checked_bookmark!!
                    if (checked_bookmark == true) {
                        setOnCheckedChangeListener { compoundButton, b ->
                            if (this.isChecked) {
                                feedViewModel.onClickBookMark(my_m_seq, feed_seq!!, "true")
                                editor.putBoolean("bookmark_checked" + feed_seq, true)
                                editor.apply()
                            } else {
                                feedViewModel.onClickBookMark(my_m_seq, feed_seq!!, "false")
                                editor.putBoolean("bookmark_checked" + feed_seq, false)
                                editor.apply()
                            }
                        }
                    } else {
                        setOnCheckedChangeListener { compoundButton, b ->
                            if (this.isChecked) {
                                feedViewModel.onClickBookMark(my_m_seq, feed_seq!!, "true")
                                editor.putBoolean("bookmark_checked" + feed_seq, true)
                                editor.apply()
                            } else {
                                feedViewModel.onClickBookMark(my_m_seq, feed_seq!!, "false")
                                editor.putBoolean("bookmark_checked" + feed_seq, false)
                                editor.apply()
                            }
                        }
                    }
                }

            },
                {
                    Log.e("errors", it.message)
                })

        et_comment.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event!!.getAction() == MotionEvent.ACTION_DOWN){

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("feed_seq",feed_seq.toString())
                    intent.putExtra("CommentFragment", true)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                }

                return false;
            }
        })

    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.check_follow -> {
                if (check_follow.isChecked == true) {

                    check_follow.setTextColor(resources.getColor(R.color.white))
                    check_follow.text = "구독취소"
                    memberViewModel.memberSubscribe(m_seq, my_m_seq, "true", check_follow)
                } else {
                    check_follow.setTextColor(resources.getColor(R.color.black))
                    check_follow.text = "구독하기"
                    memberViewModel.memberSubscribe(m_seq, my_m_seq, "false", check_follow)
                }
            }
            R.id.check_edit -> {
                val intent = Intent(this, FeedEditActivity::class.java)
                intent.putExtra("feed_seq", feed_seq)

                startActivity(intent)
                overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)

                finish()

            }

            R.id.profile_layout -> {
                // 자신을 클릭시
                if (m_seq == my_m_seq) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("ProfileMyFragment", true)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)

                    finish()

                }
                // 유저 클릭시
                else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("m_seq", m_seq)
                    intent.putExtra("ProfileUsersFragment", true)
                    startActivity(intent)
                    overridePendingTransition(R.anim.fragment_fade_in, R.anim.fragment_fade_out)

                    finish()
                }

            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun closeOnError() {
        finish()
        Toast.makeText(this, "Feed data not available", Toast.LENGTH_SHORT).show()
    }

}