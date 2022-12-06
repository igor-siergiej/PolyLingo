package com.app.polylingo.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.app.polylingo.datasource.PolyLingoRepository

class EntryViewModel(application: Application) :AndroidViewModel(application) {
    private val repository: PolyLingoRepository = PolyLingoRepository(application)

    var entryList: LiveData<List<Entry>> = repository.getAllEntries()

    suspend fun addEntry(entry: Entry) {
        repository.insert(entry)
    }
    // search should be here
}