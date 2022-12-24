package com.app.polylingo.datasource

import android.app.Application
import com.app.polylingo.model.Entry

class PolyLingoRepository(application: Application) {
    private val entryDao = PolyLingoRoomDatabase.getDatabase(application)!!.entryDao()

    suspend fun insert(entry: Entry) {
        entryDao.insertSingleEntry(entry)
    }

    suspend fun remove(entry: Entry) {
        entryDao.deleteEntry(entry)
    }

    suspend fun removeAll() {
        entryDao.deleteAll()
    }

    suspend fun insertMultipleEntries(entries: MutableList<Entry>) {
        entryDao.insertMultipleEntries(entries)
    }// is this needed?

    fun getAllEntries() = entryDao.getAllEntries()

}