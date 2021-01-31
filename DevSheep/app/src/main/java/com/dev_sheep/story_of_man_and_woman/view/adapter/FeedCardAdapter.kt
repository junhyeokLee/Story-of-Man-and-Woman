package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedRankActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class FeedCardAdapter(
    private val context: Context
) : PagerAdapter() {
    lateinit var layoutInflater: LayoutInflater
    // my_m_seq 가져오기
    val preferences: SharedPreferences = context!!.getSharedPreferences(
        "m_seq",
        Context.MODE_PRIVATE
    )
    var m_seq = preferences.getString("inputMseq", "")
    var my_Age = preferences.getString("inputAge", "")
    var my_NickName = preferences.getString("inputNickName", "")
    var my_Gender = preferences.getString("inputGender", "")


    override fun getCount(): Int {
        return 5
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(R.layout.adapter_feed_card, container, false)
        lateinit var mFeedCardItemAdater: FeedCardItemAdapter
        var my_age : String


        val tv_content: TextView
        val recyclerview: RecyclerView
        val cv_feed: CardView
//        val desc: TextView

        tv_content = view.findViewById(R.id.tv_content)
        recyclerview = view.findViewById(R.id.recyclerview)
        cv_feed = view.findViewById(R.id.cv_feed)
//        desc = view.findViewById(R.id.desc)

        with(cv_feed){
            setOnClickListener {
                val lintent = Intent(context, FeedRankActivity::class.java)
                lintent.putExtra("tv_title", tv_content.text.toString())
                val options: ActivityOptionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,
                tv_content as TextView, "RankName")
            (context as Activity).startActivity(lintent, options.toBundle())

            }
        }

//        imageView.setImageResource(models.get(position).getImage())
        if(position == 0){
            tv_content.text = "오늘의 관심사"
            val single = FEED_SERVICE.getTodayList(0,10)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFeedCardItemAdater = FeedCardItemAdapter(it, context,tv_content)
                        recyclerview?.apply {
                        var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutMnager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mFeedCardItemAdater
                    }
                }, {

                })

        }else if(position == 1){
//            memberViewModel.getMemberAge(m_seq ,1, tv_content)
            tv_content.text = my_Age+" 여성들이 좋아하는"
            tv_content.setTextColor(Color.parseColor("#ec6674"))

            val single = FEED_SERVICE.getAgeWomanRecommendList(my_Age,0,10)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFeedCardItemAdater = FeedCardItemAdapter(it, context,tv_content)
                    recyclerview?.apply {
                        var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutMnager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mFeedCardItemAdater

                    }
                }, {

                })
        }else if(position == 2){
//            memberViewModel.getMemberAge(m_seq ,2, tv_content)
            tv_content.text = my_Age+" 남성들이 좋아하는"
//            tv_content.setTextColor(R.color.main_Accent)8446CC
            tv_content.setTextColor(Color.parseColor("#7A5DC7"))
            val single = FEED_SERVICE.getAgeManRecommendList(my_Age,0,10)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFeedCardItemAdater = FeedCardItemAdapter(it, context,tv_content)
                    recyclerview?.apply {
                        var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutMnager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mFeedCardItemAdater

                    }
                }, {

                })
        }else if(position == 3){
            tv_content.text = "가장 많이 읽은 카드"
            val single = FEED_SERVICE.getViewRecommendList(0,10)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFeedCardItemAdater = FeedCardItemAdapter(it, context,tv_content)
                    recyclerview?.apply {
                        var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutMnager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mFeedCardItemAdater

                    }
                }, {

                })
        }else if(position == 4){
            tv_content.text =  "가장 많이 좋아한 카드"
            val single = FEED_SERVICE.getLikeRecommendList(0,10)
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mFeedCardItemAdater = FeedCardItemAdapter(it, context,tv_content)
                    recyclerview?.apply {
                        var linearLayoutMnager = LinearLayoutManager(this.context)
                        this.layoutManager = linearLayoutMnager
                        this.itemAnimator = DefaultItemAnimator()
                        this.adapter = mFeedCardItemAdater

                    }
                }, {

                })
        }



//        view.setOnClickListener {
//            val intent = Intent(context, DetailActivity::class.java)
//            intent.putExtra("param", models.get(position).getTitle())
//            context.startActivity(intent)
//            // finish();
//        }

        container.addView(view, 0)
        return view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
        (container as ViewPager).removeView(`object` as View)
    }
}

