package com.tstudioz.fax.fme.feature.cameras

import android.graphics.Bitmap
import okhttp3.HttpUrl

interface CamerasRepositoryInterface {

    suspend fun getImage(path: String): HttpUrl

}