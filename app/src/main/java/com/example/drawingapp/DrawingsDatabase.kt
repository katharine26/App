package com.example.drawingapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

/*
Database class used to save my drawings with persistent storage
 */
@Database(entities = [Drawing::class], exportSchema = false, version = 1)
abstract class DrawingsDatabase : RoomDatabase() {
    abstract fun drawingDao(): DrawingDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: DrawingsDatabase? = null

        fun getDatabase(context: Context): DrawingsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                //if another thread initialized this before we got the lock
                //return the object they created
                if (INSTANCE != null) return INSTANCE!!
                //otherwise we're the first thread here, so create the DB
                val instance = Room.databaseBuilder(
                    context.applicationContext, DrawingsDatabase::class.java, "drawing_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}

@Dao
interface DrawingDAO {

    @Insert
    fun addDrawingData(drawing: Drawing)

    @Query("SELECT * from drawings")
    fun allDrawings(): Flow<List<Drawing>>

    @Query("SELECT filename from drawings")
    fun currentDrawing(): String


}