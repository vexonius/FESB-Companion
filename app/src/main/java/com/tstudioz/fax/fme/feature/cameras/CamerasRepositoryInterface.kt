package com.tstudioz.fax.fme.feature.cameras

import android.graphics.Bitmap

interface CamerasRepositoryInterface {

    suspend fun getImage(href: String): Bitmap

}