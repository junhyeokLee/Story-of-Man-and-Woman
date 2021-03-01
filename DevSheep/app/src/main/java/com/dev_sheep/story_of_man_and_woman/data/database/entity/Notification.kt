package com.dev_sheep.story_of_man_and_woman.data.database.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "notification")
data class Notification(@PrimaryKey
                    @NonNull
                    @SerializedName("noti_seq")
                    var noti_seq: Int,
                        @SerializedName("m_seq")
                    var m_seq: String? = null,
                        @SerializedName("target_m_seq")
                    var target_m_seq: String? = null,
                        @SerializedName("noti_content_seq")
                    var noti_content_seq: Int? = null,
                        @SerializedName("noti_type")
                    var noti_type: String? = null,
                        @SerializedName("noti_message")
                    var noti_message: String? = null,
                        @SerializedName("noti_datetime")
                    var noti_datetime: String? = null,
                        @SerializedName("noti_read_datetime")
                    var noti_read_datetime: String? = null,
                        @SerializedName("last_index")
                        var last_index: String? = null
                    )