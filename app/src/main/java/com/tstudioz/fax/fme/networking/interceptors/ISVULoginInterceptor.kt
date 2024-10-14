package com.tstudioz.fax.fme.networking.interceptors

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.feature.studomat.di.cok1
import com.tstudioz.fax.fme.feature.studomat.services.StudomatLoginService
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ISVULoginInterceptor(
    private val cookieJar: MonsterCookieJar,
    private val studomatLoginService: StudomatLoginService,
    private val userDao: UserDaoInterface
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (!cookieJar.isISVUTokenValid()) {
            /**
             * Running this as blocking as we're sure that this method will be run inside coroutine
             */
        }
        runBlocking { refreshSession() }

        val response: Response = chain.proceed(request)

        return response
    }

    private suspend fun refreshSession(){
        val realmModel = userDao.getUser()
        val user = User(realmModel.username, realmModel.password)

        studomatLoginService.getSamlRequest()
        studomatLoginService.sendSamlResponseToAAIEDU()
        studomatLoginService.getSamlResponse(user.email, user.password)
        studomatLoginService.sendSAMLToDecrypt()
        studomatLoginService.sendSAMLToISVU()
        studomatLoginService.getStudomatData()
    }

}