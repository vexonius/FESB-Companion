package com.tstudioz.fax.fme.feature.menza.service

import com.tstudioz.fax.fme.feature.menza.CamerasResult
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class CamerasService(private val client: OkHttpClient) : CamerasServiceInterface {

    override suspend fun getCameraImageUrls(path: String): CamerasResult.GetCamerasResult {
        val url: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("camerasfiles.dbtouch.com")
            .addPathSegment("images")
            .addPathSegments("$path/") //adding a / at the end, otherwise it will redirect to the page that has a / (so this saves a network call)
            .build()
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            return CamerasResult.GetCamerasResult.Failure
        }

        val doc = response.body?.string() ?: ""
        response.close()

        return CamerasResult.GetCamerasResult.Success(doc)
    }
}