package com.tstudioz.fax.fme.feature.cameras

interface CamerasServiceInterface{

        suspend fun getCameraImageUrls(href: String): CamerasResult.GetCamerasResult

        suspend fun getCameraImage(href: String, imageId: String): CamerasResult.Image
}