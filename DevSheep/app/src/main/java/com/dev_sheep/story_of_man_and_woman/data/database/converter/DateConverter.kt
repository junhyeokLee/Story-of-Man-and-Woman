package com.leopold.mvvm.data.db.converter

import androidx.room.TypeConverter
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Leopold
 */
class DateConverter {
    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @TypeConverter
    fun toLong(date: Date): Long = date.time


    @TypeConverter
    fun fromList(list : ArrayList<String>): String{

        var gson:Gson = Gson()
        var json : String = gson.toJson(list)

        return json
    }

}