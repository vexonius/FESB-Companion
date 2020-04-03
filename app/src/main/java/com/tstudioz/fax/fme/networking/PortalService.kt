package com.tstudioz.fax.fme.networking

import com.tstudioz.fax.fme.models.PortalResult
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

    override suspend fun loginUser(): Flow<PortalResult.LoginResult> = flow {
        val requestBody = FormBody.Builder()
                .add("Username", "temer00")
                .add("Password", "Jc72028N")
                .add("IsRememberMeChecked", "true")
                .build()

        val request = Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava?returnURL=https://elearning.fesb" +
                             ".unist.hr/login/index.php")
                .post(requestBody)
                .build();

        val response = client.newCall(request).execute()

        if (response.isSuccessful)  emit(PortalResult.LoginResult.Success(User("temi", "Tino", "temi@fesb.hr")))
        else emit(PortalResult.LoginResult.Failure(Throwable("Doslo je do pogreske prilikom prijave")))

    }.flowOn(context = Dispatchers.IO)

    override fun logoutUser(): Flow<PortalResult.LogoutResult> {
        TODO("Not yet implemented")
    }


}