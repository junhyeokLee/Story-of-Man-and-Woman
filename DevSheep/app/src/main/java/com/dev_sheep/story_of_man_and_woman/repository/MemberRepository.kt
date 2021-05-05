package com.dev_sheep.story_of_man_and_woman.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Notification
import com.dev_sheep.story_of_man_and_woman.data.remote.api.MemberService
import com.dev_sheep.story_of_man_and_woman.utils.RedDotImageView
import com.dev_sheep.story_of_man_and_woman.utils.RedDotImageView2
import com.dev_sheep.story_of_man_and_woman.view.Fragment.SearchTitleFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.AlarmActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.LoginActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.SignUpStartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MemberRepository(private val memberService: MemberService) {

    val memberListLiveData = MutableLiveData<MutableList<Member>>()
    val memberLiveData = MutableLiveData<Member>()
    val memberListNotiLiveData = MutableLiveData<MutableList<Notification>>()


    fun insertMember(email: String, password: String, nick_name: String, context: Context){
        val single = memberService.insertMember(email, password, nick_name)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.toString() == "true") { // email 중복되면 false 반환 nick_name 중복일땐 false_nick_name
                    // 회원가입시 파이어베이스 DB에도 저장
                    performRegister(email,password,nick_name,context) // firebase 저장
                    // 회원가입시 자동로그인 하기위해 email,password 저장
                    val auto = context.getSharedPreferences("autoLogin", AppCompatActivity.MODE_PRIVATE)
                    // auto의 loginEmail , loginPassword에 값을 저장해 줍니다.
                    val autoLogin : SharedPreferences.Editor = auto.edit()
                    autoLogin.putString("inputEmail", email);
                    autoLogin.putString("inputPassword",password);
                    //꼭 commit()을 해줘야 값이 저장됩니다 ㅎㅎ
                    autoLogin.commit();
                    val intent = Intent(context, SignUpStartActivity::class.java)
                    context.applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } else if(it.toString() == "false") {
                    Toast.makeText(context.applicationContext, "이미 사용중인 이메일 입니다.", Toast.LENGTH_SHORT).show()
                } else if(it.toString() == "false_nick_name"){
                    Toast.makeText(context.applicationContext, "이미 사용중인 닉네임 입니다.", Toast.LENGTH_SHORT).show()
                }
            }, {
                Log.d("실패함 Insert Member", "" + it.message)
            })
    }

    fun deleteMember(m_seq: String){
        val single = memberService.deleteMember(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun deleteFollowMember(m_seq: String){
        val single = memberService.deleteFollowMember(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun insertMemberProfile(email: String, gender: String, age: String, context: Context){
        val single = memberService.insertMemberProfile(email,gender,age)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val intent = Intent(context, MainActivity::class.java)
                context.applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }, {
                Log.d("실패함 Insert Member Profile", "" + it.message)
            })
    }

    fun getMemberSeq(email: String,password: String,context: Context){
        val single = memberService.getMemberSeq(email,password)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val getMseq = context.getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
                val m_seq = it.m_seq
                val age = it.age
                val nick_name = it.nick_name
                val gender = it.gender
                val getSeq : SharedPreferences.Editor = getMseq.edit()
                getSeq.putString("inputMseq", m_seq);
                getSeq.putString("inputAge", age);
                getSeq.putString("inputNickName",nick_name);
                getSeq.putString("inputGender", gender);
                //꼭 commit()을 해줘야 값이 저장됩니다 ㅎㅎ
                getSeq.commit();

            },{
                Log.d("실패 Get Member", "" + it.message)
            })
    }

    fun getMember(m_seq: String){
        val single = memberService.getMember(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                memberLiveData.postValue(it)
            },{

            })

    }

    fun getUserSearch(nick_name:String,offset:Int,limit:Int){
        val single = memberService.getUserSearch(nick_name,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                memberListLiveData.postValue(it)
            },{

            })

    }
    fun getSubsribers(m_seq:String,offset:Int,limit:Int){
              val single = memberService.getSubsribers(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                memberListLiveData.postValue(it)
            },{

            })
    }
    fun getSubscribing(m_seq:String,offset:Int,limit:Int){
       val single = memberService.getSubscribing(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                memberListLiveData.postValue(it)
            },{

            })
    }

    fun getNotification(target_m_seq:String,offset: Int,limit: Int){
        val single = memberService.getNotification(target_m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                memberListNotiLiveData.postValue(it)
            },{

            })
    }

    fun editProfileImg(m_seq: String, profileImg: String) {
        val single = memberService.editMemberProfile(m_seq,profileImg)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun editProfileBackgroundImg(m_seq: String, profileBackgroundImg: String) {
        val single = memberService.editMemberProfileBackground(m_seq,profileBackgroundImg)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun getMemberCheck(email: String, password: String, context: Context){

        val single = memberService.getMemberCheck(email, password)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                performLogin(email,password,context) // 파이어베이스 로그인
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
                    context.applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                } else {
                    Toast.makeText(context.applicationContext, "email 또는 password를 확인해주세요.", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(context, LoginActivity::class.java)
//                    context.applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                  }
            }, {
                Log.d("실패함 Insert Member", "" + it.message)
            })
    }
    fun memberSubscribe(target_m_seq:String,m_seq:String,type:String,count: TextView) {
        val single = memberService.memberSubscribe(target_m_seq,m_seq,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(count.text == "구독하기" || count.text == "구독취소"){

                }else {
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
                Log.d("실패함 구독", "" + it.message)
            })
    }

    fun memberSubscribe2(target_m_seq:String,m_seq:String,type:String) {
        val single = memberService.memberSubscribe(target_m_seq,m_seq,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(type.equals("true")) {
                }
            }, {
                Log.d("실패함 구독", "" + it.message)

            })
    }
        fun getMemberNickName(m_seq:String,nick_name: TextView) {
        val single = memberService.getMemberNickName(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                nick_name.text = it.toString()
            }, {
            })
    }

    fun memberSubscribeChecked(target_m_seq:String, m_seq:String, type:String, checkBox: CheckBox, context: Context) {
        val single = memberService.memberSubscribe(target_m_seq,m_seq,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.toString() == "true"){
                    checkBox.isChecked = true
                    checkBox.text = "구독취소"
                }else{
                    checkBox.isChecked = false
                    checkBox.text = "구독하기"
                }
            }, {
            })
    }

    fun memberSubscribeChecked2(target_m_seq:String, m_seq:String, type:String, checkBox: CheckBox, context: Context) {
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

    fun memberMySubscribeCount(m_seq: String,count: TextView) {
        val single = memberService.memberMySubscribeCount(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                count.text = it
            }, {
            })
    }

    fun memberUserSubscribeCount(m_seq: String,count: TextView) {
        val single = memberService.memberUserSubscribeCount(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                count.text = it
            }, {
            })
    }

    fun getMemberProfileImgFromNickName(nick_name: String, profile: ImageView, context: Context) {
        val single = memberService.getMemberProfileImgFromNickName(nick_name)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.toString() == ""){
                    Glide.with(context)
                        .load("http://www.storymaw.com/data/member/empty_user.png")
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }else{
                    Glide.with(context)
                        .load(it.toString())
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }
            }, {
                Log.e("실패함", "" + it.message)

            })
    }

    fun getMemberProfileImgFromNickNameDot(nick_name: String, profile: RedDotImageView2, context: Context) {
        val single = memberService.getMemberProfileImgFromNickName(nick_name)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.toString() == ""){
                    Glide.with(context)
                        .load("http://www.storymaw.com/data/member/empty_user.png")
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }else{
                    Log.e("이미지가져옴 ", "" + it.toString())
                    Glide.with(context)
                        .load(it.toString())
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }


            }, {
                Log.e("실패함", "" + it.message)

            })
    }

    fun getProfileImg(m_seq: String,profile: ImageView,context: Context) {
        val single = memberService.getMemberProfileImgFromMseq(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.toString() == "" || it.toString() == null){
                    Glide.with(context)
                        .load("http://www.storymaw.com/data/member/empty_user.png")
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }else{
                    Glide.with(context)
                        .load(it.toString())
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }


            }, {
                Log.d("getProfileImg failed", "" + it.message)

            })

    }

    fun getProfileImgDot(m_seq: String, profile: RedDotImageView2, context: Context) {
        val single = memberService.getMemberProfileImgFromMseq(m_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if(it.toString() == "" || it.toString() == null){
                    Glide.with(context)
                        .load("http://www.storymaw.com/data/member/empty_user.png")
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }else{
                    Log.e("이미지가져옴 ", "" + it.toString())
                    Glide.with(context)
                        .load(it.toString())
                        .apply(RequestOptions().circleCrop())
                        .placeholder(android.R.color.transparent)
                        .into(profile)
                }



            }, {
                Log.d("실패함", "" + it.message)

            })

    }

    fun updateProfile(m_seq: String,memo:String,gender:String,age:String) {
        val single = memberService.updateProfile(m_seq,memo,gender,age)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    fun addNotifiaction(m_seq: String,target_m_seq: String,noti_content_seq: Int,noti_type: String, noti_message: String) {
        val single = memberService.addNotification(m_seq,target_m_seq,noti_content_seq,noti_type,noti_message)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    fun editNotification(noti_seq: Int) {
        val single = memberService.editNotification(noti_seq)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun getNotificationCount(target_m_seq: String, alarm: ImageView, alarmDot: RedDotImageView, context: Context) {
        alarmDot.setOnClickListener {
            val lintent = Intent(context, AlarmActivity::class.java)
            (context as Activity).startActivity(lintent)
        }
        alarm.setOnClickListener {
            val lintent = Intent(context, AlarmActivity::class.java)
            (context as Activity).startActivity(lintent)
        }
        val single = memberService.getNotification(target_m_seq,0,100)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                for(noti in it){
                    if(noti.toString().contains("null") || noti.toString().isEmpty()){
                        Log.e("noti not null",""+noti.toString())
                        alarm.visibility = View.GONE
                        alarmDot.visibility = View.VISIBLE
                    }
                }
            }, {
                Log.e("실패함", "" + it.message)

            })
    }

        private fun performRegister(email: String,password: String,nick_name: String,context: Context) {

        Log.d(SearchTitleFragment.TAG, "Attempting to create user with email: $email")

        val user_email = email
        val user_password = password
        val nickname = nick_name

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user_email, user_password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                Log.d("가입성공", "가입성공?: ${it.result!!.user!!.uid}")
                saveUserToFirebaseDatabase(nickname)


            }
            .addOnFailureListener{
                Log.d(SearchTitleFragment.TAG, "가입실패?: ${it.message}")
            }
    }


    private fun performLogin(email: String,password: String,context: Context) {

        val user_email = email
        val user_password = password

        if (user_email.isEmpty() || user_password.isEmpty()) {
            Toast.makeText(context, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")
            }
            .addOnFailureListener {
                Log.d(SearchTitleFragment.TAG, "파이어베이스 실패: ${it.message}")
            }
    }
    private fun saveUserToFirebaseDatabase(username: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = FB_User(uid, username)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(SearchTitleFragment.TAG, "파이어베이스 유저 저장")

            }
            .addOnFailureListener {
                Log.d(SearchTitleFragment.TAG, "파이어베이스 유저 저장실패: ${it.message}")
            }
    }

}