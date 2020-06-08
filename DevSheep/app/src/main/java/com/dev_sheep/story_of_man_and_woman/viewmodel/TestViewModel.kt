package com.dev_sheep.story_of_man_and_woman.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dev_sheep.story_of_man_and_woman.data.Test
import com.dev_sheep.story_of_man_and_woman.database.TestDAO
import com.dev_sheep.story_of_man_and_woman.service.TestService
import kotlin.concurrent.thread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestViewModel(private val testDAO: TestDAO, private val testService: TestService) : ViewModel() {

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

}
