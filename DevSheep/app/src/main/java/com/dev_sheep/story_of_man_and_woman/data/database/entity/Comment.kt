package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Comments")
data class Comment( @PrimaryKey
                    @NonNull
                    @SerializedName("comment_seq")
                    var comment_seq: Int,
                    @SerializedName("feed_seq")
                    var feed_seq: String? = null,
                    @SerializedName("writer")
                    var writer: String? = null,
                    @SerializedName("writer_seq")
                    var writer_seq: String? = null,
                    @SerializedName("like_no")
                    var like_no: Int? = null,
                    @SerializedName("comment")
                    var comment: String? = null,
                    @SerializedName("comment_date")
                    var comment_date: String? = null,
                    @SerializedName("group_seq")
                    var group_seq: Int? = null,
                    @SerializedName("parent_comment_seq")
                    var parent_comment_seq: Int? = null,
                    @SerializedName("depth")
                    var depth: Int? = null,
                    @SerializedName("order_no")
                    var order_no: Int? = null,
                    @SerializedName("writer_img")
                    var writer_img:String? = null,
                    @SerializedName("writer_gender")
                    var writer_gender:String? = null,
                    @SerializedName("writer_age")
                    var writer_age:String? = null,
                    @SerializedName("last_index")
                    var last_index: String? = null
                    )