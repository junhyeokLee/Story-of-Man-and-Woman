package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Member
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Notification
import com.dev_sheep.story_of_man_and_woman.utils.BaseDiffUtil
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.AlarmActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MessageActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import kotlinx.android.synthetic.main.adapter_alarm.view.*
import kotlinx.android.synthetic.main.adapter_alarm.view.img_profile
import kotlinx.android.synthetic.main.adapter_feed.view.*
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notiList: MutableList<Notification>,
                          private val context: Context,
                          private var memberViewModel: MemberViewModel,
                          private val onClickSubscribeListener: NotificationAdapter.OnClickSubscribeListener,
                          private val onClickFeedViewListener: NotificationAdapter.OnClickFeedViewListener,
                          private val onEndlessScrollListener: NotificationAdapter.OnEndlessScrollListener

): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mcontext: Context
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_ITEM = 1
    private var isLoadingAdded = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        val view: View?

//                = LayoutInflater.from(context).inflate(R.layout.adapter_alarm, parent, false)

        return when (viewType){
            VIEW_TYPE_LOADING -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.progress_loading, parent, false)
                LoadingViewHolder(view, isLoadingAdded)
            }
            VIEW_TYPE_ITEM ->{

                view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_alarm,parent,false)
                ViewHolder(view,isLoadingAdded,onClickSubscribeListener,onClickFeedViewListener,onEndlessScrollListener)
            }
            else -> throw RuntimeException("알 수 없는 뷰타입")
        }

//        ViewHolder(view,onClickSubscribeListener,onClickFeedViewListener)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            VIEW_TYPE_LOADING ->{
                val viewHolder: LoadingViewHolder = holder as LoadingViewHolder
                viewHolder.bindView()
            }
            VIEW_TYPE_ITEM -> {
                val viewHolder: ViewHolder = holder as ViewHolder
                val item = notiList[position]
                viewHolder.bindView(item,context,memberViewModel)
            }
        }
    }


    class ViewHolder(itemView: View,
                     isLoadingAdded: Boolean,
                     onClickSubscribeListener: NotificationAdapter.OnClickSubscribeListener,
                     onClickFeedViewListener: OnClickFeedViewListener,
                     onEndlessScrollListener: OnEndlessScrollListener
    ) : RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private val onClickSubscribe= onClickSubscribeListener
        private val onClickFeedView = onClickFeedViewListener
        private val onEndlessScrollListener = onEndlessScrollListener
        private var isLoadingAdded = isLoadingAdded


        fun bindView(item: Notification, context: Context,memberViewModel:MemberViewModel) {


            // 마지막 피드 인지 아닌지 체크
            if(item.last_index.equals("true")){
                isLoadingAdded = false
                onEndlessScrollListener.OnEndless(true)
            }else if(item.last_index.equals("false")) {
                isLoadingAdded = true
                onEndlessScrollListener.OnEndless(false)
            }

            // 저장된 m_seq 가져오기
            val getM_seq = context.getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
            my_m_seq = getM_seq.getString("inputMseq", null)


            memberViewModel.getMemberNickName(item.m_seq!!,itemView.tv_nickname)
            itemView.setAnimation(AnimationUtils.loadAnimation(itemView.context, R.anim.anim_recyclerview));

            itemView.noti_type.text = item.noti_type
            itemView.noti_content.text = item.noti_message
            itemView.tv_noti_date.text = calculateTime(sdf.parse(item.noti_datetime))

            if(item.noti_read_datetime == null || item.noti_read_datetime == "") {
                itemView.img_profile_dot.visibility = View.VISIBLE
                itemView.img_profile.visibility = View.GONE
                memberViewModel.getProfileImgDot(item.m_seq!!, itemView.img_profile_dot, itemView.context)
            }else{
                itemView.img_profile_dot.visibility = View.GONE
                itemView.img_profile.visibility = View.VISIBLE
                memberViewModel.getProfileImg(item.m_seq!!, itemView.img_profile, itemView.context)
            }


            with(itemView.layout_noti){
                setOnClickListener {
                    if(item.noti_type.equals("구독알림")){
                        onClickSubscribe.OnClickSubscribe(item.m_seq!!)
                        memberViewModel.editNotification(item.noti_seq)
                    }
                    else if(item.noti_type.equals("피드알림")){
                        onClickFeedView.OnClickFeedView(item.noti_content_seq!!)
                        memberViewModel.editNotification(item.noti_seq)
                    }
                    else if(item.noti_type.equals("댓글알림")){
                        onClickFeedView.OnClickFeedView(item.noti_content_seq!!)
                        memberViewModel.editNotification(item.noti_seq)
                    }
                    else if(item.noti_type.equals("대댓글 알림")){
                        onClickFeedView.OnClickFeedView(item.noti_content_seq!!)
                        memberViewModel.editNotification(item.noti_seq)
                    }
                }
            }


        }

        private object TIME_MAXIMUM {
            const val SEC = 60
            const val MIN = 60
            const val HOUR = 24
            const val DAY = 30
            const val MONTH = 12
        }

        fun calculateTime(date: Date): String? {
            val curTime = System.currentTimeMillis()
            val regTime = date.time
            var diffTime = (curTime - regTime) / 1000
            var msg: String? = null
            if (diffTime < TIME_MAXIMUM.SEC) {
                // sec
                msg = diffTime.toString() + " 초전"
            } else if (TIME_MAXIMUM.SEC.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MIN) {
                // min
                println(diffTime)
                msg = diffTime.toString() + " 분전"
            } else if (TIME_MAXIMUM.MIN.let { diffTime /= it; diffTime } < TIME_MAXIMUM.HOUR) {
                // hour
                msg = diffTime.toString() + " 시간전"
            } else if (TIME_MAXIMUM.HOUR.let { diffTime /= it; diffTime } < TIME_MAXIMUM.DAY) {
                // day
                msg = diffTime.toString() + " 일전"
            } else if (TIME_MAXIMUM.DAY.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MONTH) {
                // day
                msg = diffTime.toString() + " 달전"
            } else {
                msg = diffTime.toString() + " 년전"
            }
            return msg
        }


    }

    internal class LoadingViewHolder(itemView: View,isLoadingAdded:Boolean):RecyclerView.ViewHolder(itemView){
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private var isLoading = isLoadingAdded
        fun bindView() {
            if(isLoading == true){
                progressBar.visibility = View.VISIBLE
            }else{
                progressBar.visibility = View.GONE
            }
        }

    }

    fun updateList(notis: MutableList<Notification>) {
        // diif util 리사이클러뷰 재활용 능력 향상시켜줌 깜빡임 현상없어짐
        val diffUtil = BaseDiffUtil(notis, this.notiList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        this.notiList.clear()
        this.notiList.addAll(notis)
        diffResult.dispatchUpdatesTo(this)

    }


    override fun getItemCount(): Int {

        return if(notiList == null) 0
        else notiList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == notiList.size) VIEW_TYPE_LOADING
        else{
            VIEW_TYPE_ITEM
        }
    }

    interface OnClickSubscribeListener{
        fun OnClickSubscribe(m_seq:String)
    }
    interface OnClickFeedViewListener{
        fun OnClickFeedView(feed_seq:Int)
    }
    interface OnEndlessScrollListener{
        fun OnEndless(boolean_value: Boolean)
    }


}