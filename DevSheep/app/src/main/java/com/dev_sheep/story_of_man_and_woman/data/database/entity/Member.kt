package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Member")
data class Member( @PrimaryKey
                   @NonNull
                   @SerializedName("m_seq")
                   var m_seq: String,
                   @SerializedName("email")
                   var email: String? = null,
                   @SerializedName("password")
                   var password: String? = null,
                   @SerializedName("nick_name")
                   var nick_name: String? = null,
                   @SerializedName("profile_img")
                   var profile_img: String? = null,
                   @SerializedName("background_img")
                   var background_img: String? = null,
                   @SerializedName("gender")
                   var gender : String? = null,
                   @SerializedName("age")
                   var age: String? = null,
                   @SerializedName("wdate")
                   var wdate: String? = null,
                   @SerializedName("memo")
                   var memo: String? = null)