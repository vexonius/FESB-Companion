package com.tstudioz.fax.fme.feature.login.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.User
import kotlinx.coroutines.flow.Flow
import okhttp3.FormBody
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
                .url("https://korisnik.fesb.unist.hr/prijava")
                .post(requestBody)
                .build()

        val response = client.newCall(request).execute()

        return if (response.request.url.toString() == "https://korisnik.fesb.unist.hr/") {
            NetworkServiceResult.LoginResult.Success(User(username, password))}
        else {
            NetworkServiceResult.LoginResult.Failure(Throwable("Doslo je do pogreske prilikom prijave"))}
    }

    override fun logoutUser(): Flow<NetworkServiceResult.LogoutResult> {
        TODO("Not yet implemented")
    }

}