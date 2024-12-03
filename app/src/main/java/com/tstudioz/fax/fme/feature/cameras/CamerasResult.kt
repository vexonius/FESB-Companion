package com.tstudioz.fax.fme.feature.cameras

sealed class CamerasResult {

    sealed class GetCamerasResult : CamerasResult() {
        data class Success(val data: String) : GetCamerasResult()
        data object Failure : GetCamerasResult()
    }

}