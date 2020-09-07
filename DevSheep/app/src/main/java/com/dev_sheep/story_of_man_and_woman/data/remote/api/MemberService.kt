package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
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
    ): Single<Boolean>

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
    fun uploadProfile(@Part("nick_name") nick_name: String, @Part File: MultipartBody.Part?): Call<Member>

    @FormUrlEncoded
    @POST("member_edit_profile.php")
    fun editMemberProfile(@Field("m_seq") m_seq:String,@Field("profile_img") profile_img:String ): Single<Member>
}