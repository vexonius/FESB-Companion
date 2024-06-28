package com.tstudioz.fax.fme.feature.iksica.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup


class IksicaService(private val client: OkHttpClient) : IksicaServiceInterface {

    private var currentUrl: HttpUrl? = null
    private var authState = ""
    private var sAMLResponse = ""

    override suspend fun getAuthState(): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr/auth/loginaai")
            .build()

        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        currentUrl = response.request.url
        authState = doc.selectFirst("form.login-form")?.attr("action")?.split("AuthState=")?.get(1) ?: ""
        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failed to get AuthState"))
        }

        return NetworkServiceResult.IksicaResult.Success("Success")
    }

    override suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult {
        val formBody = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .add("AuthState", authState)
            .add("Submit", "")
            .build()
        val request = Request.Builder()
            .url(currentUrl!!)
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc1 = Jsoup.parse(response.body?.string() ?: "")
        sAMLResponse = doc1.select("input[name=SAMLResponse]").attr("value")

        val content = doc1.selectFirst("p.content_text")?.text()
        val submit = doc1.selectFirst("button[type=submit]")?.text()
        val error = doc1.selectFirst("div.error")?.text()

        if (content != null && content.contains("Uspješno", true)
            || submit != null && submit.contains("Da, nastavi", true)
        ) {
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }
        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error))
        }
        return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure login"))
    }

    override suspend fun getAspNetSessionSAML(): NetworkServiceResult.IksicaResult {
        val formBody = FormBody.Builder()
            .add("SAMLResponse", sAMLResponse)
            .add("Submit", "")
            .build()
        val request = Request.Builder()
            .url("https://issp.srce.hr/auth/prijavakorisnika")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getAspNetSessionSAML"))
        }

        return NetworkServiceResult.IksicaResult.Success(body)
    }

    override suspend fun getRacuni(): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr/student/studentracuni?oib=34106510630")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""

        val h2 = Jsoup.parse(doc).selectFirst("h2")?.text()
        val test = response.headers.size

        if (h2?.contains("Odaberi nacin prijave u sustav") == true) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacuni: Not logged in"))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacuni"))
        }


        return NetworkServiceResult.IksicaResult.Success(doc)
    }

    override suspend fun getRacun(url: String): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr$url")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string() ?: ""

        val h2 = Jsoup.parse(doc).selectFirst("h2")?.text()

        if (h2?.contains("Odaberi nacin prijave u sustav") == true) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacun: Not logged in"))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacun"))
        }

        return NetworkServiceResult.IksicaResult.Success(doc)
    }

}
