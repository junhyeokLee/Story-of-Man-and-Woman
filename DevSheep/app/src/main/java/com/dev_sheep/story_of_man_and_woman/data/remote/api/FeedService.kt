package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import io.reactivex.Single
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import java.sql.Date

interface FeedService {

    @FormUrlEncoded
    @POST("add_feed.php")
    fun insertFeed(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("tag_seq") tag_seq: Int,
        @Field("creater") creater: String,
        @Field("type") type: String
    ): Call<Void>


//    @FormUrlEncoded
//    @GET("get_feed.php")
//    fun getStart(): Call<List<Feed>>


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
