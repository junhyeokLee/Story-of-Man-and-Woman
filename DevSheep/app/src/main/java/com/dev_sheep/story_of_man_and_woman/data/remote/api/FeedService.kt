package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.*
import com.google.gson.JsonObject
import io.reactivex.Observable
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

    @FormUrlEncoded
    @POST("feed_update.php")
    fun updateFeed(
        @Field("feed_seq") feed_seq: Int,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("type") type: String
    ): Single<Void>

    @FormUrlEncoded
    @POST("feed_delete.php")
    fun deleteFeed(
        @Field("feed_seq") feed_seq: Int
    ): Single<Void>


    @GET("feed_get_all.php")
    fun getList(): Observable<List<Feed>>

    @GET("feed_get_scroll.php")
    fun getListScroll(@Query("offset") offset: Int,@Query("limit") limit: Int): Observable<List<Feed>>

    @GET("feed_get_today_recommend.php")
    fun getTodayList(@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_best.php")
    fun getBestList(): Observable<List<Feed>>

    @GET("feed_get_week.php")
    fun getWeekList(): Observable<List<Feed>>

    @GET("feed_get_man_age_recommend.php")
    fun getAgeManRecommendList(@Query("age") age: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_woman_age_recommend.php")
    fun getAgeWomanRecommendList(@Query("age") age: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_view_recommend.php")
    fun getViewRecommendList(@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_like_recommend.php")
    fun getLikeRecommendList(@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_tag_search.php")
    fun getTagSearch(@Query("tag_seq") tag_seq: Int): Single<List<Feed>>

    @GET("feed_get_search_card.php")
    fun getSearchCard(@Query("tag_seq") tag_seq: Int): Single<List<Feed>>

    @GET("feed_get_mystory.php")
    fun getListMystory(@Query("m_seq") m_seq: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_search.php")
    fun getFeedSearch(@Query("title") title: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_secret.php")
    fun getListSecert(@Query("m_seq") m_seq: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_subscribe.php")
    fun getListSubscribe(@Query("m_seq") m_seq: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @GET("feed_get_notification_subscribe.php")
    fun getListNotificationSubscribe(@Query("m_seq") m_seq: String): Single<List<Feed>>

    @GET("feed_get_user_subscribe.php")
    fun getListUserSubscribe(@Query("m_seq") m_seq: String,@Query("my_seq") my_seq: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    @FormUrlEncoded
    @POST("feed_edit_view_count.php")
    fun edit_feed_view_count(@Field("feed_seq") feed_seq: Int): Single<Feed>

    @FormUrlEncoded
    @POST("feed_edit_like_count.php")
    fun edit_feed_like_count(@Field("feed_seq") feed_seq: Int,@Field("boolean_value") boolean_value : String): Single<Feed>

    @FormUrlEncoded
    @POST("comment_edit_like_count.php")
    fun edit_comment_like_count(@Field("comment_seq") comment_seq: Int,@Field("boolean_value") boolean_value : String): Single<Comment>

    @FormUrlEncoded
    @POST("feed_item_get.php")
    fun getFeed(@Field("feed_seq") feed_seq: Int): Single<Feed>

    @FormUrlEncoded
    @POST("feed_complain.php")
    fun increase_Complain(@Field("feed_seq") feed_seq: Int): Single<Feed>

    //사용자가 프로필 이미지를 변경했을때 해당 이미지를 서버로 전송하는 통신
    @Multipart
    @POST("content_upload.php")
    fun uploadImage(@Part("email") email:String,@Part File: MultipartBody.Part?): Call<Feed>

    //book_mark 저장하기
    @FormUrlEncoded
    @POST("book_mark_add.php")
    fun onClickBookMark(@Field("m_seq") m_seq: String, @Field("feed_seq") feed_seq: Int,@Field("boolean_value") boolean_value: String): Single<BookMark>

    @FormUrlEncoded
    @POST("book_mark_get.php")
    fun getBookMark(@Field("m_seq") m_seq: String,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Feed>>

    //comment 등록
    @FormUrlEncoded
    @POST("comment_add.php")
    fun addComment(@Field("writer") writer: String, @Field("feed_seq") feed_seq: Int,@Field("comment") comment: String): Single<Void>

    @FormUrlEncoded
    @POST("recomment_add.php")
    fun addReComment(@Field("comment_seq") TaskEntrycomment_seq: Int, @Field("feed_seq") feed_seq: Int,@Field("writer_seq") writer_seq: String, @Field("group_seq") group_seq: Int,
                     @Field("depth") depth: Int,@Field("comment") comment: String): Single<Void>

    @GET("recomment_get.php")
    fun getReComment(@Query("feed_seq") feed_seq: Int,@Query("group_seq") group_seq: Int,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Comment>>


    @GET("comment_get.php")
    fun getComment(@Query("feed_seq") feed_seq: Int,@Query("offset") offset: Int,@Query("limit") limit: Int): Single<List<Comment>>

    @GET("comment_item_get.php")
    fun getCommentItem(@Query("comment_seq") comment_seq: Int): Single<Comment>

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
