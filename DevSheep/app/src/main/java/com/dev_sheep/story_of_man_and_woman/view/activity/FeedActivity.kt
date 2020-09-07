package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_feed.img_profile
import kotlinx.android.synthetic.main.activity_feed.tv_feed_date
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedActivity : AppCompatActivity() ,View.OnClickListener{

    private val feedViewModel: FeedViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        write_content.setInputEnabled(false)

        write_content
            .setEditorFontSize(16)
            .setEditorPadding(16, 16, 16, 8)

        im_back.setOnClickListener { onBackPressed() }

//        write_content.isHorizontalScrollBarEnabled.not
//        write_content.isVerticalScrollBarEnabled.not()
//        write_content.requestDisallowInterceptTouchEvent(true)
//        write_content.setOnTouchListener { v, event ->
//             event.action == MotionEvent.ACTION_MOVE
//        }

        getIntentFeed()
    }

    fun getIntentFeed(){

        if(intent.hasExtra("feed_seq")){

            val feed_seq = intent.getIntExtra("feed_seq",0)
            var favCount : String
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = preferences.edit()
            val single = FEED_SERVICE.getFeed(feed_seq)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("feed_creater",""+it.creater)
                    tv_creater.text = it.creater
                    tv_tag_name.text = "# "+it.tag_name
                    write_headline.text = it.title
                    write_content.html = it.content
                    tv_feed_date.text = it.feed_date!!.substring(0, 10);
                    view_count.text = it.view_no.toString()
                    like_count.text = it.like_no.toString()

                    check_follow.setOnClickListener(this)
                    img_profile.setOnClickListener(this)
                    Glide.with(this)
                        .load(it.creater_image_url)
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(img_profile)

                    with(favorite_btn){

                        if(intent.hasExtra("feed_seq")){
                            val feed_seq = intent.getIntExtra("feed_seq",0)

                            if(intent.hasExtra("checked"+feed_seq)) {
                                val checked = intent.getBooleanExtra("checked"+feed_seq,false)
                                this.isChecked = checked

                                if(checked == true){
                                    setOnCheckedChangeListener { compoundButton, b ->
                                        if(this.isChecked){
                                            feedViewModel.increaseLikeCount(feed_seq,"true")
                                            favCount = it.like_no.toString()
                                            editor.putBoolean("checked"+feed_seq, true)
                                            editor.apply()
                                        }else{
                                            feedViewModel.increaseLikeCount(feed_seq,"false")
                                            favCount = it.like_no?.minus(1).toString()
                                            editor.putBoolean("checked"+feed_seq, false)
                                            editor.apply()
                                        }
                                        like_count.text = favCount
                                    }
                                }else{
                                    setOnCheckedChangeListener { compoundButton, b ->
                                        if(this.isChecked){
                                            feedViewModel.increaseLikeCount(feed_seq,"true")
                                            favCount = it.like_no?.plus(1).toString()
                                            editor.putBoolean("checked"+feed_seq, true)
                                            editor.apply()
                                        }else{
                                            feedViewModel.increaseLikeCount(feed_seq,"false")
                                            favCount = it.like_no.toString()
                                            editor.putBoolean("checked"+feed_seq, false)
                                            editor.apply()
                                        }
                                        like_count.text = favCount
                                    }
                                }

                            }
                        }
                    }



                },
                    {
                        Log.e("errors",it.message)
                    })

        } else{
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.check_follow -> {
                if(check_follow.isChecked == true){
                    check_follow.setTextColor(resources.getColor(R.color.white))
                    check_follow.text = "구독중"
                }else{
                    check_follow.setTextColor(resources.getColor(R.color.black))
                    check_follow.text = "구독하기"
                }
            }
            R.id.img_profile -> {
                Toast.makeText(this, "유저 페이지로 이동하기", Toast.LENGTH_SHORT).show()

            }
        }
    }


}