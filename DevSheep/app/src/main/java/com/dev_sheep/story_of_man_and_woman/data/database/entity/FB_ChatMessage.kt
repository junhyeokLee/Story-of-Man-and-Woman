package com.dev_sheep.story_of_man_and_woman.data.database.entity

class FB_ChatMessage(
  val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long,val readUsers: Any
) {
  constructor() : this("", "", "", "", -1,false)
}