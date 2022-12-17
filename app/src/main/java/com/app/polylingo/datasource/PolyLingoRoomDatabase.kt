package com.app.polylingo.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.polylingo.model.Entry
import com.app.polylingo.model.EntryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Database(entities = [Entry::class], version = 1)
abstract class PolyLingoRoomDatabase : RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        private var instance: PolyLingoRoomDatabase? = null
        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        @Synchronized
        fun getDatabase(context: Context): PolyLingoRoomDatabase? {
            if (instance == null) {
                instance =
                    Room.databaseBuilder<PolyLingoRoomDatabase>(
                        context.applicationContext,
                        PolyLingoRoomDatabase::class.java,
                        "polyLingo_database"
                    )
                        .addCallback(roomDatabaseCallback(context))
                        .build()
            }
            return instance
        }

        private fun roomDatabaseCallback(context: Context): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    coroutineScope.launch {
                        getDatabase(context)
                    }
                }
            }
        }
    }
}
