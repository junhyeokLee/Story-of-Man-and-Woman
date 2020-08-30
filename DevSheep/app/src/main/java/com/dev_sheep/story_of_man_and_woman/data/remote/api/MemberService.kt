package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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


}