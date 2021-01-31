package com.dev_sheep.story_of_man_and_woman.data.database.entity

import com.xwray.groupie.Group

class FB_ChatMessage(
  val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long,val readUsers: Any,
  val username : String,val date : String
) {
  constructor() : this("", "", "", "", -1,false,"","")
}