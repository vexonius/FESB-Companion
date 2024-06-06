package com.tstudioz.fax.fme.feature.attendance.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import okhttp3.FormBody
import okhttp3.FormBody.Builder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Element

class AttendanceService(private val client: OkHttpClient) : AttendanceServiceInterface {

    override suspend fun loginAttendance(user: User): NetworkServiceResult.PrisutnostResult {

        val url :HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("korisnik.fesb.unist.hr")
            .build()

        if (!client.cookieJar.loadForRequest(url).any{it.name == "Fesb.AuthCookie"}) {
            val formData: FormBody = Builder()
                .add("Username", user.username)
                .add("Password", user.password)
                .add("IsRememberMeChecked", "true")
                .build()
            val rq: Request = Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava?returnUrl=https://raspored.fesb.unist.hr")
                .post(formData)
                .build()

            val response = client.newCall(rq).execute()

            if (response.isSuccessful) {
                return NetworkServiceResult.PrisutnostResult.Success("Successfully logged in")
            }
            return NetworkServiceResult.PrisutnostResult.Failure(Throwable("Failed to login"))
        }
        return NetworkServiceResult.PrisutnostResult.Success("Already logged in")
    }

    override suspend fun fetchAttendance(user: User): NetworkServiceResult.PrisutnostResult {
        val request: Request = Request.Builder()
            .url("https://raspored.fesb.unist.hr/part/prisutnost/opcenito/tablica")
            .get()
            .build()
        val response1 = client.newCall(request).execute()

        return if (response1.isSuccessful) {
            NetworkServiceResult.PrisutnostResult.Success(response1.body?.string() ?: "")
        } else {
            NetworkServiceResult.PrisutnostResult.Failure(Throwable("Failed to fetch attendance"))
        }

    }

    override suspend fun getDetailedPrisutnost(element: Element): NetworkServiceResult.PrisutnostResult {
        val request: Request = Request.Builder()
            .url("https://raspored.fesb.unist.hr${element.attr("href")}")
            .get()
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            return NetworkServiceResult.PrisutnostResult.Success(response.body?.string() ?: "")
        }
        return NetworkServiceResult.PrisutnostResult.Failure(Throwable("Failed to fetch attendance details"))
    }
}