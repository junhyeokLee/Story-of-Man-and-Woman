package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Comment
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentReAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.calculateTime
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.adapter_comment.view.*
import kotlinx.android.synthetic.main.fragment_recomment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


class ReCommentFragment : Fragment() ,SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun newInstance(comment: Comment): ReCommentFragment {
            val args = Bundle()
            args.putString("comment_seq",comment.comment_seq.toString())
            val fragment = ReCommentFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val feedViewModel: FeedViewModel by viewModel()

    lateinit var contexts : Context
    lateinit var editText : EditText
    lateinit var comment_seq : String
    private var feed_seq : Int? = null
    //    private var writer_seq : String? = null
    private var group_seq : Int? = null
    private var depth : Int? = null


    lateinit var m_seq : String
    lateinit var recyclerview_recomments: RecyclerView
    lateinit var mCommentAdapter : CommentReAdapter
    lateinit var iv_back : ImageView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mShimmerViewContainer: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recomment, null)
        recyclerview_recomments = view.findViewById(R.id.recyclerview_recomments)
        iv_back = view.findViewById(R.id.im_back)

        mSwipeRefreshLayout = view.findViewById<View>(R.id.sr_refresh) as SwipeRefreshLayout
        mShimmerViewContainer = view.findViewById<View>(R.id.shimmer_view_container) as ShimmerFrameLayout

        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.main_Accent
        );

        val layoutManager = GridLayoutManager(view.context, 1)
        recyclerview_recomments?.layoutManager = layoutManager

        editText = view.findViewById(R.id.et_comment)
        editText.requestFocus();

        // my_m_seq 가져오기
        val preferences: SharedPreferences = context!!.getSharedPreferences(
            "m_seq",
            Context.MODE_PRIVATE
        )
        m_seq = preferences.getString("inputMseq", "")


        initData()

        editText.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= editText.getRight() - editText.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    feedViewModel.addReComment(Integer.parseInt(comment_seq),feed_seq!!
                        ,m_seq!!,group_seq!!, depth!!,editText.text.toString())
                    Toast.makeText(context, "완료.", Toast.LENGTH_SHORT).show();

                    activity!!.onBackPressed()
                    return@OnTouchListener true
                }
            }
            false
        })

        //키보드 보이게 하는 부분
//        var imm : InputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        return view
    }

    private fun initData(){
        comment_seq = arguments?.getString("comment_seq").toString()
        val handlerFeed: Handler = Handler(Looper.myLooper())
        var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val single = FEED_SERVICE.getCommentItem(Integer.parseInt(comment_seq))
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                tv_m_nick.text = it.writer
                tv_comment.text = it.comment
                tv_age.text = it.writer_age
                tv_gender.text = it.writer_gender
                tv_feed_date.text = calculateTime(sdf.parse(it.comment_date))
                like_count.text = it.like_no.toString()


                feed_seq = Integer.parseInt(it.feed_seq)
                group_seq = it.group_seq
                depth= it.depth

                editText.setText("@"+it.writer+" ")
                editText.setSelection(et_comment.text.length)

                Glide.with(context!!)
                    .load(it.writer_img)
                    .apply(RequestOptions().circleCrop())
                    .placeholder(android.R.color.transparent)
                    .into(img_profile)


                val single_recomment = FEED_SERVICE.getReComment(Integer.parseInt(it.feed_seq!!),it.group_seq!!)
                single_recomment.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        if(it.size > 0) {
                            mCommentAdapter = CommentReAdapter(it, context!!, feedViewModel)

                            handlerFeed.postDelayed({
                                // stop animating Shimmer and hide the layout
                                mShimmerViewContainer.stopShimmerAnimation()
                                mShimmerViewContainer.visibility = View.GONE

                                recyclerview_recomments.apply {
                                    this.adapter = mCommentAdapter
                                }
                            }, 1000)
                        }else{
                            mShimmerViewContainer.stopShimmerAnimation()
                            mShimmerViewContainer.visibility = View.GONE
                        }

                    },{
                        Log.e("get comment 실패 = ",it.message.toString())

                    })

            },{
                Log.e("get comment 실패 = ",it.message.toString())

            })





        iv_back.apply {
            setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }


    /** 몇분전, 방금 전,  */

    private object TIME_MAXIMUM {
        const val SEC = 60
        const val MIN = 60
        const val HOUR = 24
        const val DAY = 30
        const val MONTH = 12
    }


    fun calculateTime(date: Date): String? {
        val curTime = System.currentTimeMillis()
        val regTime = date.time
        var diffTime = (curTime - regTime) / 1000
        var msg: String? = null
        if (diffTime < TIME_MAXIMUM.SEC) {
            // sec
            msg = diffTime.toString() + " 초전"
        } else if (TIME_MAXIMUM.SEC.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MIN) {
            // min
            println(diffTime)
            msg = diffTime.toString() + " 분전"
        } else if (TIME_MAXIMUM.MIN.let { diffTime /= it; diffTime } < TIME_MAXIMUM.HOUR) {
            // hour
            msg = diffTime.toString() + " 시간전"
        } else if (TIME_MAXIMUM.HOUR.let { diffTime /= it; diffTime } < TIME_MAXIMUM.DAY) {
            // day
            msg = diffTime.toString() + " 일전"
        } else if (TIME_MAXIMUM.DAY.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MONTH) {
            // day
            msg = diffTime.toString() + " 달전"
        } else {
            msg = diffTime.toString() + " 년전"
        }
        return msg
    }

    override fun onResume() {
        super.onResume()
        initData()
        mShimmerViewContainer.startShimmerAnimation()

    }

    override fun onPause() {
        super.onPause()
        super.onPause()
        mShimmerViewContainer.stopShimmerAnimation()
    }

    override fun onRefresh() {
        initData()
        mSwipeRefreshLayout.setRefreshing(false)
    }



}