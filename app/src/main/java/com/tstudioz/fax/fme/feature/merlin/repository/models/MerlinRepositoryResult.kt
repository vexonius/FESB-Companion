package com.tstudioz.fax.fme.feature.merlin.repository.models

import com.tstudioz.fax.fme.models.data.User

sealed class MerlinRepositoryResult {

    sealed class MerlinLoginResult : MerlinRepositoryResult() {
        data class Success(val data: User) : MerlinLoginResult()
        class Failure(throwable: Throwable) : MerlinLoginResult()
    }

}
