package com.tstudioz.fax.fme.feature.timetable.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.database.models.EventRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeTableDao {
    @Query("DELETE FROM eventroom")
    fun deleteAll()

    @Insert(onConflict = REPLACE)
    fun insert(classes: List<EventRoom>)

    @Query("SELECT * FROM eventroom")
    fun getEvents(): List<EventRoom>

    @Query("SELECT * FROM eventroom")
    fun getEventsAsync(): Flow<List<EventRoom>>
}
