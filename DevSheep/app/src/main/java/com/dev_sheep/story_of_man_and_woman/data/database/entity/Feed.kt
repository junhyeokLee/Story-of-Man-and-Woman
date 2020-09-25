package com.dev_sheep.story_of_man_and_woman.data.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.ArrayList

@Entity(tableName = "Feed")
data class Feed(
    @PrimaryKey
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
    var creater_age: String? = null,
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
    var bookmark_check: String? = null,
    @SerializedName("images")
    var images: ArrayList<ItemImage>
) : Parcelable{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt() == 1,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readValue(java.util.ArrayList::class.java.classLoader) as java.util.ArrayList<ItemImage>

    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {

        dest.writeInt(feed_seq)
        dest.writeString(title);
        dest.writeValue(images)

    }


    override fun toString(): String {
        return "feed{"+
                "feed_seq="+feed_seq+
                ", Images"+ images+
                '}'
    }

    companion object CREATOR : Parcelable.Creator<Feed> {
        override fun createFromParcel(parcel: Parcel): Feed {
            return Feed(parcel) }
        override fun newArray(size: Int): Array<Feed?> {
            return arrayOfNulls(size)
        }
    }


}


