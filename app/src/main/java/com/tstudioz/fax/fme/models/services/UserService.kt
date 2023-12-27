package com.tstudioz.fax.fme.models.services

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.interfaces.UserInterface
import kotlinx.coroutines.flow.Flow
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.java.KoinJavaComponent.inject


class UserService : UserInterface {

    private val client: OkHttpClient by inject(OkHttpClient::class.java)

    override suspend fun loginUser(user: User): Result.LoginResult {
        val requestBody = FormBody.Builder()
                .add("Username", user.username)
                .add("Password", user.password)
                .add("IsRememberMeChecked", "true")
                .build()

        val request = Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava")
                .post(requestBody)
                .build()

        val response = client.newCall(request).execute()

        return if (response.request.url.toString() == "https://korisnik.fesb.unist.hr/") {
            Result.LoginResult.Success(User(user.username, user.password, user.username+"fesb.hr"))}
        else {
            Result.LoginResult.Failure(Throwable("Doslo je do pogreske prilikom prijave"))}
    }

    override fun logoutUser(): Flow<Result.LogoutResult> {
        TODO("Not yet implemented")
    }


}