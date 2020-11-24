package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_ChatMessage
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment
import com.dev_sheep.story_of_man_and_woman.view.Fragment.ProfileUsersFragment.Companion.USER_ID
import com.dev_sheep.story_of_man_and_woman.view.activity.FeedRankActivity
import com.dev_sheep.story_of_man_and_woman.view.activity.MessageActivity
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.adapter_latest_message_row.view.*


class MessageLatestAdapter(val FBChatMessage: FB_ChatMessage, val memberViewModel:MemberViewModel): Item<ViewHolder>() {
  var chatPartnerUser: FB_User? = null

  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.itemView.message_textview_latest_message.text = FBChatMessage.text

    Log.e("읽음안읽음",FBChatMessage.readUsers.toString())
    if(FBChatMessage.readUsers == false) {
      viewHolder.itemView.state_message.text = "안읽음"
    }else{
      viewHolder.itemView.state_message.text = "읽음"
    }
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
    } else {
      chatPartnerId = FBChatMessage.fromId
    }

    val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {
      override fun onDataChange(p0: DataSnapshot) {
        chatPartnerUser = p0.getValue(FB_User::class.java)
        viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

        memberViewModel.getMemberProfileImgFromNickName(chatPartnerUser?.username!!,viewHolder.itemView.imageview_latest_message,viewHolder.itemView.context)

        val targetImageView = viewHolder.itemView.imageview_latest_message
//        Picasso.get().load(chatPartnerUser?.profile_img).into(targetImageView)
      }

      override fun onCancelled(p0: DatabaseError) {

      }
    })
  }

  override fun getLayout(): Int {
    return R.layout.adapter_latest_message_row
  }
}