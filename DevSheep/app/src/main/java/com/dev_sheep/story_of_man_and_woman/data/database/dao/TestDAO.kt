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
interface TestDAO {

    @Query("SELECT * FROM Test WHERE id = :id")
    fun getById(id: String?): LiveData<Test>

    @Query("SELECT * FROM Test WHERE id IN(:evolutionIds)")
    fun getEvolutionsByIds(evolutionIds: List<String>): LiveData<List<Test>>

    @Query("SELECT * FROM Test" )
    fun all(): LiveData<List<Test>>

    @Query("SELECT * FROM Test ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun allList(limit: Int, offset: Int) : LiveData<List<Test>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(pokemon: List<Test>)

    @Query("DELETE FROM Test")
    fun deleteAll()

    @Delete
    fun delete(model: Test)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAllList(feed: List<Feed>)

    @Query("SELECT * FROM Feed" )
    fun getallList(): LiveData<List<Feed>>

    @Query("SELECT * FROM Search")
    fun getSearchList(): LiveData<List<Search>>

    @Query("DELETE FROM Search")
    fun deleteAllSearch()

    @Query("DELETE FROM Search WHERE id = :id")
    fun deleteSearch(id : Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSearch(search: Search)



}
