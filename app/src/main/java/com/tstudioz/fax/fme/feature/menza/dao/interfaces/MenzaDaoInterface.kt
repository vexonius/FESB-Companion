package com.tstudioz.fax.fme.feature.menza.dao.interfaces

import com.tstudioz.fax.fme.database.models.Meni

interface MenzaDaoInterface {

    suspend fun insert(classes: List<Meni> )

    suspend fun getCachedMenza(): List<Meni>

}