package com.dev_sheep.story_of_man_and_woman.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev_sheep.story_of_man_and_woman.data.Test

@Dao
interface TestDAO {

    @Query("SELECT * FROM Test WHERE id = :id")
    fun getById(id: String?): LiveData<Test>

    @Query("SELECT * FROM Test WHERE id IN(:evolutionIds)")
    fun getEvolutionsByIds(evolutionIds: List<String>): LiveData<List<Test>>

    @Query("SELECT * FROM Test")
    fun all(): LiveData<List<Test>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(pokemon: List<Test>)

    @Query("DELETE FROM Test")
    fun deleteAll()

    @Delete
    fun delete(model: Test)
}
