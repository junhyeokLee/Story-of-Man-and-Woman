package com.dev_sheep.story_of_man_and_woman.data.remote.api

import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface TestService {
    @GET("pokemon.json")
    fun get(): Call<List<Test>>


    @GET("pokemon.json")
    fun getList(): Single<List<Test>>

}
