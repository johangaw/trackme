package com.example.trackme.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "track_entry",
        foreignKeys = [ForeignKey(entity = Track::class,
                                  parentColumns = ["id"],
                                  childColumns = ["trackId"],
                                  onDelete = ForeignKey.CASCADE)])
data class TrackEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val speed: Float,
    val bearing: Float,
    val time: Long,
    val trackId: Int,
)

@Entity(tableName = "track")
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
)

@Dao
interface TrackDao {
    @Insert
    suspend fun insert(vararg entries: Track)

    @Insert
    suspend fun insert(entries: List<Track>)

    @Query("SELECT * FROM track WHERE id = :id")
    fun getAndObserve(id: Int): LiveData<Track>

    @Query("SELECT * FROM track WHERE id = :id")
    suspend fun get(id: Int): Track
}

@Dao
interface TrackEntryDao {
    @Insert
    suspend fun insert(vararg entries: TrackEntry)

    @Insert
    suspend fun insert(entries: List<TrackEntry>)

    @Query("SELECT * FROM track_entry")
    fun getAllAndObserve(): LiveData<List<TrackEntry>>

    @Query("DELETE FROM track_entry")
    suspend fun removeTrack()
}

@Database(entities = [TrackEntry::class, Track::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackEntryDao(): TrackEntryDao
    abstract fun trackDao(): TrackDao

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
