package com.dev_sheep.story_of_man_and_woman.viewmodel

import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dev_sheep.story_of_man_and_woman.data.database.dao.SearchDAO
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Search
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.remote.api.FeedService
import com.lumyjuwon.richwysiwygeditor.RichEditor.RichEditor
import com.lumyjuwon.richwysiwygeditor.RichWysiwyg
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Field


class FeedViewModel(private val searchDAO: SearchDAO, private val feedService: FeedService) :  ViewModel() {

//    init {
//        initNetworkRequest()
//    }


    fun deleteSearchAll(){
        return searchDAO.deleteAllSearch()
    }
    fun deleteSearchId(id:Int){
        return searchDAO.deleteSearch(id)
    }
    fun addSearch(search_title: Search){
        return searchDAO.addSearch(search_title)
    }
    fun getSearchList(): LiveData<List<Search>> {
        return searchDAO.getSearchList()
    }

    private val disposable = CompositeDisposable()



    fun insertFeed(title:String,content:String,tag_seq:Int,creater:String,type:String){
        // use call
        val single = feedService.insertFeed(title,content,tag_seq,creater,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
            })
    }
    fun updateFeed(feed_seq: Int,title:String,content:String,type:String){
        val single = feedService.updateFeed(feed_seq,title,content,type)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
            })
    }

    fun deleteFeed(feed_seq: Int){
        val single = feedService.deleteFeed(feed_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
            })

    }
    fun increaseViewCount(feed_seq:Int){
        val single = feedService.edit_feed_view_count(feed_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            })
    }
    fun increaseLikeCount(feed_seq:Int,boolean_value: String){
        val single = feedService.edit_feed_like_count(feed_seq,boolean_value)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            })
    }

    fun increaseLikeCommentCount(comment_seq: Int,boolean_value: String){
        val single = feedService.edit_comment_like_count(comment_seq,boolean_value)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            })
    }

    fun increaseComplain(feed_seq:Int){
        val single = feedService.increase_Complain(feed_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            })
    }


    fun getTag(){
        val single = feedService.getList()
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            }
                ,{

                })
    }
    fun onClickBookMark(m_seq:String,feed_seq: Int,boolean_value: String){
        val single = feedService.onClickBookMark(m_seq,feed_seq,boolean_value)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("BookMark m_seq = ",m_seq)

            },{

            })
    }
    fun onCheckedBookMark(m_seq: String,feed_seq: Int,bookmarkButton:CheckBox){
        val single = feedService.checkedBookMark(m_seq,feed_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("BookMark Checked = ",it)
                if(it.equals("checked")){
                    bookmarkButton.isChecked = true
                }else{
                    bookmarkButton.isChecked = false
                }
            },{

            })
    }

    fun addComment(writer: String,feed_seq: Int,comment:String){
        val single = feedService.addComment(writer,feed_seq,comment)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("add comment m_seq = ",writer)

            },{
                Log.e("add comment 실패 = ",it.message.toString())

            })
    }
    fun addReComment(comment_seq: Int,feed_seq: Int,writer_seq: String,group_seq:Int,depth:Int,comment:String){
        val single = feedService.addReComment(comment_seq,feed_seq,writer_seq,group_seq,depth,comment)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("add Recomment m_seq = ",writer_seq)

            },{
                Log.e("add Recomment 실패 = ",it.message.toString())

            })
    }

    fun getFeed(feed_seq: Int){
        val single = feedService.getFeed(feed_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d("get Feed = ",it.toString())

            },{
                Log.d("get Feed 실패 = ",it.message.toString())

            })
    }

    fun getFeedTitle(feed_seq: Int,title:String){
        val single = feedService.getFeedTitle(feed_seq)
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
               it.toString()
            },{
                Log.d("get Feed Title Failed = ",it.message.toString())

            })
    }

    override fun onCleared() {
        disposable.clear()
    }
}
