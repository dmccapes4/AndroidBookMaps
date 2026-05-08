package com.example.bookmaps.book

import android.app.Application
import com.example.bookmaps.database.BookMapsDatabase
import com.example.bookmaps.reader.ReaderPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BookRepository(
    private val application: Application,
) {
    private val db = BookMapsDatabase.getInstance(application)
    private val prefs = ReaderPreferences(application)
    private val ingestMutex = Mutex()

    val bookPageDao get() = db.bookPageDao()
    val bookmarkDao get() = db.bookmarkDao()

    suspend fun ensureBookInRoom() {
        ingestMutex.withLock {
            if (bookPageDao.countPages() > 0) {
                return
            }
            application.assets.open(BookAssetPaths.ARISTOPIA_ASSET).use { stream ->
                val text = stream.bufferedReader().readText()
                val pages = PlainTextBookParser.parse(text)
                bookPageDao.insertPages(pages)
            }
        }
    }

    suspend fun clampedReadingPage(candidate: Int): Int {
        val max = bookPageDao.getMaxPageNumber().coerceAtLeast(1)
        return candidate.coerceIn(1, max)
    }

    suspend fun readLastSavedPageOrNull(): Int? = prefs.readLastSavedPage()

    suspend fun saveLastReadPage(page: Int) {
        prefs.saveLastReadPage(page)
    }

    /**
     * First launch (no saved page): open the most recently updated bookmark, or page 1.
     * Persists the chosen page so later sessions use [readLastSavedPageOrNull].
     */
    suspend fun resolveInitialPage(): Int {
        val max = bookPageDao.getMaxPageNumber()
        if (max == 0) {
            return 1
        }
        val saved = readLastSavedPageOrNull()
        if (saved != null && saved in 1..max) {
            return saved
        }
        val bookmark = bookmarkDao.getMostRecentlyUpdatedBookmark()
        val start = (bookmark?.pageNumber ?: 1).coerceIn(1, max)
        saveLastReadPage(start)
        return start
    }
}
