package ru.netology.travelmarkers.repository

import kotlinx.coroutines.flow.map
import ru.netology.travelmarkers.dao.BookmarksDao
import ru.netology.travelmarkers.dto.Bookmark
import ru.netology.travelmarkers.entity.BookmarksEntity
import ru.netology.travelmarkers.entity.toDto

class BookmarksRepositoryImpl(private val dao: BookmarksDao) : BookmarksRepository {
    override val data = dao.getAll()
        .map(List<BookmarksEntity>::toDto)

    override suspend fun save(bookmark: Bookmark) = dao.insert(BookmarksEntity.fromDto(bookmark))

    override suspend fun removeById(id: Long) = dao.removeById(id)

    override fun getById(id: Long): Bookmark? {
        val bookmark = dao.getById(id)
        return bookmark?.toDto()
    }

}