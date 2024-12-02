package com.tstudioz.fax.fme.feature.cameras

import android.graphics.BitmapFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream

class CamerasService(private val client: OkHttpClient) : CamerasServiceInterface {

    override suspend fun getCameraImageUrls(href: String): CamerasResult.GetCamerasResult {
        val url: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("camerasfiles.dbtouch.com")
            .addPathSegment("images")
            .addPathSegment(href)
            .build()
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""
        response.close()

        if (!response.isSuccessful) {
            return CamerasResult.GetCamerasResult.Failure(Throwable("Failure getCameraImage"))
        }

        return CamerasResult.GetCamerasResult.Success(doc)
    }

    override suspend fun getCameraImage(href: String, imageId:String): CamerasResult.Image {

        val url: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("camerasfiles.dbtouch.com")
            .addPathSegment("images")
            .addPathSegment(href)
            .addPathSegment(imageId)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.byteStream()
        val bufferedInputStream = BufferedInputStream(doc)
        val bitmap = BitmapFactory.decodeStream(bufferedInputStream)
        response.close()

        if (!response.isSuccessful) {
            return CamerasResult.Image.Failure(Throwable("Failure getCameraImage"))
        }

        return CamerasResult.Image.Success(bitmap)
    }
}