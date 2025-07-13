package com.tstudioz.fax.fme.feature.menza.service

import com.tstudioz.fax.fme.feature.menza.CamerasResult

interface CamerasServiceInterface{

        suspend fun getCameraImageUrls(path: String): CamerasResult.GetCamerasResult

}