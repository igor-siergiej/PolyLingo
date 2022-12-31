package com.app.polylingo.model

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.polylingo.datasource.PolyLingoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PolyLingoRepository = PolyLingoRepository(application)

    var entryList: LiveData<MutableList<Entry>> = repository.getAllEntries()

    fun addEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(entry)
        }
    }

    fun removeEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.remove(entry)
        }
    }

    suspend fun removeAll() {
            repository.removeAll()
    }

    // TODO these methods should also include being able to sort by translated word too
    fun sortEntriesAsc() {
        viewModelScope.launch(Dispatchers.IO) {
            val sortedList = repository.getSortedEntriesAsc()
            removeAll()
            sortedList.forEach{entry ->
                addEntry(entry)
            }
        }
    }

    fun sortEntriesDesc() {
        viewModelScope.launch(Dispatchers.IO) {
            val sortedList = repository.getSortedEntriesDsc()
            removeAll()
            sortedList.forEach{entry ->
                addEntry(entry)
            }
        }
    }
}