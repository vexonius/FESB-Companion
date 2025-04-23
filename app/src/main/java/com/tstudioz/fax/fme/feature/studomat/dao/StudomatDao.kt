package com.tstudioz.fax.fme.feature.studomat.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year

@Dao
interface StudomatDao {
    @Query("DELETE FROM Year")
    fun deleteYears()

    @Query("DELETE FROM StudomatSubject WHERE year = :year")
    fun deleteAll(year: String)

    @Insert(onConflict = REPLACE)
    fun insertYears(years: List<Year>)

    @Insert(onConflict = REPLACE)
    fun insert(subjects: List<StudomatSubject>)

    @Query("SELECT * FROM Year")
    fun readYears(): List<Year>

    @Query("SELECT * FROM StudomatSubject WHERE year = :year")
    fun read(year: String): List<StudomatSubject>

}