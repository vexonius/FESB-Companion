package com.tstudioz.fax.fme.feature.menza.service

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MenzaService(private val client: OkHttpClient) : MenzaServiceInterface {

    override suspend fun fetchMenza(place: String): NetworkServiceResult.MenzaResult {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("sc.dbtouch.com")
            .addPathSegment("menu")
            .addPathSegment("api.php")
            .addQueryParameter("place", place)
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response: Response = client.newCall(request).execute()
        val data = response.body?.string()
        val isSuccessful = response.isSuccessful
        response.close()

        return if (!isSuccessful || data.isNullOrEmpty()) {
            NetworkServiceResult.MenzaResult.Failure(Throwable("Failed to fetch menza details."))
        } else {
            NetworkServiceResult.MenzaResult.Success(data)
        }
    }
}