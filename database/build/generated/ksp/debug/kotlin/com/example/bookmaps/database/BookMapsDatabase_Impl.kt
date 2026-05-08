package com.example.bookmaps.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BookMapsDatabase_Impl : BookMapsDatabase() {
  private val _bookPageDao: Lazy<BookPageDao> = lazy {
    BookPageDao_Impl(this)
  }

  private val _bookmarkDao: Lazy<BookmarkDao> = lazy {
    BookmarkDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "ba5c344ee2ca2bc0d05c38b9f01b6306", "8cedab8ecb132838ee57fd25e44c4e6e") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `book_pages` (`pageNumber` INTEGER NOT NULL, `chapterNumber` INTEGER NOT NULL, `text` TEXT NOT NULL, PRIMARY KEY(`pageNumber`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `pageNumber` INTEGER NOT NULL, `title` TEXT NOT NULL, `note` TEXT NOT NULL, `last_updated` INTEGER NOT NULL, FOREIGN KEY(`pageNumber`) REFERENCES `book_pages`(`pageNumber`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_bookmarks_pageNumber` ON `bookmarks` (`pageNumber`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ba5c344ee2ca2bc0d05c38b9f01b6306')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `book_pages`")
        connection.execSQL("DROP TABLE IF EXISTS `bookmarks`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsBookPages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBookPages.put("pageNumber", TableInfo.Column("pageNumber", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookPages.put("chapterNumber", TableInfo.Column("chapterNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookPages.put("text", TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBookPages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBookPages: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBookPages: TableInfo = TableInfo("book_pages", _columnsBookPages, _foreignKeysBookPages, _indicesBookPages)
        val _existingBookPages: TableInfo = read(connection, "book_pages")
        if (!_infoBookPages.equals(_existingBookPages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |book_pages(com.example.bookmaps.database.BookPage).
              | Expected:
              |""".trimMargin() + _infoBookPages + """
              |
              | Found:
              |""".trimMargin() + _existingBookPages)
        }
        val _columnsBookmarks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBookmarks.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("pageNumber", TableInfo.Column("pageNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("note", TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBookmarks.put("last_updated", TableInfo.Column("last_updated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBookmarks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysBookmarks.add(TableInfo.ForeignKey("book_pages", "CASCADE", "NO ACTION", listOf("pageNumber"), listOf("pageNumber")))
        val _indicesBookmarks: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesBookmarks.add(TableInfo.Index("index_bookmarks_pageNumber", false, listOf("pageNumber"), listOf("ASC")))
        val _infoBookmarks: TableInfo = TableInfo("bookmarks", _columnsBookmarks, _foreignKeysBookmarks, _indicesBookmarks)
        val _existingBookmarks: TableInfo = read(connection, "bookmarks")
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |bookmarks(com.example.bookmaps.database.Bookmark).
              | Expected:
              |""".trimMargin() + _infoBookmarks + """
              |
              | Found:
              |""".trimMargin() + _existingBookmarks)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "book_pages", "bookmarks")
  }

  public override fun clearAllTables() {
    super.performClear(true, "book_pages", "bookmarks")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BookPageDao::class, BookPageDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(BookmarkDao::class, BookmarkDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun bookPageDao(): BookPageDao = _bookPageDao.value

  public override fun bookmarkDao(): BookmarkDao = _bookmarkDao.value
}
