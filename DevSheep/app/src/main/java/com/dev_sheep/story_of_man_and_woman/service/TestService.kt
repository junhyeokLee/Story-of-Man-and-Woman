package com.dev_sheep.story_of_man_and_woman.service

import com.dev_sheep.story_of_man_and_woman.data.Test
import retrofit2.Call
import retrofit2.http.GET

interface TestService {
    @GET("pokemon.json")
    fun get(): Call<List<Test>>
}
