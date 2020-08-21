package com.dev_sheep.story_of_man_and_woman.di

import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.api.TestService
import com.google.gson.GsonBuilder
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create


val networkModule = module {

//    val gson = GsonBuilder()
//        .setLenient()
//        .create()
//    // single = 앱이 살아있는 동안 전역적으로 사용가능한 객체를 생성
//    single<Retrofit> {
//        Retrofit.Builder()
//            .baseUrl("https://gist.githubusercontent.com/mrcsxsiq/b94dbe9ab67147b642baa9109ce16e44/raw/97811a5df2df7304b5bc4fbb9ee018a0339b8a38/")
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }
//
//    single {
//        get<Retrofit>().create<TestService>()
//    }

    val gson = GsonBuilder()
        .setLenient()
        .create()
    // single = 앱이 살아있는 동안 전역적으로 사용가능한 객체를 생성
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("http://www.storymaw.com/")
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create<TestService>()
    }
}
