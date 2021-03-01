package com.dev_sheep.story_of_man_and_woman.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Feed
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Search
import com.dev_sheep.story_of_man_and_woman.data.database.entity.Test

@Dao
interface SearchDAO {




    @Query("SELECT * FROM Search")
    fun getSearchList(): LiveData<List<Search>>

    @Query("DELETE FROM Search")
    fun deleteAllSearch()

    @Query("DELETE FROM Search WHERE id = :id")
    fun deleteSearch(id : Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSearch(search: Search)


}
