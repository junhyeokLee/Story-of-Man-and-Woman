package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "BookMark")
data class BookMark( @PrimaryKey
                     @NonNull
                     @SerializedName("bm_seq")
                     var bm_seq: Int,
                     @SerializedName("m_seq")
                     var m_seq: String? = null,
                     @SerializedName("feed_seq")
                     var feed_seq: Int? = null,
                     @SerializedName("date")
                     var date: String? = null,
                     @SerializedName("last_index")
                     var last_index: String? = null)
