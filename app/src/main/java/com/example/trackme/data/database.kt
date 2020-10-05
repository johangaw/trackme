package com.example.trackme.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "track_entry",
        indices = [Index("trackId", unique = false)],
        foreignKeys = [ForeignKey(entity = Track::class,
                                  parentColumns = ["id"],
                                  childColumns = ["trackId"],
                                  onDelete = ForeignKey.CASCADE)])
data class TrackEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val longitude: Double,
    val latitude: Double,
    val altitude: Double,
    val speed: Float,
    val bearing: Float,
    val time: Long,
    val trackId: Long,
)

@Entity(tableName = "track")
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String = "",
    val active: Boolean = false,
)

data class TrackWithEntries(
    @Embedded
    val track: Track,
    @Relation(parentColumn = "id", entityColumn = "trackId")
    val entries: List<TrackEntry>

)

data class TrackActivity(
    val id: Long,
    val active: Boolean,
)

@Dao
interface TrackDao {
    @Insert
    suspend fun insert(vararg entries: Track): List<Long>

    @Insert
    suspend fun insert(entries: List<Track>): List<Long>

    @Update(entity = Track::class)
    suspend fun updateActivity(entry: TrackActivity)

    @Query("SELECT * FROM track")
    fun getAllAndObserve(): LiveData<List<Track>>

    @Query("SELECT * FROM track")
    fun getAllWithTracksAndObserve(): LiveData<List<TrackWithEntries>>

    @Query("SELECT * FROM track WHERE id = :id")
    fun getAndObserve(id: Long): LiveData<Track>

    @Query("SELECT * FROM track WHERE id = :id")
    suspend fun get(id: Long): Track
}

@Dao
interface TrackEntryDao {
    @Insert
    suspend fun insert(vararg entries: TrackEntry)

    @Insert
    suspend fun insert(entries: List<TrackEntry>)

    @Query("SELECT * FROM track_entry WHERE trackId == :trackId ORDER BY time ASC")
    fun getAllAndObserve(trackId: Long): LiveData<List<TrackEntry>>

    @Query("DELETE FROM track_entry")
    suspend fun removeTrack()
}

@Database(entities = [TrackEntry::class, Track::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackEntryDao(): TrackEntryDao
    abstract fun trackDao(): TrackDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(applicationContext: Context): AppDatabase {
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        applicationContext,
                        AppDatabase::class.java,
                        "track-me-db"
                    )
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}
