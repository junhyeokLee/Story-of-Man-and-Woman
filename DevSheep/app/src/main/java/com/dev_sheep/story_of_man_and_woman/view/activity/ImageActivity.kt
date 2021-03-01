package com.dev_sheep.story_of_man_and_woman.view.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedActivity.Companion.FEED_SEQ
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_imageurl.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.koin.androidx.viewmodel.ext.android.viewModel
import technolifestyle.com.imageslider.FlipperLayout
import technolifestyle.com.imageslider.FlipperView
import technolifestyle.com.imageslider.pagetransformers.ZoomOutPageTransformer
import java.lang.Exception
import java.lang.reflect.Array.get
import java.nio.file.Paths.get

class ImageActivity : AppCompatActivity(), View.OnClickListener {
    private val feedViewModel: FeedViewModel by viewModel()
    private var feed_content: String? = null
    private var img_count : Int = 0
    private var select_count : String? = null
    private var back_btn:LinearLayout? = null
    private var back_btn2:ImageButton?=null
    lateinit var images : String
    private lateinit var flipperLayout: FlipperLayout
    private var feed_seq:Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imageurl)
        flipperLayout = findViewById(R.id.flipper_layout)
//        flipperLayout.addPageTransformer(false, ZoomOutPageTransformer())


        back_btn = findViewById(R.id.im_back)
        back_btn2 = findViewById(R.id.im_back_btn)
        back_btn?.setOnClickListener(this)
        back_btn2?.setOnClickListener(this)

        if(intent.hasExtra("select_url")) {
            select_count = intent.getStringExtra("select_url")
        }

//        if(intent.hasExtra("select_count")) {
//            select_count = intent.getIntExtra("select_count",0)
//        }

        val flipperViewList: ArrayList<FlipperView> = ArrayList()

        val single = APIService.FEED_SERVICE.getFeedImages(FEED_SEQ)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val images = it
            for(count in 0..images.size - 1){

                // 저장된 파일 이름과 같으면 그 포지션 값 체크하여 선택한 이미지 보여줌
                if(images.get(count).thumb.equals(select_count)){
                    img_count = count
                }

                val view = FlipperView(baseContext)
//             images = document.select("#feed_img"+count).attr("src")
            var currentCount = count + 1
                // 선택한 파일이름과 같으면 그 선택값 보여주기

            view.setDescription(""+ currentCount +" / "+images.size)
                .setDescriptionBackgroundColor(Color.TRANSPARENT)
                .resetDescriptionTextView()
                .setImageUrl(images.get(count).imagePath) { flipperImageView, image ->
             Picasso.get().load(images.get(count).imagePath as String).into(flipperImageView)
                }
             flipperViewList.add(view)

//                Log.e("ImageActivity Images",""+it.get(count).imagePath)
        }
                flipperLayout.addFlipperViewList(flipperViewList)
                flipperLayout.removeCircleIndicator()
                flipperLayout.removeAutoCycle()
                flipperLayout.removeCircleIndicator()
                flipperLayout.customizeFlipperPager {
                    it.currentItem = img_count
                }
//        flipperLayout.showCircleIndicator()
//        val view = FlipperView(baseContext)
//        view.setImageScaleType(ImageView.ScaleType.CENTER_CROP)


            }, {
                Log.d("Error MoreData", it.message.toString())
            })

//        Log.e("img Count",""+img_count)
//        val document: Document = Jsoup.parse(feed_content)

//        select_images = document.select("#feed_img"+select_count).attr("src")

//        for(count in 1..img_count){
//            val view = FlipperView(baseContext)
//             images = document.select("#feed_img"+count).attr("src")
//
//            view.setDescription(""+count+" / "+img_count)
//                .setDescriptionBackgroundColor(Color.TRANSPARENT)
//                .resetDescriptionTextView()
//                .setImageUrl(images) { flipperImageView, image ->
//             Picasso.get().load(image as String).into(flipperImageView)
//                }
//            flipperViewList.add(view)
//            Log.e("ImageActivity Images",""+images)
//
//        }

//        flipperLayout.addFlipperViewList(flipperViewList)
//        flipperLayout.removeCircleIndicator()
//        flipperLayout.removeAutoCycle()
//        flipperLayout.removeCircleIndicator()
////        flipperLayout.showCircleIndicator()
////        val view = FlipperView(baseContext)
////        view.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//        flipperLayout.customizeFlipperPager {
//            it.currentItem = select_count
//        }

        //  마지막 사진에 넣을 광고 참고해볼만 할듯

//        view.setDescription("This is Black Panther II from new Marvel Movies")
//        view.setImage(R.drawable.error) { imageView, image ->
//            imageView.setImageDrawable(image as Drawable)
//        }
//        flipperLayout.addFlipperView(view)
//        flipperLayout.onCurrentPageChanged(3)


    }


    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.im_back ->{
                onBackPressed()
            }
            R.id.im_back_btn ->{
                onBackPressed()
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}