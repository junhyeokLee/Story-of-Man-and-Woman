package com.dev_sheep.story_of_man_and_woman.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object APIService {
     val retrofit = Retrofit.Builder()
        .baseUrl("https://gist.githubusercontent.com/mrcsxsiq/b94dbe9ab67147b642baa9109ce16e44/raw/97811a5df2df7304b5bc4fbb9ee018a0339b8a38/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val testService: TestService = retrofit.create()
}
