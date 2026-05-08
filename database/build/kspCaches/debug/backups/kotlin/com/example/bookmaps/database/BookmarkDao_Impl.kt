package com.example.bookmaps.database

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
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
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BookmarkDao_Impl(
  __db: RoomDatabase,
) : BookmarkDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBookmark: EntityInsertAdapter<Bookmark>

  private val __deleteAdapterOfBookmark: EntityDeleteOrUpdateAdapter<Bookmark>

  private val __updateAdapterOfBookmark: EntityDeleteOrUpdateAdapter<Bookmark>
  init {
    this.__db = __db
    this.__insertAdapterOfBookmark = object : EntityInsertAdapter<Bookmark>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `bookmarks` (`id`,`pageNumber`,`title`,`note`,`last_updated`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Bookmark) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.pageNumber.toLong())
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.note)
        statement.bindLong(5, entity.lastUpdatedMillis)
      }
    }
    this.__deleteAdapterOfBookmark = object : EntityDeleteOrUpdateAdapter<Bookmark>() {
      protected override fun createQuery(): String = "DELETE FROM `bookmarks` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Bookmark) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfBookmark = object : EntityDeleteOrUpdateAdapter<Bookmark>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `bookmarks` SET `id` = ?,`pageNumber` = ?,`title` = ?,`note` = ?,`last_updated` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Bookmark) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.pageNumber.toLong())
        statement.bindText(3, entity.title)
        statement.bindText(4, entity.note)
        statement.bindLong(5, entity.lastUpdatedMillis)
        statement.bindLong(6, entity.id)
      }
    }
  }

  public override suspend fun insert(bookmark: Bookmark): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfBookmark.insertAndReturnId(_connection, bookmark)
    _result
  }

  public override suspend fun delete(bookmark: Bookmark): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfBookmark.handle(_connection, bookmark)
  }

  public override suspend fun update(bookmark: Bookmark): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfBookmark.handle(_connection, bookmark)
  }

  public override suspend fun getById(id: Long): Bookmark? {
    val _sql: String = "SELECT * FROM bookmarks WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfLastUpdatedMillis: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _result: Bookmark?
        if (_stmt.step()) {
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
          _result = Bookmark(_tmpId,_tmpPageNumber,_tmpTitle,_tmpNote,_tmpLastUpdatedMillis)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeBookmarksForPage(pageNumber: Int): Flow<List<Bookmark>> {
    val _sql: String = "SELECT * FROM bookmarks WHERE pageNumber = ? ORDER BY last_updated DESC"
    return createFlow(__db, false, arrayOf("bookmarks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfLastUpdatedMillis: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _result: MutableList<Bookmark> = mutableListOf()
        while (_stmt.step()) {
          val _item: Bookmark
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
          _item = Bookmark(_tmpId,_tmpPageNumber,_tmpTitle,_tmpNote,_tmpLastUpdatedMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBookmarksForPage(pageNumber: Int): List<Bookmark> {
    val _sql: String = "SELECT * FROM bookmarks WHERE pageNumber = ? ORDER BY last_updated DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, pageNumber.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPageNumber: Int = getColumnIndexOrThrow(_stmt, "pageNumber")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _columnIndexOfLastUpdatedMillis: Int = getColumnIndexOrThrow(_stmt, "last_updated")
        val _result: MutableList<Bookmark> = mutableListOf()
        while (_stmt.step()) {
          val _item: Bookmark
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
          _item = Bookmark(_tmpId,_tmpPageNumber,_tmpTitle,_tmpNote,_tmpLastUpdatedMillis)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
