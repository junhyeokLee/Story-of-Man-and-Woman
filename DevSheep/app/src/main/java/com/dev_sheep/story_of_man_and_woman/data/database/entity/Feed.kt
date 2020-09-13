package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dev_sheep.story_of_man_and_woman.utils.ListStringConverter
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "Feed")
data class Feed( @PrimaryKey
                 @NonNull
                 @SerializedName("feed_seq")
                 var feed_seq: Int,
                 @SerializedName("title")
                 var title: String? = null,
                 @SerializedName("content")
                 var content: String? = null,
                 @SerializedName("tag_name")
                 var tag_name: String? = null,
                 @SerializedName("tag_seq")
                 var tag_seq: String? = null,
                 @SerializedName("creater")
                 var creater: String? = null,
                 @SerializedName("creater_seq")
                 var creater_seq: String? = null,
                 @SerializedName("creater_image_url")
                 var creater_image_url: String? = null,
                 @SerializedName("creater_age")
                 var creater_age : String? = null,
                 @SerializedName("creater_gender")
                 var creater_gender: String? = null,
                 @SerializedName("view_no")
                 var view_no: Int? = null,
                 @SerializedName("like_no")
                 var like_no: Int? = null,
                 @SerializedName("comment_seq")
                 var comment_seq: Int? = null,
                 @SerializedName("feed_date")
                 var feed_date: String? = null,
                 @SerializedName("feed_rank")
                 var feed_rank: String? = null,
                 @SerializedName("favorited")
                 var favorited: Boolean = false,
                 @SerializedName("type")
                 var type: String? = null,
                 @SerializedName("bookmark_check")
                 var bookmark_check: String? = null)

