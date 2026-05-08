package com.example.bookmaps.database

import androidx.room.Embedded
import androidx.room.Relation

data class BookPageWithBookmarks(
    @Embedded val page: BookPage,
    @Relation(
        parentColumn = "pageNumber",
        entityColumn = "pageNumber",
    )
    val bookmarks: List<Bookmark>,
)
