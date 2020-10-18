package com.dev_sheep.story_of_man_and_woman.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.dev_sheep.story_of_man_and_woman.R
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
                Log.e("Insert Member", "" + it.toString())

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
                } else if(it.toString() == "false") {
                    Toast.makeText(
                        context.applicationContext,
                        "이미 사용중인 이메일 입니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if(it.toString() == "false_nick_name"){
                    Toast.makeText(
                        context.applicationContext,
                        "이미 사용중인 닉네임 입니다.",
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

    fun editProfileImg(m_seq: String, profileImg: String){
        val single = memberService.editMemberProfile(m_seq,profileImg)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 profile edit", "" + it.profile_img)

            },{
                Log.e("실패 profile edit", "" + it.message)
            })

    }
    fun editProfileBackgroundImg(m_seq: String, profileBackgroundImg: String){
        val single = memberService.editMemberProfileBackground(m_seq,profileBackgroundImg)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 profile backgorund edit", "" + it.profile_img)

            },{
                Log.e("실패 profile backgorund edit", "" + it.message)
            })

    }


    fun getMemberCheck(
        email: String,
        password: String,
        context: Context
    ){
        val single = memberService.getMemberCheck(email, password)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 Login", "" + it.toString())

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
                        "email 또는 password를 확인해주세요.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, {
                Log.e("실패함 Insert Member", "" + it.message)
            })
    }

    fun memberSubscribe(target_m_seq:String,m_seq:String,type:String,count: TextView){
        val single = memberService.memberSubscribe(target_m_seq,m_seq,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(count.text == "구독하기" || count.text == "구독취소"){

                }else {
                    Log.e("성공함 구독", "" + it.toString())
                    if (it.toString() == "true") {
                        var value = Integer.parseInt(count.text.toString())
                        var result = value + 1
                        count.text = result.toString()
                    } else {
                        var value = Integer.parseInt(count.text.toString())
                        var result = value - 1
                        count.text = result.toString()
                    }
                }
            }, {
                Log.e("실패함 구독", "" + it.message)

            })
    }
    fun memberSubscribe2(target_m_seq:String,m_seq:String,type:String){
        val single = memberService.memberSubscribe(target_m_seq,m_seq,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                    Log.e("성공함 구독", "" + it.toString())

            }, {
                Log.e("실패함 구독", "" + it.message)

            })
    }

    fun memberSubscribeChecked(target_m_seq:String, m_seq:String, type:String, checkBox: CheckBox,context: Context){
        val single = memberService.memberSubscribe(target_m_seq,m_seq,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.toString() == "true"){
                    checkBox.isChecked = true
                    checkBox.text = "구독취소"
                    checkBox.setTextColor(context.getColor(R.color.white))
                }else{
                    checkBox.isChecked = false
                    checkBox.text = "구독하기"
                    checkBox.setTextColor(context.getColor(R.color.black))

                }

            }, {
            })
    }

    fun memberMySubscribeCount(m_seq: String,count: TextView){
        val single = memberService.memberMySubscribeCount(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                count.text = it
            }, {
            })
    }
    fun memberUserSubscribeCount(m_seq: String,count: TextView){
        val single = memberService.memberUserSubscribeCount(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                count.text = it
            }, {
            })
    }

    fun getSubscribing(m_seq: String){
        val single = memberService.getSubscribing(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 ", "" + it.toString())
            }, {
                Log.e("실패함", "" + it.message)

            })
    }
    fun getSubscriber(m_seq: String){
        val single = memberService.getSubsribers(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("성공함 ", "" + it.toString())
            }, {
                Log.e("실패함", "" + it.message)

            })
    }
}