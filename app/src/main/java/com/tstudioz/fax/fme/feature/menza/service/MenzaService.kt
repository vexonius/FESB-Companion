package com.tstudioz.fax.fme.feature.menza.service

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MenzaService(private val client: OkHttpClient) : MenzaServiceInterface {

    override suspend fun fetchMenza(): NetworkServiceResult.MenzaResult {
        val request: Request = Request.Builder()
            .url("http://sc.dbtouch.com/menu/api.php/?place=fesb_vrh")
            .get()
            .build()

        val response: Response = client.newCall(request).execute()
        val data = response.body?.string()
        val isSuccessful = response.isSuccessful
        response.close()

        return if (isSuccessful || data.isNullOrEmpty()) {
            NetworkServiceResult.MenzaResult.Failure(Throwable("Failed to fetch menza details."))
        } else {
            NetworkServiceResult.MenzaResult.Success(data)
        }
    }

}