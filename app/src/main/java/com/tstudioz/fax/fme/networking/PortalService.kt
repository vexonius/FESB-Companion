package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.Result
import com.tstudioz.fax.fme.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.java.KoinJavaComponent.inject


class PortalService : FService {

    private val client: OkHttpClient by inject(OkHttpClient::class.java)

    override suspend fun loginUser(user:User): Flow<Result.LoginResult> = flow {
        val requestBody = FormBody.Builder()
                .add("Username", user.username)
                .add("Password", user.password)
                .add("IsRememberMeChecked", "true")
                .build()

        val request = Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava")
                .post(requestBody)
                .build();

        val response = client.newCall(request).execute()

        if (response.request.url.toString() == "https://korisnik.fesb.unist.hr/") emit(Result.LoginResult.Success(User(user.username, user.password, user.username+"fesb.hr")))
        else emit(Result.LoginResult.Failure(Throwable("Doslo je do pogreske prilikom prijave")))

    }.flowOn(context = Dispatchers.IO)

    override fun logoutUser(): Flow<Result.LogoutResult> {
        TODO("Not yet implemented")
    }


}