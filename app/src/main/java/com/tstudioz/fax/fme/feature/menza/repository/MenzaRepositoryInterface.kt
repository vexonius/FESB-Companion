package com.tstudioz.fax.fme.feature.menza.repository

import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.feature.menza.MenzaResult

interface MenzaRepositoryInterface {

    suspend fun fetchMenzaDetails(url: String): MenzaResult

    suspend fun readMenza(): List<Meni>

}