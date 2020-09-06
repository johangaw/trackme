package com.example.trackme.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "track_entry", )
data class TrackEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val speed: Float,
    val bearing: Float,
    val time: Long,
)

@Dao
interface TrackEntryDao {
    @Insert
    suspend fun insert(vararg entries: TrackEntry)

    @Insert
    suspend fun insert(entries: List<TrackEntry>)

    @Query("SELECT * FROM track_entry")
    fun getAll(): LiveData<List<TrackEntry>>

    @Query("DELETE FROM track_entry")
    suspend fun removeTrack()
}

@Database(entities = [TrackEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackEntryDao(): TrackEntryDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(applicationContext: Application): AppDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "track-me-db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}
