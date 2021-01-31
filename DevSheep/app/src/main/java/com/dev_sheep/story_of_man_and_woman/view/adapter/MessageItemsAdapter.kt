package com.dev_sheep.story_of_man_and_woman.view.adapter

import android.util.Log
import com.dev_sheep.story_of_man_and_woman.R
import com.dev_sheep.story_of_man_and_woman.data.database.entity.FB_User
import com.dev_sheep.story_of_man_and_woman.viewmodel.MemberViewModel
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.adapter_from_message.view.*
import kotlinx.android.synthetic.main.adapter_latest_message_row.view.*
import kotlinx.android.synthetic.main.adapter_to_message.view.*


class MessageFromItem(val text: String, val user: FB_User,val memberViewModel: MemberViewModel): Item<ViewHolder>() {
  override fun bind(viewHolder: ViewHolder, position: Int) {
    viewHolder.itemView.toMessage.text = text
    viewHolder.itemView.toUsername.text = user.username

    memberViewModel.getMemberProfileImgFromNickName(user.username,viewHolder.itemView.profile_img_to,viewHolder.itemView.context)
  }

  override fun getLayout(): Int {
    return R.layout.adapter_to_message
  }
}

class MessageToItem(val text: String,val user: FB_User,val memberViewModel: MemberViewModel): Item<ViewHolder>() {
  override fun bind(viewHolder: ViewHolder, position: Int) {

    viewHolder.itemView.fromMessage.text = text
    viewHolder.itemView.fromUsername.text = user.username
    memberViewModel.getMemberProfileImgFromNickName(user.username,viewHolder.itemView.profile_img_from,viewHolder.itemView.context)
    // load our user image into the star

  }

  override fun getLayout(): Int {
    return R.layout.adapter_from_message
  }
}