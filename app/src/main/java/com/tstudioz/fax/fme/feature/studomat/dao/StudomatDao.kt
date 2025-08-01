package com.tstudioz.fax.fme.feature.studomat.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo

@Dao
interface StudomatDao {
    @Query("DELETE FROM studomatyearinfo")
    fun deleteYears()

    @Query("DELETE FROM StudomatSubject WHERE year = :year")
    fun deleteAll(year: String)

    @Insert(onConflict = REPLACE)
    fun insertYears(years: List<StudomatYearInfo>)

    @Insert(onConflict = REPLACE)
    fun insert(subjects: List<StudomatSubject>)

    @Query("SELECT * FROM studomatyearinfo")
    fun readYears(): List<StudomatYearInfo>

    @Query("SELECT * FROM StudomatSubject")
    fun read(): List<StudomatSubject>

}