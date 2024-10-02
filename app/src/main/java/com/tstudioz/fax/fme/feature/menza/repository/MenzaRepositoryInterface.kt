package com.tstudioz.fax.fme.feature.menza.repository

import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.models.Menza

interface MenzaRepositoryInterface {

    suspend fun fetchMenzaDetails(url: String): MenzaResult

    suspend fun readMenza(): Menza?

}