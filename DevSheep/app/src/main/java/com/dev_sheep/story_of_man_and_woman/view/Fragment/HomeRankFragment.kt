package com.dev_sheep.story_of_man_and_woman.view.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dev_sheep.story_of_man_and_woman.R

class HomeRankFragment(position: Int) : Fragment() {
    private val ARG_PARAM1 = "param1"
    private var mParam1 = 0
    private var position = position

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rank,null)
        val tv:TextView = view.findViewById(R.id.tv_number)

        if (arguments != null) {
            mParam1 = arguments!!.getInt(ARG_PARAM1)
        }
        tv.setText(""+position)

//                chefImage.drawable.setColorFilter(
//                    ContextCompat.getColor(view.context, R.color.colorPrimary),
//                    PorterDuff.Mode.DST_ATOP)
//                experience.text = view.context.getString(R.string.chef_years_of_experience)

//                messageButton.setOnClickListener { presenter.onMessageChef(this) }
//
//                setFavoriteDrawable(chefModel.favorited,view,favoriteButton)
//                favoriteButton.setOnClickListener {
//                    val favorited = !chefModel.favorited
//                    setFavoriteDrawable(favorited)
//                    presenter.onFavoriteChef(this, favorited)
//                }
//
//                setBookmarkDrawable(chefModel.bookmarked)
//                bookmarkButton.setOnClickListener {
//                    val bookmarked = !chefModel.bookmarked
//                    setBookmarkDrawable(bookmarked,view,bookmarkButton)
//                    presenter.onBookmarkChef(this, bookmarked)
//                }
//
//            view.setOnClickListener { presenter.onChefSelected(chefModel.getName()) }


        return view
    }
}