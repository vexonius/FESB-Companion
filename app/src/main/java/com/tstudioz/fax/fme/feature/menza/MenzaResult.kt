package com.tstudioz.fax.fme.feature.menza

import com.tstudioz.fax.fme.feature.menza.models.Menza

sealed class MenzaResult {
    data class Success(val data: Menza) : MenzaResult()
    class Failure(exception: Throwable) : MenzaResult()
}

sealed class CamerasResult {

    sealed class GetCamerasResult : CamerasResult() {
        data class Success(val data: String) : GetCamerasResult()
        data object Failure : GetCamerasResult()
    }

}