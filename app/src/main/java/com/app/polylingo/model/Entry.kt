package com.app.polylingo.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey
    @NonNull
    var word: String,
    @ColumnInfo(name = "translated_entry")
    @NonNull
    var translatedWord: String
) {

}
