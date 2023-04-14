package ru.netology.travelmarkers.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.travelmarkers.dto.Bookmark

@Entity
data class BookmarksEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val description: String
) {

    fun toDto() = Bookmark(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        description = description
    )

    companion object {
        fun fromDto(bookmark: Bookmark) = BookmarksEntity(
            id = bookmark.id,
            name = bookmark.name,
            latitude = bookmark.latitude,
            longitude = bookmark.longitude,
            description = bookmark.description
        )
    }
}

fun List<BookmarksEntity>.toDto(): List<Bookmark> = map(BookmarksEntity::toDto)