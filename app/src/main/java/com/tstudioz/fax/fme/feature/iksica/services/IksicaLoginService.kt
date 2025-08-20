package com.tstudioz.fax.fme.feature.iksica.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class IksicaLoginService(
    private val client: OkHttpClient,
    private var currentUrl: HttpUrl?,
    private var authState: String,
    private var sAMLResponse: String
) : IksicaLoginServiceInterface {

    private var successfulIsspLoginAlready: Boolean = false
    private var successfulAaieduLoginAlready = false

    override suspend fun getAuthState(): NetworkServiceResult.IksicaResult {
        val request = Request.Builder()
            .url("https://issp.srce.hr/auth/loginaai")
            .build()

        val response = client.newCall(request).execute()
        val success = response.isSuccessful
        val body = response.body?.string() ?: ""
        val doc = Jsoup.parse(body)

        successfulIsspLoginAlready =  doc.selectFirst("a[aria-label='povratak u sustav']")
            ?.text()?.contains("Povratak u sustav", true) == true
        successfulAaieduLoginAlready = doc.selectFirst("div[class=onscript-msg]")
            ?.text()?.contains("Uspješno ste autenticirani.", true) == true

        response.close()

        if (successfulAaieduLoginAlready) {
            doc.select("input[name=SAMLResponse]").forEach { sAMLResponse = it.attr("value") }
            return NetworkServiceResult.IksicaResult.Success("Success early login to AAIEDU")
        }
        if (successfulIsspLoginAlready) {
            return NetworkServiceResult.IksicaResult.Success("Success early login to ISSP")
        }

        if (!success || body.isEmpty()) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failed to get AuthState"))
        }

        authState = response.request.url.queryParameter("AuthState") ?: ""
        currentUrl = response.request.url

        return NetworkServiceResult.IksicaResult.Success("Success")
    }

    override suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult {
        if (successfulAaieduLoginAlready || successfulIsspLoginAlready) {
            successfulAaieduLoginAlready = false
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }

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
        val doc = Jsoup.parse(response.body?.string() ?: "")
        sAMLResponse = doc.select("input[name=SAMLResponse]").attr("value")

        val content = doc.selectFirst("div.onscript-msg")?.text()
        val submit = doc.selectFirst("button[type=submit]")?.text()
        val error = doc.selectFirst("div.error")?.text()

        if (content != null && content.contains("Uspješno", true)
            || submit != null && submit.contains("Nastavak", true)
        ) {
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }

        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error))
        }

        return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure login"))
    }

    override suspend fun getAspNetSessionSAML(): NetworkServiceResult.IksicaResult {
        if (successfulIsspLoginAlready) {
            successfulIsspLoginAlready = false
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }
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
        response.close()

        val error = Jsoup.parse(body).selectFirst(".alert-danger")?.text()

        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error.substringAfter("error_outline ")))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getAspNetSessionSAML"))
        }

        return NetworkServiceResult.IksicaResult.Success("Success")
    }
}