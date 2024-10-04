package com.tstudioz.fax.fme.feature.menza.dao.interfaces

import com.tstudioz.fax.fme.database.models.NoteRealm

interface NoteDaoInterface {

    suspend fun getNotes(): List<NoteRealm>

    suspend fun insert(note: NoteRealm)

    suspend fun delete(note: NoteRealm)
}