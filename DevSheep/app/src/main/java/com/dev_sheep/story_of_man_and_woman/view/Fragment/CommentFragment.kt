package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed


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

    lateinit var contexts : Context
    lateinit var editText : EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comment, null)

        editText = view.findViewById(R.id.et_comment)
	    editText.requestFocus();

	//키보드 보이게 하는 부분
//        var imm : InputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        return view
    }
}