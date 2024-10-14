package com.tstudioz.fax.fme.feature.studomat.services

import android.util.Log
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class StudomatService(private val client: OkHttpClient) {

    fun getStudomatData(): String {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/index")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""

        return if (Jsoup.parse(body).title() == "Studomat - Prijava") {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            throw Throwable("Not logged in!")
        } else if (response.code == 200) {
            Log.d("StudomatService", "getStudomatData: ${body.substring(0, 100)}")
            body
        } else {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            throw Throwable("Couldn't get Studomat data!")
        }
    }

    fun getYears(): NetworkServiceResult.StudomatResult {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/studiranje/upisanegodine")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""

        return if (Jsoup.parse(body).title() == "Studomat - Prijava") {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Not logged in!"))
        } else if (body != "") {
            Log.d("StudomatService", "getUpisaneGodine: ${body.substring(0, 100)}")
            NetworkServiceResult.StudomatResult.Success(body)
        } else {
            Log.d("StudomatService", "getUpisaneGodine: Couldn't get upisane godine data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get upisane godine data!"))
        }
    }

    fun getChosenYear(href: String): NetworkServiceResult.StudomatResult {
        val request = Request.Builder()
            .url("https://www.isvu.hr$href")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        return if (Jsoup.parse(body).title() == "Studomat - Prijava") {
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Not logged in!"))
        } else if (response.isSuccessful) {
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