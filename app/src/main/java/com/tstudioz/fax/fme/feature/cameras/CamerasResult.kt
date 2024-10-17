package com.tstudioz.fax.fme.feature.cameras

import android.graphics.Bitmap

sealed class CamerasResult {

    sealed class GetCamerasResult : CamerasResult() {
        data class Success(val data: String) : GetCamerasResult()
        data class Failure(val throwable: Throwable) : GetCamerasResult()
    }

    sealed class Image : CamerasResult() {
        data class Success(val data: Bitmap) : Image()
        data class Failure(val throwable: Throwable) : Image()
    }
}