package ru.netology.travelmarkers.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.travelmarkers.database.AppDatabase
import ru.netology.travelmarkers.dto.Bookmark
import ru.netology.travelmarkers.repository.BookmarksRepository
import ru.netology.travelmarkers.repository.BookmarksRepositoryImpl

class BookmarksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BookmarksRepository = BookmarksRepositoryImpl(
        AppDatabase.getInstance(application).bookmarksDao()
    )

    val data = repository.data

    fun save(bookmark: Bookmark) = viewModelScope.launch { repository.save(bookmark) }

    fun removeById(id: Long) = viewModelScope.launch { repository.removeById(id) }

    fun getById(id: Long): Bookmark? {
        return repository.getById(id)
    }
}