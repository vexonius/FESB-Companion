package com.tstudioz.fax.fme.feature.studomat.services

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class StudomatService(private val client: OkHttpClient) {

    fun loadCookieToWebview(webView: WebView) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        val cookie = "JSESSIONID=" + client.cookieJar.loadForRequest(targetUrl).find { it.name == "JSESSIONID" }?.value

        cookieManager.setCookie("https://www.isvu.hr/studomat/hr/", cookie)

        cookieManager.flush()
        webView.loadUrl("https://www.isvu.hr/studomat/hr/ispit/ponudapredmetazaprijavuispita")
    }

    fun getStudomatData(): String {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/index")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        val isSuccessful = response.isSuccessful
        response.close()

        return if (isSuccessful && Jsoup.parse(body).title() == "Studomat - Prijava") {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            (client.cookieJar as MonsterCookieJar).clearCookiesForUrl(request.url)
            throw Throwable("Not logged in!")
        } else if (isSuccessful) {
            Log.d("StudomatService", "getStudomatData: ${body.substring(0, 100)}")
            body
        } else {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            throw Throwable("Couldn't get Studomat data!")
        }
    }

    fun getYearNames(): NetworkServiceResult.StudomatResult {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/studiranje/upisanegodine")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        val isSuccessful = response.isSuccessful
        response.close()

        return if (isSuccessful && Jsoup.parse(body).title() == "Studomat - Prijava") {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            (client.cookieJar as MonsterCookieJar).clearCookiesForUrl(request.url)
            throw Throwable("Not logged in!")
        } else if (body != "") {
            Log.d("StudomatService", "getUpisaneGodine: ${body.substring(0, 100)}")
            NetworkServiceResult.StudomatResult.Success(body)
        } else {
            Log.d("StudomatService", "getUpisaneGodine: Couldn't get upisane godine data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get upisane godine data!"))
        }
    }

    fun getYearSubjects(href: String): NetworkServiceResult.StudomatResult {
        val request = Request.Builder()
            .url("https://www.isvu.hr$href")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        val isSuccessful = response.isSuccessful
        response.close()

        return if (isSuccessful && Jsoup.parse(body).title() == "Studomat - Prijava") {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            (client.cookieJar as MonsterCookieJar).clearCookiesForUrl(request.url)
            throw Throwable("Not logged in!")
        } else if (isSuccessful) {
            Log.d("StudomatService", "getTrenutnuGodinuData: ${body.substring(0, 100)}")
            NetworkServiceResult.StudomatResult.Success(body)
        } else {
            Log.d("StudomatService", "getTrenutnuGodinuData: Couldn't get current year data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get current year data!"))
        }
    }

    companion object {
        private const val SCHEME = "https"

        val targetUrl = HttpUrl.Builder()
            .scheme(SCHEME)
            .host("www.isvu.hr")
            .build()

    }
}