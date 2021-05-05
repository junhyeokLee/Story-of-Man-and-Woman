package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.*
import io.reactivex.Completable
import io.reactivex.Observable
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
        @Field("nick_name") nick_name: String
    ): Single<String>

    @FormUrlEncoded
    @POST("member_profile_add.php")
    fun insertMemberProfile(
        @Field("email") email: String,
        @Field("gender") gender: String,
        @Field("age") age: String
    ): Single<String>


    @FormUrlEncoded
    @POST("member_get_m_seq.php")
    fun getMemberSeq(@Field("email") email: String, @Field("password") password: String): Single<Member>

    @FormUrlEncoded
    @POST("delete_member.php")
    fun deleteMember(@Field("m_seq") m_seq:String) : Completable

    @FormUrlEncoded
    @POST("delete_follow_member.php")
    fun deleteFollowMember(@Field("m_seq") m_seq:String) : Completable

    @FormUrlEncoded
    @POST("login_check.php")
    fun getMemberCheck(@Field("email") email: String, @Field("password") password: String): Single<String>

    @FormUrlEncoded
    @POST("member_get.php")
    fun getMember(@Field("m_seq") m_seq: String): Single<Member>

    @GET("user_get_search.php")
    fun getUserSearch(@Query("nick_name") nick_name: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<MutableList<Member>>

    //사용자가 프로필 이미지를 변경했을때 해당 이미지를 서버로 전송하는 통신
    @Multipart
    @POST("member_profile_upload.php")
    fun uploadProfile(@Part("email") nick_name: String, @Part File: MultipartBody.Part?): Call<Member>

    @GET("member_get_nickname.php")
    fun getMemberNickName(@Query("m_seq") m_seq: String): Single<String>

    @FormUrlEncoded
    @POST("member_edit_profile.php")
    fun editMemberProfile(@Field("m_seq") m_seq:String,@Field("profile_img") profile_img:String ): Completable

    @FormUrlEncoded
    @POST("member_edit_profilebackground.php")
    fun editMemberProfileBackground(@Field("m_seq") m_seq:String,@Field("background_img") background_img:String ): Completable

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
    fun getSubsribers(@Field("m_seq") m_seq: String,@Field("offset") offset: Int,@Field("limit") limit: Int): Single<MutableList<Member>>

    @FormUrlEncoded
    @POST("subscribing_get.php")
    fun getSubscribing(@Field("m_seq") m_seq: String,@Field("offset") offset: Int,@Field("limit") limit: Int): Single<MutableList<Member>>


    @GET("get_member_profile_img_from_nickname.php")
    fun getMemberProfileImgFromNickName(@Query("nick_name") nick_name: String): Single<String>


    @GET("get_member_profile_img_from_m_seq.php")
    fun getMemberProfileImgFromMseq(@Query("m_seq") m_seq: String): Single<String>

    @FormUrlEncoded
    @POST("update_profile.php")
    fun updateProfile(@Field("m_seq") m_seq: String, @Field("memo") memo: String, @Field("gender") gender:String, @Field("age") age: String):Completable

    @FormUrlEncoded
    @POST("password_search_send.php")
    fun passwordSearchSend(@Field("m_seq") m_seq: String): Single<Void>

    @FormUrlEncoded
    @POST("notification_add.php")
    fun addNotification(@Field("m_seq") m_seq: String,@Field("target_m_seq") target_m_seq: String
    ,@Field("noti_content_seq") noti_content_seq: Int,@Field("noti_type") noti_type: String
    ,@Field("noti_message") noti_message: String): Completable

    @GET("notification_get.php")
    fun getNotification(@Query("target_m_seq") target_m_seq: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Observable<MutableList<Notification>>


    @FormUrlEncoded
    @POST("notification_edit.php")
    fun editNotification(@Field("noti_seq") noti_seq: Int): Completable
}