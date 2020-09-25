package com.dev_sheep.story_of_man_and_woman.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dev_sheep.story_of_man_and_woman.data.database.converter.Converters
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.database.dao.TestDAO
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.leopold.mvvm.data.db.converter.DateConverter


@Database(version = 3, entities = [Test::class, Feed::class],exportSchema = false )
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun testDAO(): TestDAO


}
