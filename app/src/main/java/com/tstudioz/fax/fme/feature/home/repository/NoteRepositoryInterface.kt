package com.tstudioz.fax.fme.feature.home.repository

import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.database.models.NoteRealm
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.home.WeatherFeature

interface NoteRepositoryInterface {
    suspend fun getNotes(): List<NoteRealm>
    suspend fun insert(note: NoteRealm)
    suspend fun delete(note: NoteRealm)
}
