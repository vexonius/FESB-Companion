package com.tstudioz.fax.fme.feature.login.services

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class UserService(private val client: OkHttpClient) : UserServiceInterface {

    override suspend fun loginUser(username: String, password: String): NetworkServiceResult.LoginResult {
        val requestBody = FormBody.Builder()
            .add("Username", username)
            .add("Password", password)
            .add("IsRememberMeChecked", "true")
            .build()

        val request = Request.Builder()
            .url(loginUrl)
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        val url = response.request.url

        response.close()

        return if (url == targetUrl) {
            NetworkServiceResult.LoginResult.Success(User(username, password))}
        else {
            NetworkServiceResult.LoginResult.Failure(Throwable("Error during login"))}
    }

    companion object {
        private const val SCHEME = "https"

        val targetUrl = HttpUrl.Builder()
            .scheme(SCHEME)
            .host("korisnik.fesb.unist.hr")
            .build()

        val loginUrl = HttpUrl.Builder()
            .scheme(SCHEME)
            .host("korisnik.fesb.unist.hr")
            .addPathSegment("prijava")
            .build()
    }

}