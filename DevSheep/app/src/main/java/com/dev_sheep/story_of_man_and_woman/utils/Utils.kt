package com.dev_sheep.story_of_man_and_woman.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.App
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.ImageSize
import com.werb.library.MoreViewHolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by wanbo on 2017/7/14.
 */
object Utils {

    fun sendTime(ms: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = ms
        val format = SimpleDateFormat("MM/dd HH:mm")
        return format.format(Date(ms))
    }

    fun dp2px(dpValue: Float): Int {
        val scale = App.myApp.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getIMImageSize(width: Double, height: Double): ImageSize {
        val fixWidth: Int
        val fixHeight: Int
        var ratio = width.toFloat() / height.toFloat()
        if (ratio > 1) {
            if (ratio > 3) {
                ratio = 3f
            }
            fixWidth = dp2px(500f)
            fixHeight = (fixWidth / ratio).toInt()
        } else {
            if (ratio < 0.3) {
                ratio = 0.3f
            }
            fixHeight = dp2px(300f)
            fixWidth = (ratio * fixHeight).toInt()
        }
        return ImageSize(fixWidth, fixHeight)
    }

    fun readImageSize(path: String): ImageSize? {
        val orFile = File(path)
        if (!orFile.exists()) {
            return null
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(orFile.absolutePath, options)
        return ImageSize(options.outWidth, options.outHeight)
    }

    fun getProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 10f
            centerRadius = 50f
            start()
        }
    }

    fun ImageView.loadImage(uri: String?, progressDrawable: CircularProgressDrawable) {
        val options = RequestOptions()
            .placeholder(progressDrawable)
            .error(R.mipmap.ic_launcher_round)
        Glide.with(this.context)
            .setDefaultRequestOptions(options)
            .load(uri)
            .into(this)
    }

}