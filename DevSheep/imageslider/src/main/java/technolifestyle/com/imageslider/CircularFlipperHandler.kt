package technolifestyle.com.imageslider

import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE
import technolifestyle.com.imageslider.utils.CustomViewPager
import java.lang.Exception

internal class CircularFlipperHandler(private val mViewPager: CustomViewPager) : ViewPager.OnPageChangeListener {

    private var scrollState = SCROLL_STATE_IDLE
    private var nextPageAfterScrollStateChanged: Int? = null
    private var currentPageListener: CurrentPageListener? = null


    fun setCurrentPageListener(currentPageListener: CurrentPageListener) {
        this.currentPageListener = currentPageListener
    }

    override fun onPageSelected(position: Int) {
        currentPageListener!!.onCurrentPageChanged(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        scrollState = state

        nextPageAfterScrollStateChanged?.let {
            mViewPager.currentItem = it
            mViewPager.setOnTouchListener { v, event ->
                try {
                    v.onTouchEvent(event)
                    mViewPager.onInterceptTouchEvent(event)
                } catch (ex: Exception) {
                    false
                }
            }
            nextPageAfterScrollStateChanged = null
        }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val count = mViewPager.adapter!!.count

        if (scrollState == SCROLL_STATE_DRAGGING && positionOffset == 0f) {
            if (position == 0) {
                nextPageAfterScrollStateChanged = count - 1
            } else if (position == count - 1) {
                nextPageAfterScrollStateChanged = 0
            }
        }
    }

    internal interface CurrentPageListener {
        fun onCurrentPageChanged(currentPosition: Int)
    }

}
