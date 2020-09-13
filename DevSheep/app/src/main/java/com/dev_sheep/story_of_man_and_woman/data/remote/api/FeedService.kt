package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.*
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface FeedService {

    // Tag
    @GET("tag_get.php")
    fun getTagList(): Single<List<Tag>>

    // Feed
    @GET("pokemon.json")
    fun get(): Call<List<Test>>

    @FormUrlEncoded
    @POST("feed_add.php")
    fun insertFeed(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("tag_seq") tag_seq: Int,
        @Field("creater") creater: String,
        @Field("type") type: String
    ): Single<Void>


    @GET("feed_get_all.php")
    fun getList(): Single<List<Feed>>

    @GET("feed_get_mystory.php")
    fun getListMystory(@Query("m_seq") m_seq: String): Single<List<Feed>>

    @GET("feed_get_secret.php")
    fun getListSecert(@Query("m_seq") m_seq: String): Single<List<Feed>>

    @GET("feed_get_subscribe.php")
    fun getListSubscribe(@Query("m_seq") m_seq: String): Single<List<Feed>>

    @FormUrlEncoded
    @POST("feed_edit_view_count.php")
    fun edit_feed_view_count(@Field("feed_seq") feed_seq: Int): Single<Feed>

    @FormUrlEncoded
    @POST("feed_edit_like_count.php")
    fun edit_feed_like_count(@Field("feed_seq") feed_seq: Int,@Field("boolean_value") boolean_value : String): Single<Feed>

    @FormUrlEncoded
    @POST("feed_item_get.php")
    fun getFeed(@Field("feed_seq") feed_seq: Int): Single<Feed>

    //사용자가 프로필 이미지를 변경했을때 해당 이미지를 서버로 전송하는 통신
    @Multipart
    @POST("content_upload.php")
    fun uploadImage(@Part File: MultipartBody.Part?): Call<Feed>

    //book_mark 저장하기
    @FormUrlEncoded
    @POST("book_mark_add.php")
    fun onClickBookMark(@Field("m_seq") m_seq: String, @Field("feed_seq") feed_seq: Int,@Field("boolean_value") boolean_value: String): Single<BookMark>

    @FormUrlEncoded
    @POST("book_mark_get.php")
    fun getBookMark(@Field("m_seq") m_seq: String): Single<List<Feed>>

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
