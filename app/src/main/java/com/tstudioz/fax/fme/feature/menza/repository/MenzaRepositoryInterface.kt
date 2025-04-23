package com.tstudioz.fax.fme.feature.menza.repository

import com.tstudioz.fax.fme.feature.menza.MenzaResult

interface MenzaRepositoryInterface {

    suspend fun fetchMenzaDetails(): MenzaResult

}