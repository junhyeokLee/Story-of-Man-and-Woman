package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.mikhaellopez.circularimageview.CircularImageView
import java.nio.file.Path

class ImageDialog(context: Context, imagePath: String) : View.OnClickListener {

    private val dlg = Dialog(context,android.R.style.Theme_Black)

    private lateinit var img_back : ImageButton
    private lateinit var img_url : ImageView
    var img = imagePath
    fun start(content: String){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_imageurl)     //다이얼로그에 사용할 xml 파일을 불러옴
//        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        dlg.window.getAttributes().windowAnimations = R.style.DialogAnimation;
        dlg.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//        dlg.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        dlg.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        img_back = dlg.findViewById(R.id.im_back)
        img_url = dlg.findViewById(R.id.id_Profile_Image)

        with(img_url){
            Glide.with(this)
                .load(img)
                .centerInside()
                .placeholder(android.R.color.transparent)
                .into(this)
        }



        img_back.setOnClickListener(this)

        dlg.show()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.im_back ->{
                dlg.dismiss()
            }

        }
    }

}
