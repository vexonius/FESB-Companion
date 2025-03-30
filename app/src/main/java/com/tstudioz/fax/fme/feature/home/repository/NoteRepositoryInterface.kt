package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.database.models.NoteRoom

interface NoteRepositoryInterface {

    suspend fun getNotes(): List<Note>

    suspend fun insert(note: Note)

    suspend fun delete(note: Note)

}
