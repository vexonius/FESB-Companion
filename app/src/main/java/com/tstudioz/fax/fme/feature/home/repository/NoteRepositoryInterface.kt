package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.NoteRealm

interface NoteRepositoryInterface {

    suspend fun getNotes(): List<NoteRealm>

    suspend fun insert(note: NoteRealm)

    suspend fun delete(note: NoteRealm)

}
