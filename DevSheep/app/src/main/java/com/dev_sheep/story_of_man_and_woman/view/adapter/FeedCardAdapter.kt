package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedRankActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel


class FeedCardAdapter (
    private val feed: List<Feed>,
    private val context: Context,
    private val feedViewModel: FeedViewModel
) : PagerAdapter() {
    lateinit var layoutInflater: LayoutInflater

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
//                        context.transitionName = position.toString()
                (context as Activity).startActivity(lintent)
                (context as Activity).overridePendingTransition(
                    R.anim.fragment_fade_in,
                    R.anim.fragment_fade_out
                )
            }
        }

//        imageView.setImageResource(models.get(position).getImage())
        if(position == 0){
            tv_content.text = "오늘의 Top 100"
            mFeedCardItemAdater = FeedCardItemAdapter(feed,context,object :FeedCardItemAdapter.OnClickViewListener{
                override fun OnClickFeed(
                    feed: Feed,
                    cb: CheckBox,
                    cb2: CheckBox,
                    position: Int
                ) {
                    feedViewModel.increaseViewCount(feed.feed_seq)

                    val lintent = Intent(context, FeedActivity::class.java)
                    lintent.putExtra("feed_seq", feed.feed_seq)
                    lintent.putExtra("checked" + feed.feed_seq, cb.isChecked)
                    lintent.putExtra("creater_seq", feed.creater_seq)
                    lintent.putExtra("bookmark_checked" + feed.feed_seq, cb2.isChecked)
                    lintent.putExtra(FeedActivity.EXTRA_POSITION, position)
//                        context.transitionName = position.toString()
                    (context as Activity).startActivity(lintent)
                    (context as Activity).overridePendingTransition(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                    )
                }

            })
            recyclerview?.apply {
                var linearLayoutMnager = LinearLayoutManager(this.context)
                this.layoutManager = linearLayoutMnager
                this.itemAnimator = DefaultItemAnimator()
                this.adapter = mFeedCardItemAdater

            }

        }else if(position == 1){
            tv_content.text = "추천사연"
        }else if(position == 2){
            tv_content.text = "Best 5"
        }else if(position == 3){
            tv_content.text = "이번주 Top 10"
        }else if(position == 4){
            tv_content.text =  "공감카드"
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

