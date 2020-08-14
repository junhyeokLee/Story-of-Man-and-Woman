package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tag")
data class Tag( @PrimaryKey
                 @NonNull
                 var tag_seq: Int,
                 var tag_name: String? = null)
