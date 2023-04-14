package ru.netology.travelmarkers.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netology.travelmarkers.dao.BookmarksDao
import ru.netology.travelmarkers.entity.BookmarksEntity


@Database(entities = [BookmarksEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun bookmarksDao(): BookmarksDao

    companion object{
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase{
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
    }
}