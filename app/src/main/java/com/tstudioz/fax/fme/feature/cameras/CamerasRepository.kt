package com.tstudioz.fax.fme.feature.cameras

import android.graphics.Bitmap
import android.util.Log

class CamerasRepository(private val camerasService: CamerasService) {
    suspend fun getImage(href: String): Bitmap {
        return when (val result = camerasService.getCameraImageUrls(href)) {
            is CamerasResult.GetCamerasResult.Success -> {

                val hrefs = parseImageUrls(result.data)

                if (hrefs.isEmpty()) {
                    Log.e("image", "No images found")
                    throw Exception("No images found")
                }

                when (val bitmap = camerasService.getCameraImage(href +"/" + hrefs.last())) {
                    is CamerasResult.Image.Success -> {
                        bitmap.data
                    }

                    is CamerasResult.Image.Failure -> {
                        Log.e("image", "Image fetching error")
                        throw Exception("Image fetching error")
                    }
                }
            }

            is CamerasResult.GetCamerasResult.Failure -> {
                Log.e("image", "Images urls fetching error")
                throw Exception("Images urls fetching error")
            }
        }
    }
}