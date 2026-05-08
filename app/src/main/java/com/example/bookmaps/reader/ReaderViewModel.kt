package com.example.bookmaps.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookmaps.book.BookRepository
import com.example.bookmaps.database.BookPage
import com.example.bookmaps.database.Bookmark
import com.example.bookmaps.database.ChapterStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReaderViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repo = BookRepository(application)

    private val _bootstrapping = MutableStateFlow(true)
    val bootstrapping: StateFlow<Boolean> = _bootstrapping.asStateFlow()

    private val _maxPage = MutableStateFlow(0)
    val maxPage: StateFlow<Int> = _maxPage.asStateFlow()

    private val _readerPage = MutableStateFlow(1)
    val readerPage: StateFlow<Int> = _readerPage.asStateFlow()

    fun observePage(oneBasedPage: Int): Flow<BookPage?> =
        repo.bookPageDao.observePage(oneBasedPage)

    fun observeChapterStarts(): Flow<List<ChapterStart>> =
        repo.bookPageDao.observeChapterStarts()

    val chapterStarts =
        observeChapterStarts().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList(),
        )

    val allBookmarks =
        repo.bookmarkDao.observeAllBookmarks().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList(),
        )

    init {
        viewModelScope.launch {
            repo.ensureBookInRoom()
            val max = repo.bookPageDao.getMaxPageNumber()
            val start = repo.resolveInitialPage().coerceIn(1, maxOf(max, 1))
            _maxPage.value = max
            _readerPage.value = start
            _bootstrapping.value = false
        }
    }

    fun syncReaderPageFromPager(settledZeroBasedPage: Int) {
        val max = maxOf(_maxPage.value, 1)
        val clamped = (settledZeroBasedPage + 1).coerceIn(1, max)
        viewModelScope.launch {
            repo.saveLastReadPage(clamped)
        }
        if (_readerPage.value != clamped) {
            _readerPage.value = clamped
        }
    }

    fun goToPage(pageOneBased: Int) {
        val max = maxOf(_maxPage.value, 1)
        val clamped = pageOneBased.coerceIn(1, max)
        viewModelScope.launch {
            repo.saveLastReadPage(clamped)
        }
        _readerPage.value = clamped
    }

    suspend fun addBookmark(title: String, note: String) {
        val page = _readerPage.value
        repo.bookmarkDao.insert(
            Bookmark(
                pageNumber = page,
                title = title.trim().ifBlank { "Page $page" },
                note = note.trim(),
                lastUpdatedMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun updateBookmark(bookmark: Bookmark, title: String, note: String) {
        repo.bookmarkDao.update(
            bookmark.copy(
                title = title.trim(),
                note = note.trim(),
                lastUpdatedMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        repo.bookmarkDao.delete(bookmark)
    }
}
