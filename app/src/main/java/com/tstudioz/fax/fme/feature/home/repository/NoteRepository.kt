package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.database.models.NoteRoom
import com.tstudioz.fax.fme.feature.home.dao.NoteDao

class NoteRepository(private val noteDao: NoteDao) : NoteRepositoryInterface {

    override suspend fun getNotes(): List<Note> {
        return noteDao.getNotes().map { Note(it) }
    }

    override suspend fun insert(note: Note) {
        noteDao.insert(NoteRoom(note))
    }

    override suspend fun delete(note: Note) {
        noteDao.delete(NoteRoom(note))
    }

}