package com.tstudioz.fax.fme.feature.menza.service

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface MenzaServiceInterface {

    suspend fun fetchMenza(place:String) : NetworkServiceResult.MenzaResult

}