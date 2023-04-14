package ru.netology.travelmarkers.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.travelmarkers.entity.BookmarksEntity

@Dao
interface BookmarksDao {

    @Query("SELECT * FROM BookmarksEntity ORDER BY id DESC")
    fun getAll(): Flow<List<BookmarksEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarksEntity)

    @Query("SELECT * FROM BookmarksEntity WHERE id = :id")
    fun getById(id: Long): BookmarksEntity?

    @Query("DELETE FROM BookmarksEntity WHERE id = :id")
    suspend fun removeById(id: Long)

}