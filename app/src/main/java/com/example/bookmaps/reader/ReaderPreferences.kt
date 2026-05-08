package com.example.bookmaps.reader

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.readerDataStore: DataStore<Preferences> by preferencesDataStore(name = "reader")

class ReaderPreferences(
    context: Context,
) {
    private val dataStore = context.applicationContext.readerDataStore

    suspend fun readLastSavedPage(): Int? {
        val value =
            dataStore.data
                .map { it[lastReadPageKey] ?: UNSET }
                .first()
        return if (value == UNSET) null else value
    }

    suspend fun saveLastReadPage(page: Int) {
        dataStore.edit {
            it[lastReadPageKey] = page
        }
    }

    companion object {
        private val lastReadPageKey = intPreferencesKey("last_read_page")
        private const val UNSET = -1
    }
}
