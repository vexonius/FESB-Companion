package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.NoteRealm
import com.tstudioz.fax.fme.feature.menza.dao.interfaces.NoteDaoInterface

class NoteRepository(
    private val noteDao: NoteDaoInterface,
) : NoteRepositoryInterface {

    override suspend fun getNotes(): List<NoteRealm> {
        return noteDao.getNotes()
    }

    override suspend fun insert(note: NoteRealm) {
        noteDao.insert(note)
    }

    override suspend fun delete(note : NoteRealm) {
        noteDao.delete(note)
    }

}