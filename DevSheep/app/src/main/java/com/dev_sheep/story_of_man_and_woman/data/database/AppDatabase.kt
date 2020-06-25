package com.dev_sheep.story_of_man_and_woman.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test
import com.dev_sheep.story_of_man_and_woman.data.database.dao.TestDAO

@Database(entities = [Test::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun testDAO(): TestDAO
}
