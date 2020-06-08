package com.dev_sheep.story_of_man_and_woman.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev_sheep.story_of_man_and_woman.data.Test

@Database(entities = [Test::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun testDAO(): TestDAO
}
