package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.testService
import com.dev_sheep.story_of_man_and_woman.view.Fragment.PrefsFragment
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.dialog.ImageDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_feed_write.*
import kotlinx.android.synthetic.main.adapter_feed.view.*
import kotlinx.android.synthetic.main.adapter_feed.view.img_profile

class FeedActivity : AppCompatActivity() ,View.OnClickListener{



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        write_content.setInputEnabled(false)

        write_content
            .setEditorFontSize(16)
            .setEditorPadding(16, 16, 16, 8)

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

             val single = testService.getFeed(intent.getIntExtra("feed_seq",0))
             single.subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe({
                    Log.e("feed_creater",""+it.creater)
                     tv_creater.text = it.creater
                     tv_tag_name.text = "# "+it.tag_name
                     tv_m_nick.text = it.creater
                     write_headline.text = it.title
                     write_content.html = it.content
                     tv_feed_date.text = it.feed_date!!.substring(0, 10);
                     check_follow.setOnClickListener(this)
                     img_profile.setOnClickListener(this)
                     Glide.with(this)
                         .load(it.creater_image_url)
                         .apply(RequestOptions().circleCrop())
                         .placeholder(android.R.color.transparent)
                         .into(img_profile)
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