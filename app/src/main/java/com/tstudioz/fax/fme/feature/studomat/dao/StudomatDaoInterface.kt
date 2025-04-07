package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYear
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import com.tstudioz.fax.fme.feature.studomat.models.Year

interface StudomatDaoInterface {
    suspend fun insert(year: StudomatYear)

    suspend fun read(): List<StudomatSubject>

    suspend fun readYearNames(): List<StudomatYearInfo>

    suspend fun readData(): List<StudomatYear>
}