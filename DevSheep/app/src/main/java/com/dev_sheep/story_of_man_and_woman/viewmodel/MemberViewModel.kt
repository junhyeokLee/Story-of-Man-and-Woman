package com.dev_sheep.story_of_man_and_woman.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Notification
import com.dev_sheep.story_of_man_and_woman.data.remote.api.MemberService
import com.dev_sheep.story_of_man_and_woman.repository.FeedRepository
import com.dev_sheep.story_of_man_and_woman.repository.MemberRepository
import com.dev_sheep.story_of_man_and_woman.utils.RedDotImageView
import com.dev_sheep.story_of_man_and_woman.utils.RedDotImageView2
import com.dev_sheep.story_of_man_and_woman.view.Fragment.SearchTitleFragment.Companion.TAG
import com.dev_sheep.story_of_man_and_woman.view.activity.AlarmActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MainActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.SignUpStartActivity
import com.google.firebase.analytics.FirebaseAnalytics.Event.SEARCH
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.http.Field


class MemberViewModel(private val memberService: MemberService) :  ViewModel(){

    private val disposable = CompositeDisposable()

    private val memberRepository: MemberRepository = MemberRepository(memberService)



    // Member LiveData
    var memberLivedata = memberRepository.memberLiveData
    // Member LiveData List
    var memberListLivedata = memberRepository.memberListLiveData
    // Member Notification List
    var memberListNotiLiveData = memberRepository.memberListNotiLiveData
    init {
        memberLivedata.value = Member()
        memberListLivedata.value = mutableListOf()
        memberListNotiLiveData.value = mutableListOf()
    }

    // userSearch 리스트
    fun getUserSearch(nick_name:String,offset:Int,limit:Int){
        memberRepository.getUserSearch(nick_name,offset,limit)
    }
    // 구독자 리스트
    fun getSubsribers(m_seq:String,offset:Int,limit:Int){
        memberRepository.getSubsribers(m_seq,offset,limit)
    }
    // 구독중 리스트
    fun getSubscribing(m_seq:String,offset:Int,limit:Int){
        memberRepository.getSubscribing(m_seq,offset,limit)
    }

    // 알람리스트
    fun getNotification(target_m_seq:String,offset: Int,limit: Int){
        memberRepository.getNotification(target_m_seq,offset,limit)
    }

    // 회원가입
    fun insertMember(email: String, password: String, nick_name: String, context: Context){
        memberRepository.insertMember(email,password,nick_name,context)
    }
    // 프로필록 등록
    fun insertMemberProfile(email: String, gender: String, age: String, context: Context){
        memberRepository.insertMemberProfile(email,gender,age,context)
    }
    // 회원삭제
    fun deleteMember(m_seq: String){
        memberRepository.deleteMember(m_seq)
    }

    // 회원가져오기
    fun getMemberSeq(email: String,password: String,context: Context){
        memberRepository.getMemberSeq(email,password,context)
    }
    // 회원 정보 가져오기
    fun getMember(m_seq: String){
        memberRepository.getMember(m_seq)
    }
    // 프로필 이미지수정
    fun editProfileImg(m_seq: String, profileImg: String){
        memberRepository.editProfileImg(m_seq,profileImg)
    }
    // 프로필 배경이미지
    fun editProfileBackgroundImg(m_seq: String, profileBackgroundImg: String){
        memberRepository.editProfileBackgroundImg(m_seq,profileBackgroundImg)
    }
    // 회원 체크
    fun getMemberCheck(email: String, password: String, context: Context){
        memberRepository.getMemberCheck(email,password,context)
    }
    // 회원 구독
    fun memberSubscribe(target_m_seq:String,m_seq:String,type:String,count: TextView){
        memberRepository.memberSubscribe(target_m_seq,m_seq,type,count)
    }
    //회원 닉네임
    fun getMemberNickName(m_seq:String,nick_name: TextView){
        memberRepository.getMemberNickName(m_seq,nick_name)
    }
    // 회원구독2
    fun memberSubscribe2(target_m_seq:String,m_seq:String,type:String){
        memberRepository.memberSubscribe2(target_m_seq,m_seq,type)
    }
    // 회원 구독 체크
    fun memberSubscribeChecked(target_m_seq:String, m_seq:String, type:String, checkBox: CheckBox,context: Context){
        memberRepository.memberSubscribeChecked(target_m_seq,m_seq,type,checkBox,context)
    }
    // 회원 구독 체크2
    fun memberSubscribeChecked2(target_m_seq:String, m_seq:String, type:String, checkBox: CheckBox,context: Context){
        memberRepository.memberSubscribeChecked2(target_m_seq,m_seq,type,checkBox,context)
    }
    // 나의 구독자수
    fun memberMySubscribeCount(m_seq: String,count: TextView){
        memberRepository.memberMySubscribeCount(m_seq,count)
    }
    // 회원 구독자수
    fun memberUserSubscribeCount(m_seq: String,count: TextView){
        memberRepository.memberUserSubscribeCount(m_seq,count)
    }
    // 파이어베이스 채팅 닉네임으로 프로필 사진 가져오기
    fun getMemberProfileImgFromNickName(nick_name: String,profile: ImageView,context: Context) {
        memberRepository.getMemberProfileImgFromNickName(nick_name,profile,context)
    }
    // 파이어베이스 채팅 읽은지 안읽은지 dot 체크
    fun getMemberProfileImgFromNickNameDot(nick_name: String, profile: RedDotImageView2, context: Context) {
        memberRepository.getMemberProfileImgFromNickNameDot(nick_name,profile,context)
    }
    // 프로필 이미지 가져오기
    fun getProfileImg(m_seq: String,profile: ImageView,context: Context){
        memberRepository.getProfileImg(m_seq,profile,context)
    }
    // 알림 읽은게 있는지 없는지 dot 체크
    fun getProfileImgDot(m_seq: String, profile: RedDotImageView2, context: Context){
        memberRepository.getProfileImgDot(m_seq,profile,context)
    }
    // 프로필 수정
    fun updateProfile(m_seq: String,memo:String,gender:String,age:String){
        memberRepository.updateProfile(m_seq,memo,gender,age)
    }
    // 알림추가
    fun addNotifiaction(m_seq: String,target_m_seq: String,noti_content_seq: Int,noti_type: String, noti_message: String){
        memberRepository.addNotifiaction(m_seq,target_m_seq,noti_content_seq,noti_type,noti_message)
    }
    // 알림수정
    fun editNotification(noti_seq: Int){
        memberRepository.editNotification(noti_seq)
    }
    // 알림 카운트
    @SuppressLint("CheckResult")
    fun getNotificationCount(target_m_seq: String, alarm: ImageView, alarmDot: RedDotImageView,context: Context){
        memberRepository.getNotificationCount(target_m_seq,alarm,alarmDot,context)
    }

}