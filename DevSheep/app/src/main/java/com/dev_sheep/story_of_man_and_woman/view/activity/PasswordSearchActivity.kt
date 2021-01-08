package com.dev_sheep.story_of_man_and_woman.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService.MEMBER_SERVICE
import kotlinx.android.synthetic.main.activity_password_search.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class PasswordSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_search)

        im_back.setOnClickListener {
            onBackPressed()
        }

        btn_send_email.setOnClickListener {
            if(isEmail(et_email.text.toString()) == false){
                Toast.makeText(applicationContext, "이메일 형식을 확인해주세요.", Toast.LENGTH_SHORT).show()
            }else{

                MEMBER_SERVICE.passwordSearchSend(et_email.text.toString())
                Toast.makeText(applicationContext, "전송완료.", Toast.LENGTH_SHORT).show()

            }
        }

    }

    // 이메일 정규식
    fun isEmail(email: String?): Boolean {
        var returnValue = false
        val regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$"
        val p: Pattern = Pattern.compile(regex)
        val m: Matcher = p.matcher(email)
        if (m.matches()) {
            returnValue = true
        }
        return returnValue
    }
}