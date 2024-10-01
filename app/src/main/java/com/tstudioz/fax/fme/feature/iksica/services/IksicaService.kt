package com.tstudioz.fax.fme.feature.iksica.services

import android.util.Log
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
        val doc = Jsoup.parse(response.body?.string() ?: "")
        sAMLResponse = doc.select("input[name=SAMLResponse]").attr("value")

        val content = doc.selectFirst("p.content_text")?.text()
        val submit = doc.selectFirst("button[type=submit]")?.text()
        val error = doc.selectFirst("div.error")?.text()

        if (content != null && content.contains("Uspješno", true)
            || submit != null && submit.contains("Da, nastavi", true)
        ) {
            return NetworkServiceResult.IksicaResult.Success("Success login")
        }
        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error))
        }
        Log.d("IksicaService", doc.toString())
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

        val error = Jsoup.parse(body).selectFirst(".alert-danger")?.text()

        if (error != null && error.contains("Greška", true)) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable(error.substringAfter("error_outline ")))
        }

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

        //it can happen that there are no receipts in the last 30 days so it returns the start page (https://issp.srce.hr/student)
        // with text under the student link that says
        // "- nema računa u zadnjih 30 dana."

        if (Jsoup.parse(doc).selectFirst("p.text-danger")?.text()?.contains("- nema računa u zadnjih 30 dana.") == true) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacuni: nema računa u zadnjih 30 dana"))
        }

        if (Jsoup.parse(doc).selectFirst("h2")?.text()?.contains("Odaberi nacin prijave u sustav") == true) {
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

        if (Jsoup.parse(doc).selectFirst("h2")?.text()?.contains("Odaberi nacin prijave u sustav") == true) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacun: Not logged in"))
        }

        if (!response.isSuccessful) {
            return NetworkServiceResult.IksicaResult.Failure(Throwable("Failure getRacun"))
        }

        return NetworkServiceResult.IksicaResult.Success(doc)
    }

}
