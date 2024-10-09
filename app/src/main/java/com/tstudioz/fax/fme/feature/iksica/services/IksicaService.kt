package com.tstudioz.fax.fme.feature.iksica.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class IksicaService(private val client: OkHttpClient) : IksicaServiceInterface {

    override suspend fun getStudentInfo(): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr/student")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        response.close()

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getAspNetSessionSAML"))
        }

        return NetworkServiceResult.IksicaResult.Success(body)
    }

    override suspend fun getReceipts(oib: String): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr/student/studentracuni?oib=$oib")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""
        response.close()

        //it can happen that there are no receipts in the last 30 days so it returns the start page (https://issp.srce.hr/student)
        // with text under the student link that says
        // "- nema računa u zadnjih 30 dana."

        if (Jsoup.parse(doc).selectFirst("p.text-danger")?.text()
                ?.contains("- nema računa u zadnjih 30 dana.") == true
        ) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacuni: nema računa u zadnjih 30 dana"))
        }

        if (Jsoup.parse(doc).selectFirst("h2")?.text()
                ?.contains("Odaberi nacin prijave u sustav") == true
        ) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacuni: Not logged in"))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacuni"))
        }

        return NetworkServiceResult.IksicaResult.Success(doc)
    }

    override suspend fun getReceipt(url: String): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr$url")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""
        response.close()

        if (Jsoup.parse(doc).selectFirst("h2")?.text()
                ?.contains("Odaberi nacin prijave u sustav") == true
        ) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacun: Not logged in"))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacun"))
        }

        return NetworkServiceResult.IksicaResult.Success(doc)
    }

    companion object {
        private const val SCHEME = "https"

        val targetUrl = HttpUrl.Builder()
            .scheme(SCHEME)
            .host("issp.srce.hr")
            .build()

    }
}


