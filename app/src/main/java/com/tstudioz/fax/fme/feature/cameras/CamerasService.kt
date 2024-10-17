package com.tstudioz.fax.fme.feature.cameras

import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream

class CamerasService(private val client: OkHttpClient) {
    suspend fun getCameraImageUrls(href: String): CamerasResult.GetCamerasResult {
        val request = Request.Builder()
            .url("https://camerasfiles.dbtouch.com/images/$href")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            return CamerasResult.GetCamerasResult.Failure(Throwable("Failure getCameraImage"))
        }

        return CamerasResult.GetCamerasResult.Success(doc)
    }

    suspend fun getCameraImage(href: String): CamerasResult.Image {
        val request = Request.Builder()
            .url("https://camerasfiles.dbtouch.com/images/$href")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.byteStream()
        val bufferedInputStream = BufferedInputStream(doc)
        val bitmap = BitmapFactory.decodeStream(bufferedInputStream)

        if (!response.isSuccessful) {
            return CamerasResult.Image.Failure(Throwable("Failure getCameraImage"))
        }

        return CamerasResult.Image.Success(bitmap)
    }
}