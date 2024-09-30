package com.tstudioz.fax.fme.feature.menza.dao.interfaces

import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.database.models.NoteRealm

interface MenzaDaoInterface {

    suspend fun insert(classes: List<Meni> )

    suspend fun getCachedMenza(): List<Meni>

}