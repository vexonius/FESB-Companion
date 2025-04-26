package com.tstudioz.fax.fme.feature.attendance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry

@Dao
interface AttendanceDao {
    @Query("DELETE FROM attendanceentry")
    fun deleteAll()

    @Insert(onConflict = REPLACE)
    fun insert(attendance: List<AttendanceEntry>)

    @Query("SELECT * FROM attendanceentry")
    fun read(): List<AttendanceEntry>
}