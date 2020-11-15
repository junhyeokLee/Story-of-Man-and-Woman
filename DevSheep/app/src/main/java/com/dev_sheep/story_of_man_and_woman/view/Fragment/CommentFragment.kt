package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.FEED_SERVICE
import com.dev_sheep.story_of_man_and_woman.view.adapter.CommentAdapter
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.androidx.viewmodel.ext.android.viewModel


class CommentFragment : Fragment(),SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun newInstance(feed: Feed): CommentFragment {
            val args = Bundle()
            args.putString("feed_seq",feed.feed_seq.toString())
            val fragment = CommentFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val feedViewModel: FeedViewModel by viewModel()

    lateinit var contexts : Context
    lateinit var editText : EditText
    lateinit var feed_seq : String
    lateinit var m_seq : String
    lateinit var recyclerview_comments: RecyclerView
    lateinit var mCommentAdapter : CommentAdapter
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mShimmerViewContainer: ShimmerFrameLayout
    lateinit var iv_back : ImageView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment, null)
        recyclerview_comments = view.findViewById(R.id.recyclerview_comments)
        iv_back = view.findViewById(R.id.im_back)
        mSwipeRefreshLayout = view.findViewById<View>(R.id.sr_refresh) as SwipeRefreshLayout
        mShimmerViewContainer = view.findViewById<View>(R.id.shimmer_view_container) as ShimmerFrameLayout

        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            R.color.main_Accent
        );
        val layoutManager = GridLayoutManager(view.context, 1)
        recyclerview_comments?.layoutManager = layoutManager

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
                    feedViewModel.addComment(m_seq,Integer.parseInt(feed_seq),editText.text.toString())
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
        feed_seq = arguments?.getString("feed_seq").toString()
        val handlerFeed: Handler = Handler(Looper.myLooper())

        val single = FEED_SERVICE.getComment(Integer.parseInt(feed_seq))
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if(it.size > 0) {
                    mCommentAdapter = CommentAdapter(it, context!!, feedViewModel)

                    handlerFeed.postDelayed({
                        // stop animating Shimmer and hide the layout
                        mShimmerViewContainer.stopShimmerAnimation()
                        mShimmerViewContainer.visibility = View.GONE
//                    progressBar?.visibility = View.GONE

                        recyclerview_comments.apply {
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


        iv_back.apply {
            setOnClickListener {
                activity?.onBackPressed()
            }
        }
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