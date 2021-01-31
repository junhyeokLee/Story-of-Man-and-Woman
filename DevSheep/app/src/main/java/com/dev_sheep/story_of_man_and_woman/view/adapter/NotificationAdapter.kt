package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Notification
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.AlarmActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MessageActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.FeedViewModel
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import kotlinx.android.synthetic.main.adapter_alarm.view.*
import kotlinx.android.synthetic.main.adapter_alarm.view.img_profile
import kotlinx.android.synthetic.main.adapter_feed.view.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(private val notiList: List<Notification>,
                          private val context: Context,
                          private var memberViewModel: MemberViewModel,
                          private val onClickSubscribeListener: NotificationAdapter.OnClickSubscribeListener,
                          private val onClickFeedViewListener: NotificationAdapter.OnClickFeedViewListener
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View,
                     onClickSubscribeListener: NotificationAdapter.OnClickSubscribeListener,
                     onClickFeedViewListener: OnClickFeedViewListener) : RecyclerView.ViewHolder(itemView) {
        lateinit var my_m_seq : String
        private var sdf : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private val onClickSubscribe= onClickSubscribeListener
        private val onClickFeedView = onClickFeedViewListener

        fun bindView(item: Notification, context: Context,memberViewModel:MemberViewModel) {
            // 저장된 m_seq 가져오기
            val getM_seq = context.getSharedPreferences("m_seq", AppCompatActivity.MODE_PRIVATE)
            my_m_seq = getM_seq.getString("inputMseq", null)


            memberViewModel.getMemberNickName(item.m_seq!!,itemView.tv_nickname)

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
                }
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_alarm, parent, false)

        return ViewHolder(view,onClickSubscribeListener,onClickFeedViewListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notiList[position]
        holder.bindView(item,context,memberViewModel)


    }

    override fun getItemCount(): Int {

        return notiList.size
    }


    interface OnClickSubscribeListener{
        fun OnClickSubscribe(m_seq:String)
    }
    interface OnClickFeedViewListener{
        fun OnClickFeedView(feed_seq:Int)
    }
}