package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.Note

interface NoteRepositoryInterface {

    suspend fun getNotes(): List<Note>

    suspend fun insert(note: Note)

    suspend fun delete(note: Note)

}
