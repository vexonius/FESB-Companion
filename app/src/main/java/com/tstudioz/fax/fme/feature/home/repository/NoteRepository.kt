package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.database.models.toNote
import com.tstudioz.fax.fme.database.models.toNoteRoom
import com.tstudioz.fax.fme.feature.home.dao.NoteDao

class NoteRepository(private val noteDao: NoteDao) : NoteRepositoryInterface {

    override suspend fun getNotes(): List<Note> {
        return noteDao.getNotes().map { it.toNote() }
    }

    override suspend fun insert(note: Note) {
        noteDao.insert(note.toNoteRoom())
    }

    override suspend fun delete(note: Note) {
        noteDao.delete(note.toNoteRoom())
    }

}