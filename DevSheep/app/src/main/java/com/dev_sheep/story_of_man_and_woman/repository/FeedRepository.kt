package com.dev_sheep.story_of_man_and_woman.repository

import android.nfc.tech.MifareUltralight
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dev_sheep.story_of_man_and_woman.data.database.dao.SearchDAO
import com.dev_sheep.story_of_man_and_woman.data.database.entity.BookMark
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Comment
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Tag
import com.dev_sheep.story_of_man_and_woman.data.remote.APIService
import com.dev_sheep.story_of_man_and_woman.data.remote.api.FeedService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class FeedRepository(private val feedService: FeedService) {

val feedListLiveData = MutableLiveData<MutableList<Feed>>()
val feedTodayListLiveData = MutableLiveData<MutableList<Feed>>()
val feedCommentListLiveData = MutableLiveData<MutableList<Comment>>()
val feedCommentLiveData = MutableLiveData<Comment>()
val feedTagListLiveData = MutableLiveData<MutableList<Tag>>()

    // List function
    fun getInitFeedList(offset: Int, limit: Int){
        val single = feedService.getListScroll(offset, limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }
    fun getTodayFeedList(){
        val single = feedService.getTodayFeedList()
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedTodayListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }
    fun getComment(feed_seq: Int,offset: Int,limit:Int){
        val single = feedService.getComment(feed_seq,offset, limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run { if (it.size > 0) {
                    feedCommentListLiveData.postValue(it)
                 }
                }
            },
                {
                    Log.d("error",it.message)
                })
    }
    fun getComment2(feed_seq: Int,offset: Int,limit:Int){
        val single = feedService.getComment2(feed_seq,offset, limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run { if (it.size > 0) {
                    feedCommentListLiveData.postValue(it)
                }
                }
            },
                {
                    Log.d("error",it.message)
                })
    }

    fun getCommentItem(comment_seq:Int){
        val single = feedService.getCommentItem(comment_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                feedCommentLiveData.postValue(it)
            },
                {
                    Log.d("error",it.message)
                })
    }

    fun getTodayList(offset: Int,limit: Int){
        val single = feedService.getTodayList(offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
            })
    }

    fun getTagSearch(tag_seq:Int,offset:Int,limit:Int){
        val single = feedService.getTagSearch(tag_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                feedListLiveData.postValue(it)
            },{
            })
    }

    fun getListNotificationSubscribe(m_seq:String,offset:Int,limit:Int){
        val single = feedService.getListNotificationSubscribe(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }
    fun getBookMark(m_seq:String,offset:Int,limit:Int){
        val single = feedService.getBookMark(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }
    fun getFeedSearch(title:String,offset: Int,limit: Int){
        val single = feedService.getFeedSearch(title,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }
    fun getListMystory(m_seq:String,offset:Int,limit:Int){
        val single = feedService.getListMystory(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }
    fun getListSecert(m_seq:String,offset:Int,limit:Int){
        val single = feedService.getListMystory(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }

    fun getListSubscribe(m_seq:String,offset:Int,limit:Int) {
        val single = feedService.getListSubscribe(m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }

    fun getListUserSubscribe(get_m_seq:String,m_seq:String,offset:Int,limit:Int) {
        val single = feedService.getListUserSubscribe(get_m_seq,m_seq,offset,limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (it.size > 0) {
                        feedListLiveData.postValue(it)
                    }
                }
            }, {
                Log.d("error",it.message)
            })
    }

    fun getReComment(feed_seq:Int,group_seq:Int,offset: Int,limit: Int){
        val single = feedService.getReComment(feed_seq,group_seq,offset, limit)
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run { if (it.size > 0) {
                    feedCommentListLiveData.postValue(it)
                }
                }
            },
                {
                    Log.d("error",it.message)
                })
    }

    fun getTagList(){
        val single = feedService.getTagList()
        single.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run { if (it.size > 0) {
                    feedTagListLiveData.postValue(it)
                }
                }
            },
                {
                    Log.d("error",it.message)
                })
    }


        // List function end


    fun insertFeed(title: String, content: String, tag_seq: Int, creater: String, type: String) {
        val single = feedService.insertFeed(title, content, tag_seq, creater, type)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun updateFeed(feed_seq: Int,title:String,content:String,type:String) {
        val single = feedService.updateFeed(feed_seq, title, content, type)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun deleteFeed(feed_seq:Int){
        val single = feedService.deleteFeed(feed_seq)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun increaseViewCount(feed_seq:Int){
        val single = feedService.edit_feed_view_count(feed_seq)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    fun increaseLikeCount(feed_seq:Int,boolean_value: String){
        val single = feedService.edit_feed_like_count(feed_seq,boolean_value)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    fun increaseLikeCommentCount(comment_seq: Int,boolean_value: String) {
        val single = feedService.edit_comment_like_count(comment_seq,boolean_value)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun increaseComplain(feed_seq:Int){
        val single = feedService.increase_Complain(feed_seq)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun onClickBookMark(m_seq:String,feed_seq: Int,boolean_value: String){
        val single = feedService.onClickBookMark(m_seq,feed_seq,boolean_value)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    fun addComment(writer: String,feed_seq: Int,comment:String){
        val single = feedService.addComment(writer,feed_seq,comment)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    fun addReComment(comment_seq: Int,feed_seq: Int,writer_seq: String,group_seq:Int,depth:Int,comment:String) {
        val single = feedService.addReComment(comment_seq,feed_seq,writer_seq,group_seq,depth,comment)
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

}