package com.dev_sheep.story_of_man_and_woman.viewmodel

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.dev_sheep.story_of_man_and_woman.data.remote.api.FeedService
import com.dev_sheep.story_of_man_and_woman.data.remote.api.MemberService
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MemberViewModel(private val memberService: MemberService) :  ViewModel(){

    fun insertMember(
        email: String,
        password: String,
        nick_name: String,
        gender: String,
        age: String,
        context: Context
    ){
        val single = memberService.insertMember(email, password, nick_name, gender, age)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 Insert Member", "" + it.toString())

                if (it.toString() == "true") { // email 중복되면 false 반환

                    // 회원가입시 자동로그인 하기위해 email,password 저장
                    val auto = context.getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)

                    // auto의 loginEmail , loginPassword에 값을 저장해 줍니다.
                    val autoLogin : SharedPreferences.Editor = auto.edit()
                    autoLogin.putString("inputEmail", email);
                    autoLogin.putString("inputPassword",password);
                    //꼭 commit()을 해줘야 값이 저장됩니다 ㅎㅎ
                    autoLogin.commit();

                    val intent = Intent(context, MainActivity::class.java)
                    context.applicationContext.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
                } else {
                    Toast.makeText(
                        context.applicationContext,
                        "이미 사용중인 이메일 입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, {
                Log.e("실패함 Insert Member", "" + it.message)
            })
    }

    fun getMemberSeq(email: String,password: String,context: Context){

        val single = memberService.getMemberSeq(email,password)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 Get Member", "" + it.m_seq)

                val getMseq = context.getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
                val m_seq = it.m_seq
                val getSeq : SharedPreferences.Editor = getMseq.edit()
                getSeq.putString("inputMseq", m_seq);
                //꼭 commit()을 해줘야 값이 저장됩니다 ㅎㅎ
                getSeq.commit();

            },{
                Log.e("실패 Get Member", "" + it.message)
    })

    }

}