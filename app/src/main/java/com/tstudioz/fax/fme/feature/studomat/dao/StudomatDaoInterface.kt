package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Year

interface StudomatDaoInterface {
    suspend fun insert(subjects: List<StudomatSubject>)

    suspend fun insertYears(years: List<Year>)

    suspend fun read(year: String): List<StudomatSubject>

    suspend fun readYears(): List<Year>
}