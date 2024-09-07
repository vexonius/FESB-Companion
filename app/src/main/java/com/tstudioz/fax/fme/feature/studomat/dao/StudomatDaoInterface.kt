package com.tstudioz.fax.fme.feature.studomat.dao

import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject

interface StudomatDaoInterface {
    suspend fun insert(subjects: List<StudomatSubject>)

    suspend fun read(): List<StudomatSubject>
}