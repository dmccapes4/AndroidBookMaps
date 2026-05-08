package com.example.bookmaps.database

import androidx.collection.LongSparseArray
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BookPageDao_Impl(
  __db: RoomDatabase,
) : BookPageDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBookPage: EntityInsertAdapter<BookPage>
  init {
    this.__db = __db
    this.__insertAdapterOfBookPage = object : EntityInsertAdapter<BookPage>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `book_pages` (`pageNumber`,`chapterNumber`,`text`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BookPage) {
        statement.bindLong(1, entity.pageNumber.toLong())
        statement.bindLong(2, entity.chapterNumber.toLong())
        statement.bindText(3, entity.text)
      }
    }
  }

  public override suspend fun insertPages(pages: List<BookPage>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookPage.insert(_connection, pages)
  }

  public override suspend fun insertPage(page: BookPage): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBookPage.insert(_connection, page)
  }

  public override suspend fun getPage(pageNumber: Int): BookPage? {
    val _sql: String = "SELECT * FROM book_pages WHERE pageNumber = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfChapterNumber: Int = getColumnIndexOrThrow(_stmt, "chapterNumber")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _result: BookPage?
        if (_stmt.step()) {
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpChapterNumber: Int
          _tmpChapterNumber = _stmt.getLong(_columnIndexOfChapterNumber).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          _result = BookPage(_tmpPageNumber,_tmpChapterNumber,_tmpText)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePage(pageNumber: Int): Flow<BookPage?> {
    val _sql: String = "SELECT * FROM book_pages WHERE pageNumber = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("book_pages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfChapterNumber: Int = getColumnIndexOrThrow(_stmt, "chapterNumber")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _result: BookPage?
        if (_stmt.step()) {
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpChapterNumber: Int
          _tmpChapterNumber = _stmt.getLong(_columnIndexOfChapterNumber).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          _result = BookPage(_tmpPageNumber,_tmpChapterNumber,_tmpText)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPageWithBookmarks(pageNumber: Int): BookPageWithBookmarks? {
    val _sql: String = "SELECT * FROM book_pages WHERE pageNumber = ? LIMIT 1"
    return performSuspending(__db, true, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfChapterNumber: Int = getColumnIndexOrThrow(_stmt, "chapterNumber")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _collectionBookmarks: LongSparseArray<MutableList<Bookmark>> = LongSparseArray<MutableList<Bookmark>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPageNumber)
          if (!_collectionBookmarks.containsKey(_tmpKey)) {
            _collectionBookmarks.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipbookmarksAscomExampleBookmapsDatabaseBookmark(_connection, _collectionBookmarks)
        val _result: BookPageWithBookmarks?
        if (_stmt.step()) {
          val _tmpPage: BookPage
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpChapterNumber: Int
          _tmpChapterNumber = _stmt.getLong(_columnIndexOfChapterNumber).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          _tmpPage = BookPage(_tmpPageNumber,_tmpChapterNumber,_tmpText)
          val _tmpBookmarksCollection: MutableList<Bookmark>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfPageNumber)
          _tmpBookmarksCollection = checkNotNull(_collectionBookmarks.get(_tmpKey_1))
          _result = BookPageWithBookmarks(_tmpPage,_tmpBookmarksCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePageWithBookmarks(pageNumber: Int): Flow<BookPageWithBookmarks?> {
    val _sql: String = "SELECT * FROM book_pages WHERE pageNumber = ? LIMIT 1"
    return createFlow(__db, true, arrayOf("bookmarks", "book_pages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfChapterNumber: Int = getColumnIndexOrThrow(_stmt, "chapterNumber")
        val _columnIndexOfText: Int = getColumnIndexOrThrow(_stmt, "text")
        val _collectionBookmarks: LongSparseArray<MutableList<Bookmark>> = LongSparseArray<MutableList<Bookmark>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPageNumber)
          if (!_collectionBookmarks.containsKey(_tmpKey)) {
            _collectionBookmarks.put(_tmpKey, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshipbookmarksAscomExampleBookmapsDatabaseBookmark(_connection, _collectionBookmarks)
        val _result: BookPageWithBookmarks?
        if (_stmt.step()) {
          val _tmpPage: BookPage
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpChapterNumber: Int
          _tmpChapterNumber = _stmt.getLong(_columnIndexOfChapterNumber).toInt()
          val _tmpText: String
          _tmpText = _stmt.getText(_columnIndexOfText)
          _tmpPage = BookPage(_tmpPageNumber,_tmpChapterNumber,_tmpText)
          val _tmpBookmarksCollection: MutableList<Bookmark>
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfPageNumber)
          _tmpBookmarksCollection = checkNotNull(_collectionBookmarks.get(_tmpKey_1))
          _result = BookPageWithBookmarks(_tmpPage,_tmpBookmarksCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countPages(): Int {
    val _sql: String = "SELECT COUNT(*) FROM book_pages"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __fetchRelationshipbookmarksAscomExampleBookmapsDatabaseBookmark(_connection: SQLiteConnection, _map: LongSparseArray<MutableList<Bookmark>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipbookmarksAscomExampleBookmapsDatabaseBookmark(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`pageNumber`,`title`,`note`,`last_updated` FROM `bookmarks` WHERE `pageNumber` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "pageNumber")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfPageNumber: Int = 1
      val _columnIndexOfTitle: Int = 2
      val _columnIndexOfNote: Int = 3
      val _columnIndexOfLastUpdatedMillis: Int = 4
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<Bookmark>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: Bookmark
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPageNumber: Int
          _tmpPageNumber = _stmt.getLong(_columnIndexOfPageNumber).toInt()
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_columnIndexOfNote)
          val _tmpLastUpdatedMillis: Long
          _tmpLastUpdatedMillis = _stmt.getLong(_columnIndexOfLastUpdatedMillis)
          _item_1 = Bookmark(_tmpId,_tmpPageNumber,_tmpTitle,_tmpNote,_tmpLastUpdatedMillis)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
