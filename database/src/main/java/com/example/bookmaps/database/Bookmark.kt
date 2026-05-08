package com.example.bookmaps.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    foreignKeys = [
        ForeignKey(
            entity = BookPage::class,
            parentColumns = ["pageNumber"],
            childColumns = ["pageNumber"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["pageNumber"])],
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pageNumber: Int,
    val title: String,
    val note: String,
    @ColumnInfo(name = "last_updated") val lastUpdatedMillis: Long,
)
