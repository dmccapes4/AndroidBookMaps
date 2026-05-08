package com.example.bookmaps.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BookPageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<BookPage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: BookPage)

    @Query("SELECT * FROM book_pages WHERE pageNumber = :pageNumber LIMIT 1")
    suspend fun getPage(pageNumber: Int): BookPage?

    @Query("SELECT * FROM book_pages WHERE pageNumber = :pageNumber LIMIT 1")
    fun observePage(pageNumber: Int): Flow<BookPage?>

    @Transaction
    @Query("SELECT * FROM book_pages WHERE pageNumber = :pageNumber LIMIT 1")
    suspend fun getPageWithBookmarks(pageNumber: Int): BookPageWithBookmarks?

    @Transaction
    @Query("SELECT * FROM book_pages WHERE pageNumber = :pageNumber LIMIT 1")
    fun observePageWithBookmarks(pageNumber: Int): Flow<BookPageWithBookmarks?>

    @Query("SELECT COUNT(*) FROM book_pages")
    suspend fun countPages(): Int

    @Query("SELECT COALESCE(MAX(pageNumber), 0) FROM book_pages")
    fun observeMaxPageNumber(): Flow<Int>

    @Query("SELECT COALESCE(MAX(pageNumber), 0) FROM book_pages")
    suspend fun getMaxPageNumber(): Int

    @Query(
        """
        SELECT chapterNumber, MIN(pageNumber) AS startPage
        FROM book_pages
        GROUP BY chapterNumber
        ORDER BY chapterNumber ASC
        """,
    )
    fun observeChapterStarts(): Flow<List<ChapterStart>>
}
