package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class CommentFragment : Fragment() {

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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment, null)

        feed_seq = arguments?.getString("feed_seq").toString()

        editText = view.findViewById(R.id.et_comment)
        editText.requestFocus();


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
                    Toast.makeText(context, "드로우블클릭", Toast.LENGTH_SHORT).show()
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
}