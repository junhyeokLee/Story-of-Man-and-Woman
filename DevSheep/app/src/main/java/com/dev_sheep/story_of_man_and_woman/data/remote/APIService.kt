package com.dev_sheep.story_of_man_and_woman.data.remote

import com.dev_sheep.story_of_man_and_woman.data.remote.api.FeedService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

object APIService {

    val gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://www.storymaw.com/")
        .addConverterFactory(ScalarsConverterFactory.create()) //important
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    val FEED_SERVICE: FeedService = retrofit.create()
}
