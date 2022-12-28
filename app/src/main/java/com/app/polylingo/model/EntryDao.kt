package com.app.polylingo.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EntryDao {
    @Insert
    suspend fun insertSingleEntry(entry: Entry)

    @Insert
    suspend fun insertMultipleEntries(entryList: MutableList<Entry>)

    @Update
    suspend fun updateEntry(entry: Entry)

    @Delete
    suspend fun deleteEntry(entry: Entry)

    @Query("DELETE FROM entries")
    suspend fun deleteAll()

    @Query("SELECT * FROM entries")
    fun getAllEntries(): LiveData<MutableList<Entry>>

    @Query("SELECT * " +
            "FROM entries " +
            " ORDER BY word ASC"
            )
    fun getAllEntriesSortedAsc(): MutableList<Entry>

    @Query("SELECT * " +
            "FROM entries " +
            " ORDER BY word DESC"
    )
    fun getAllEntriesSortedDesc(): MutableList<Entry>
}