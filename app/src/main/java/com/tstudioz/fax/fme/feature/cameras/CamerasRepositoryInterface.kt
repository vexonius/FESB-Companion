package com.tstudioz.fax.fme.feature.cameras

import okhttp3.HttpUrl

interface CamerasRepositoryInterface {

    suspend fun getImages(path: String): HttpUrl?

}