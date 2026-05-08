package com.example.bookmaps.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookPage::class, Bookmark::class],
    version = 1,
    exportSchema = true,
)
abstract class BookMapsDatabase : RoomDatabase() {
    abstract fun bookPageDao(): BookPageDao

    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private const val DB_NAME = "bookmaps.db"

        @Volatile
        private var instance: BookMapsDatabase? = null

        fun getInstance(context: Context): BookMapsDatabase =
            instance ?: synchronized(this) {
                instance ?: create(context.applicationContext).also { instance = it }
            }

        fun create(context: Context): BookMapsDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                BookMapsDatabase::class.java,
                DB_NAME,
            ).fallbackToDestructiveMigration().build()
    }
}
