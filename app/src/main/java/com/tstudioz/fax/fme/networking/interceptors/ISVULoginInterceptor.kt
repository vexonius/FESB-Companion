package com.tstudioz.fax.fme.networking.interceptors

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.feature.studomat.services.StudomatLoginServiceInterface
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ISVULoginInterceptor(
    private val cookieJar: MonsterCookieJar,
    private val studomatLoginService: StudomatLoginServiceInterface,
    private val userDao: UserDaoInterface
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (!cookieJar.isISVUTokenValid()) {
            runBlocking { refreshSession() }
        }

        return chain.proceed(request)
    }

    private suspend fun refreshSession(){
        val realmModel = userDao.getUser()
        val user = User(realmModel)

        studomatLoginService.getSamlRequest()
        studomatLoginService.sendSamlResponseToAAIEDU()
        studomatLoginService.getSamlResponse(user.email, user.password)
        studomatLoginService.sendSAMLToDecrypt()
        studomatLoginService.sendSAMLToISVU()
    }

}