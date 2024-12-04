package com.tstudioz.fax.fme.feature.cameras

interface CamerasServiceInterface{

        suspend fun getCameraImageUrls(path: String): CamerasResult.GetCamerasResult

}