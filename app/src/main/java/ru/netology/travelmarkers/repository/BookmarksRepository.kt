package ru.netology.travelmarkers.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.travelmarkers.dto.Bookmark

interface BookmarksRepository {
    val data: Flow<List<Bookmark>>
    suspend fun save(bookmark: Bookmark)
    suspend fun removeById(id: Long)
    fun getById(id: Long): Bookmark?
}