package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FollowMember
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface MemberService {

    // Member
    @FormUrlEncoded
    @POST("member_add.php")
    fun insertMember(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("nick_name") nick_name: String,
        @Field("gender") gender: String,
        @Field("age") age: String
    ): Single<String>

    @FormUrlEncoded
    @POST("member_get_m_seq.php")
    fun getMemberSeq(@Field("email") email: String, @Field("password") password: String): Single<Member>

    @FormUrlEncoded
    @POST("login_check.php")
    fun getMemberCheck(@Field("email") email: String, @Field("password") password: String): Single<String>

    @FormUrlEncoded
    @POST("member_get.php")
    fun getMember(@Field("m_seq") m_seq: String): Single<Member>

    //사용자가 프로필 이미지를 변경했을때 해당 이미지를 서버로 전송하는 통신
    @Multipart
    @POST("member_profile_upload.php")
    fun uploadProfile(@Part("email") nick_name: String, @Part File: MultipartBody.Part?): Call<Member>

    @FormUrlEncoded
    @POST("member_edit_profile.php")
    fun editMemberProfile(@Field("m_seq") m_seq:String,@Field("profile_img") profile_img:String ): Single<Member>

    @FormUrlEncoded
    @POST("member_edit_profilebackground.php")
    fun editMemberProfileBackground(@Field("m_seq") m_seq:String,@Field("background_img") background_img:String ): Single<Member>

    @FormUrlEncoded
    @POST("follow_member_request.php")
    fun memberSubscribe(@Field("target_m_seq") target_m_seq:String,@Field("m_seq") m_seq:String,@Field("type") type: String ): Single<String>

    @FormUrlEncoded
    @POST("follow_member_count.php")
    fun memberMySubscribeCount(@Field("m_seq") m_seq:String): Single<String>

    @FormUrlEncoded
    @POST("follow_member_user_count.php")
    fun memberUserSubscribeCount(@Field("m_seq") m_seq:String): Single<String>

    @FormUrlEncoded
    @POST("subscribers_get.php")
    fun getSubsribers(@Field("m_seq") m_seq: String): Single<List<Member>>

    @FormUrlEncoded
    @POST("subscribing_get.php")
    fun getSubscribing(@Field("m_seq") m_seq: String): Single<List<Member>>

}