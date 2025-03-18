package com.tstudioz.fax.fme.feature.cameras

import android.util.Log
import okhttp3.HttpUrl
import org.jsoup.Jsoup

class CamerasRepository(private val camerasService: CamerasServiceInterface) : CamerasRepositoryInterface {

    override suspend fun getImages(path: String): HttpUrl? {
        return when (val result = camerasService.getCameraImageUrls(path)) {
            is CamerasResult.GetCamerasResult.Success -> {

                val hrefs = parseImageUrls(result.data)
                if (hrefs.isEmpty()) {
                    Log.e("image", "No images found")
                    return null
                }
                HttpUrl.Builder()
                    .scheme("https")
                    .host("camerasfiles.dbtouch.com")
                    .addPathSegment("images")
                    .addPathSegment(path)
                    .addPathSegment(hrefs.last())
                    .build()
            }

            is CamerasResult.GetCamerasResult.Failure -> {
                Log.e("image", "Images urls fetching error")
                throw Exception("Images urls fetching error")
            }
        }
    }

    private fun parseImageUrls(body: String): List<String> {
        return Jsoup.parse(body).select("a")
            .map { it.attr("href") }
            .filter { !it.contains("medium") }
            .filter { !it.contains("small") }
            .filter { !it.contains("../") }
    }
}