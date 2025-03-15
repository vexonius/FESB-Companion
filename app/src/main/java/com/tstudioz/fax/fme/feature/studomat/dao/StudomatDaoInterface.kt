package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.models.Year

interface StudomatDaoInterface {
    suspend fun insert(subjects: List<StudomatSubject>)

    suspend fun insertYears(years: List<StudomatYearInfo>)

    suspend fun read(): List<StudomatSubject>

    suspend fun readYearNames(): List<StudomatYearInfo>

    suspend fun readData(): List<Pair<StudomatYearInfo, List<StudomatSubject>>>
}