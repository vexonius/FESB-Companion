package com.tstudioz.fax.fme.feature.merlin.services

import com.tstudioz.fax.fme.models.data.User

sealed class MerlinNetworkServiceResult {

    sealed class MerlinNetworkResult : MerlinNetworkServiceResult() {
        data class Success(val data: Any) : MerlinNetworkResult()
        class Failure(error: String) : MerlinNetworkResult()
    }

}
