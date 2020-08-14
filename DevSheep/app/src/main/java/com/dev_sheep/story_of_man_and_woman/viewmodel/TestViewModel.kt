package com.dev_sheep.story_of_man_and_woman.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev_sheep.story_of_man_and_woman.data.database.dao.TestDAO
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.remote.api.TestService
import com.dev_sheep.story_of_man_and_woman.view.Fragment.HomeFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.MystoryActivity
import com.dev_sheep.story_of_man_and_woman.view.adapter.FeedAdapter
import com.dev_sheep.story_of_man_and_woman.view.adapter.Test_Searchtag_Adapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread


class TestViewModel(private val testDAO: TestDAO, private val testService: TestService) :  ViewModel() {

//    init {
//        initNetworkRequest()
//    }

//    private fun initNetworkRequest() {
//        val call = testService.getList()
//        call.enqueue(object : Callback<List<Feed>?> {
//            override fun onResponse(
//                call: Call<List<Feed>?>?,
//                response: Response<List<Feed>?>?
//            ) {
//                response?.body()?.let { tests: List<Feed> ->
//                    thread {
//                        testDAO.addAllList(tests)
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<Feed>?>, t: Throwable) {
//            }
//        })
//
//
//    }



    fun getListPokemon(): LiveData<List<Test>> {
        return testDAO.all()
    }
    fun getListFirst(limit: Int,offset: Int): LiveData<List<Test>>{
        return testDAO.allList(limit,offset)
    }
    fun getListMore(limit: Int,offset: Int): LiveData<List<Test>>{
        return testDAO.allList(limit,offset)
    }

    fun getListFeed(): LiveData<List<Feed>>{
        return testDAO.getallList()
    }

    fun insertFeed(title:String,content:String,tag_seq:Int,creater:String,type:String){
        // use rxjava
//       val single = testService.insertFeed(title,content,tag_seq,creater,type)
//
//        single.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                // handle sucess
//                Log.e("Retrofit 성공 ", ""+it.toString());
//            },{
//                // handle fail
//                Log.e("Retrofit 통신 ERROR: ", it.message);
//            })
        // use call
        val single = testService.insertFeed(title,content,tag_seq,creater,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
            })
    }

//    fun getFeed(){
//        val call = testService.getList()
//        call.enqueue(object : Callback<List<Feed>?>{
//            override fun onFailure(call: Call<List<Feed>?>, t: Throwable) {
//              Log.e("errors",t.message.toString())
//            }
//
//            override fun onResponse(call: Call<List<Feed>?>, response: Response<List<Feed>?>) {
//                response?.body()?.let { tests: List<Feed> ->
//                    thread {
//                        Log.e("getFeed성공",tests.toString())
//                        Log.e("feed 0 title value = ",tests.get(0).title)
//                    }
//                }
//            }
//
//        })
//    }

    fun getTag(){
        val single = testService.getList()
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }
                ,{

                })
    }

}
