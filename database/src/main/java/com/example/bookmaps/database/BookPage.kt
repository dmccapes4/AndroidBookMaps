package com.example.bookmaps.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_pages")
data class BookPage(
    @PrimaryKey val pageNumber: Int,
    val chapterNumber: Int,
    val text: String,
)
