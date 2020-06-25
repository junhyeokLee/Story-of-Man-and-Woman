package com.dev_sheep.story_of_man_and_woman.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev_sheep.story_of_man_and_woman.data.database.dao.TestDAO
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.remote.api.TestService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


class TestViewModel(private val testDAO: TestDAO, private val testService: TestService) :  ViewModel() {

    init {
        initNetworkRequest()
    }

    private fun initNetworkRequest() {
        val call = testService.get()
        call.enqueue(object : Callback<List<Test>?> {
            override fun onResponse(
                call: Call<List<Test>?>?,
                response: Response<List<Test>?>?
            ) {
                response?.body()?.let { tests: List<Test> ->
                    thread {
                        testDAO.add(tests)
                    }
                }
            }

            override fun onFailure(call: Call<List<Test>?>?, t: Throwable?) {
                // TODO handle failure
            }
        })
    }

    fun getListPokemon(): LiveData<List<Test>> {

        return testDAO.all()
    }
    fun getListFirst(limit: Int,offset: Int): LiveData<List<Test>>{
        return testDAO.allList(limit,offset)
    }
    fun getListMore(limit: Int,offset: Int): LiveData<List<Test>>{
        return testDAO.allList(limit,offset)
    }

}
