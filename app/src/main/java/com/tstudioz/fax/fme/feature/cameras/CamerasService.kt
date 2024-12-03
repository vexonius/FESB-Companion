package com.tstudioz.fax.fme.feature.cameras

import android.graphics.BitmapFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream

class CamerasService(private val client: OkHttpClient) : CamerasServiceInterface {

    override suspend fun getCameraImageUrls(path: String): CamerasResult.GetCamerasResult {
        val url: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("camerasfiles.dbtouch.com")
            .addPathSegment("images")
            .addPathSegment(path)
            .build()
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""
        response.close()

        if (!response.isSuccessful) {
            return CamerasResult.GetCamerasResult.Failure
        }

        return CamerasResult.GetCamerasResult.Success(doc)
    }
}