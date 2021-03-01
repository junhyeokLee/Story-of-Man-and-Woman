package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_ChatMessage
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.activity.MessageActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MyMessageActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.adapter_latest_message_row.view.*
import java.text.SimpleDateFormat
import java.util.*


class MessageLatestAdapter(val FBChatMessage: FB_ChatMessage, val memberViewModel:MemberViewModel,val context: Context): Item<ViewHolder>() {
  var chatPartnerUser: FB_User? = null
  var fromId : String? = null
  var toId : String? = null
  var sdf : SimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
  val myId = FirebaseAuth.getInstance().uid
  val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$myId")

  override fun bind(viewHolder: ViewHolder, position: Int) {

    viewHolder.itemView.layout_item.setOnClickListener {
      val intent = Intent(viewHolder.itemView.context, MessageActivity::class.java)
      intent.putExtra(ProfileUsersFragment.USER_ID, chatPartnerUser)

      (viewHolder.itemView.context as Activity).startActivity(intent)
      (viewHolder.itemView.context as Activity).overridePendingTransition(
        R.anim.fragment_fade_in,
        R.anim.fragment_fade_out
      )

    }

    val chatPartnerId: String
    if (FBChatMessage.fromId == FirebaseAuth.getInstance().uid) {
      chatPartnerId = FBChatMessage.toId
      toId = FBChatMessage.toId
    } else {
      chatPartnerId = FBChatMessage.fromId
      fromId = FBChatMessage.fromId
    }



    viewHolder.itemView.tv_chat_id.text = chatPartnerId
    viewHolder.itemView.message_textview_latest_message.text = FBChatMessage.text
    viewHolder.itemView.tv_noti_date.text = calculateTime(sdf.parse(FBChatMessage.date))



    viewHolder.itemView.layout_item.setOnLongClickListener(object : View.OnLongClickListener{
      override fun onLongClick(v: View?): Boolean {

        showDeletePopup(position,fromId!!,chatPartnerId)

        return true
      }

    })


    // 읽음 안읽음 표시
    if(FBChatMessage.readUsers == false) {
      viewHolder.itemView.imageview_latest_message_dot.visibility = View.VISIBLE
      viewHolder.itemView.imageview_latest_message.visibility = View.INVISIBLE
    }else{
      viewHolder.itemView.imageview_latest_message_dot.visibility = View.INVISIBLE
      viewHolder.itemView.imageview_latest_message.visibility = View.VISIBLE
    }

    val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(p0: DataSnapshot) {
        chatPartnerUser = p0.getValue(FB_User::class.java)
        viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

        memberViewModel.getMemberProfileImgFromNickName(chatPartnerUser?.username!!,viewHolder.itemView.imageview_latest_message,viewHolder.itemView.context)
        memberViewModel.getMemberProfileImgFromNickNameDot(chatPartnerUser?.username!!,viewHolder.itemView.imageview_latest_message_dot,viewHolder.itemView.context)

      }
      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }

  // 롱클릭시에 내메세지 없으면 안지워짐

  override fun getLayout(): Int {
    return R.layout.adapter_latest_message_row
  }

  private fun showDeletePopup(
    position: Int,
    fromId: String,
    toId: String
  ){
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val view = inflater.inflate(R.layout.alert_popup,null)
    val textView: TextView = view.findViewById(R.id.textView)
    val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
    val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

    textView.text = "대화방에서 나가시겠습니까?"

    val alertDialog = AlertDialog.Builder(context)
      .setTitle("대화방 삭제")
      .setPositiveButton("네"){
          dialog,which -> Toast.makeText(context,"삭제하기", Toast.LENGTH_SHORT).show()
        latestMessageRef.removeValue()
        reference.removeValue()
        MyMessageActivity.adapter.removeGroup(position) // group library 스와이프 아이템 삭제
        MyMessageActivity.adapter.notifyDataSetChanged()


      }
      .setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, which ->
        MyMessageActivity.adapter.notifyDataSetChanged()
      })
      .create()

    // remaining_time - 00:00~~10:00 ,  elapsed - 10:00 ~~ 00:00
    // p1PlayTime = 00:00~ 시작, elapsed

    alertDialog.setView(view)
    alertDialog.show()



    val btn_color : Button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
    val btn_color_cancel : Button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

    if(btn_color != null){
      btn_color.setTextColor(context.resources.getColor(R.color.main_Accent))
    }
    if(btn_color_cancel != null){
      btn_color_cancel.setTextColor(context.resources.getColor(R.color.main_Accent))

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