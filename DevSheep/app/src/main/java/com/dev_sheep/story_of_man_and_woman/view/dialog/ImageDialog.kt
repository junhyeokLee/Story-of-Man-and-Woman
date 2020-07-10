package com.dev_sheep.story_of_man_and_woman.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import com.dev_sheep.story_of_man_and_woman.R
import com.mikhaellopez.circularimageview.CircularImageView

class ImageDialog(context: Context, imagePath: Int) : View.OnClickListener {

    private val dlg = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)

    private lateinit var img_back : ImageButton
    private lateinit var img_url : ImageView
    var img = imagePath
    fun start(content: String){
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.dialog_imageurl)     //다이얼로그에 사용할 xml 파일을 불러옴
//        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        dlg.window.getAttributes().windowAnimations = R.style.DialogAnimation;
        dlg.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        img_back = dlg.findViewById(R.id.im_back)
        img_url = dlg.findViewById(R.id.id_Profile_Image)

        img_url.setImageResource(img)


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