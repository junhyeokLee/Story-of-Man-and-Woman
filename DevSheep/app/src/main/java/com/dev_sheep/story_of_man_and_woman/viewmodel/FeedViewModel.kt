package com.dev_sheep.story_of_man_and_woman.viewmodel

import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dev_sheep.story_of_man_and_woman.data.database.dao.SearchDAO
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Search
import com.dev_sheep.story_of_man_and_woman.data.remote.api.FeedService
import com.dev_sheep.story_of_man_and_woman.repository.FeedRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class FeedViewModel(private val searchDAO: SearchDAO, private val feedService: FeedService) :  ViewModel() {


    private val disposable = CompositeDisposable()

    private val feedRepository: FeedRepository = FeedRepository(feedService)


    // Feed LiveData
    var listOfFeeds = feedRepository.feedListLiveData
    // Feed CommentList LiveData
    var listCommentOfFeed = feedRepository.feedCommentListLiveData
    // Feed Comment item
    var commentOfFeed = feedRepository.feedCommentLiveData
    // Tag LiveData
    var listTagOfFeed = feedRepository.feedTagListLiveData
    // Feed Today LiveData
    var listOfTodayFeeds = feedRepository.feedTodayListLiveData


    init {
        listOfFeeds.value = mutableListOf()
        listCommentOfFeed.value = mutableListOf()
        listTagOfFeed.value = mutableListOf()
        listOfTodayFeeds.value = mutableListOf()
    }

    // 피드 리스트
    fun getInitFeedList(offset:Int,limit:Int){
        feedRepository.getInitFeedList(offset,limit)
    }
    // 피드 리스트
    fun getTodayFeedList(){
        feedRepository.getTodayFeedList()
    }
    // 피드 댓글 리스트 (CommentAdapterFeed)
    fun getComment(feed_seq: Int,offset: Int,limit:Int){
        feedRepository.getComment(feed_seq,offset,limit)
    }
    // 피드 댓글 리스트 (CommentAdapter)
    fun getComment2(feed_seq: Int,offset: Int,limit:Int){
        feedRepository.getComment2(feed_seq,offset,limit)
    }
    // 구독자 이야기 리스트
    fun getListNotificationSubscribe(m_seq:String,offset:Int,limit:Int){
        feedRepository.getListNotificationSubscribe(m_seq,offset,limit)
    }
    // 북마크 리스트
    fun getBookMark(m_seq:String,offset:Int,limit:Int){
        feedRepository.getBookMark(m_seq,offset,limit)
    }
    // 마이 피드 리스트
    fun getListMystory(m_seq:String, offset:Int, limit:Int){
        feedRepository.getListMystory(m_seq,offset,limit)
    }
    // 비밀글 피드 리스트
    fun getListSecert(m_seq:String,offset:Int,limit:Int){
        feedRepository.getListSecert(m_seq,offset,limit)
    }
    // 프로필 구독자 이야기 리스트
    fun getListSubscribe(m_seq:String,offset:Int,limit:Int){
        feedRepository.getListSubscribe(m_seq,offset,limit)
    }
    // 회원 프로필 구독자이야기 리스트
    fun getListUserSubscribe(get_m_seq:String,m_seq:String,offset:Int,limit:Int){
        feedRepository.getListUserSubscribe(get_m_seq,m_seq,offset,limit)
    }

    // 피드 대댓글 리스트
    fun getReComment(feed_seq:Int,group_seq:Int,offset: Int,limit: Int){
        feedRepository.getReComment(feed_seq,group_seq,offset,limit)
    }

    // Search 피드 리스트
    fun getFeedSearch(title:String,offset: Int,limit: Int){
        feedRepository.getFeedSearch(title,offset,limit)
    }

    // 댓글 정보
    fun getCommentItem(comment_seq:Int){
        feedRepository.getCommentItem(comment_seq)
    }

    // 오늘 피드
    fun getTodayList(offset: Int,limit: Int){
        feedRepository.getTodayList(offset,limit)
    }

    // 태그서치
    fun getTagSearch(tag_seq:Int,offset:Int,limit:Int){
        feedRepository.getTagSearch(tag_seq,offset,limit)
    }

    // Tag 리스트
    fun getTagList(){
        feedRepository.getTagList()
    }

    // 피드추가
    fun insertFeed(title:String,content:String,tag_seq:Int,creater:String,type:String){
        feedRepository.insertFeed(title,content,tag_seq,creater,type)
    }
    // 피드 수정
    fun updateFeed(feed_seq: Int,title:String,content:String,type:String){
        feedRepository.updateFeed(feed_seq,title,content,type)
    }
    // 피드 삭제
    fun deleteFeed(feed_seq: Int){
        feedRepository.deleteFeed(feed_seq)
    }
    // 계정삭제시 해당 회원 피드 삭제
    fun deleteFeedMember(m_seq: String){
        feedRepository.deleteFeedMember(m_seq)
    }
    // 계정삭제시 코멘트 전체 삭제
    fun deleteFeedMemberComment(m_seq: String){
        feedRepository.deleteFeedMemberComment(m_seq)
    }
    fun deleteFeedMemberBookMark(m_seq: String){
        feedRepository.deleteFeedMemberBookMark(m_seq)
    }
    fun deleteFeedMemberNotification(m_seq: String){
        feedRepository.deleteFeedMemberNotification(m_seq)
    }
    // 뷰 카운트 증가
    fun increaseViewCount(feed_seq: Int){
        feedRepository.increaseViewCount(feed_seq)
    }
    // 좋아요 카운트 증가
    fun increaseLikeCount(feed_seq:Int,boolean_value: String) {
        feedRepository.increaseLikeCount(feed_seq,boolean_value)
    }
    // 댓글 좋아요 카운트 증가
    fun increaseLikeCommentCount(comment_seq: Int,boolean_value: String){
        feedRepository.increaseLikeCommentCount(comment_seq,boolean_value)
    }
    // 피드 신고 카운트 증가
    fun increaseComplain(feed_seq:Int){
        feedRepository.increaseComplain(feed_seq)
    }
    // 북마크 추가
    fun onClickBookMark(m_seq:String,feed_seq: Int,boolean_value: String){
        feedRepository.onClickBookMark(m_seq,feed_seq,boolean_value)
    }
    // 댓글 추가
    fun addComment(writer: String,feed_seq: Int,comment:String){
        feedRepository.addComment(writer,feed_seq,comment)
    }
    // 대댓글 추가
    fun addReComment(comment_seq: Int,feed_seq: Int,writer_seq: String,group_seq:Int,depth:Int,comment:String){
        feedRepository.addReComment(comment_seq,feed_seq,writer_seq,group_seq,depth,comment)
    }


    // Room database
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

    override fun onCleared() {
        super.onCleared()
        disposable.clear()

    }

}
