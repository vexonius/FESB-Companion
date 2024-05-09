package com.tstudioz.fax.fme.feature.merlin.repository

import com.tstudioz.fax.fme.feature.merlin.repository.models.MerlinRepositoryResult
import com.tstudioz.fax.fme.feature.merlin.services.MerlinNetworkServiceResult

interface MerlinRepositoryInterface {

    suspend fun login(email: String, password: String): MerlinNetworkServiceResult.MerlinNetworkResult
}
