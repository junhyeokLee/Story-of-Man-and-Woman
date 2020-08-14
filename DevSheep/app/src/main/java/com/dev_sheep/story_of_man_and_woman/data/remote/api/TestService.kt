package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface TestService {
    @GET("pokemon.json")
    fun get(): Call<List<Test>>


    @FormUrlEncoded
    @POST("add_feed.php")
    fun insertFeed(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("tag_seq") tag_seq: Int,
        @Field("creater") creater: String,
        @Field("type") type: String
    ): Single<Void>


    @GET("get_feed.php")
    fun getList(): Single<List<Feed>>


    @GET("get_tag.php")
    fun getTagList(): Single<List<Tag>>

    @FormUrlEncoded
    @POST("get_feed_item.php")
    fun getFeed(@Field("feed_seq") feed_seq: Int): Single<Feed>


    //사용자가 프로필 이미지를 변경했을때 해당 이미지를 서버로 전송하는 통신
    @Multipart
    @POST("upload_content.php")
    fun uploadImage(@Part File: MultipartBody.Part?): Call<Feed>

//    @FormUrlEncoded
//    @POST("update_feed.php")
//    fun updateFeed():Call<Test>

//      @FormUrlEncoded
//      @POST("delete_feed.php")
//      fun deleteFeed():Call<Test>

//    @FormUrlEncoded
//    @POST("search.php")
//    fun search(
//        @Field("action") action: String?,
//        @Field("query") query: String?,
//        @Field("start") start: String?,
//        @Field("limit") limit: String?
//    ): Call<Test?>?
}
