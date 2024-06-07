package com.tstudioz.fax.fme.feature.menza.service

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MenzaService(private val client: OkHttpClient) : MenzaServiceInterface {

    override suspend fun fetchMenza(url: String): NetworkServiceResult.MenzaResult {

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response: Response = client.newCall(request).execute()
        val data = response.body?.string()

        return if (!response.isSuccessful || data.isNullOrEmpty()) {
            NetworkServiceResult.MenzaResult.Failure(Throwable("Failed to fetch menza details."))
        } else {
            NetworkServiceResult.MenzaResult.Success(data)
        }
    }

}