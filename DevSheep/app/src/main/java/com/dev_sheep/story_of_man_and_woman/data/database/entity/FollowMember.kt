package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "FollowMember")
data class FollowMember( @PrimaryKey
                   @NonNull
                   @SerializedName("fm_seq")
                   var fm_seq: String,
                   @SerializedName("m_seq")
                   var m_seq: String? = null,
                   @SerializedName("target_m_seq")
                   var target_m_seq: String? = null,
                   @SerializedName("w_date")
                   var w_date: String? = null,
                   @SerializedName("status")
                   var status: String? = null,
                     @SerializedName("members")
                     var members: List<Member>? = null)
