package com.dev_sheep.story_of_man_and_woman.data.database.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class FB_User(val uid: String, val username:  String): Parcelable {
  constructor() : this("", "")
}