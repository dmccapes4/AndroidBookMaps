package com.example.bookmaps.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: Bookmark): Long

    @Update
    suspend fun update(bookmark: Bookmark)

    @Delete
    suspend fun delete(bookmark: Bookmark)

    @Query("SELECT * FROM bookmarks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Bookmark?

    @Query("SELECT * FROM bookmarks WHERE pageNumber = :pageNumber ORDER BY last_updated DESC")
    fun observeBookmarksForPage(pageNumber: Int): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE pageNumber = :pageNumber ORDER BY last_updated DESC")
    suspend fun getBookmarksForPage(pageNumber: Int): List<Bookmark>

    @Query("SELECT * FROM bookmarks ORDER BY last_updated DESC")
    fun observeAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks ORDER BY last_updated DESC LIMIT 1")
    suspend fun getMostRecentlyUpdatedBookmark(): Bookmark?
}
