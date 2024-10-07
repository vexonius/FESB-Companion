package com.tstudioz.fax.fme.networking.interceptors

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.feature.iksica.services.IksicaLoginServiceInterface
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ISSPLoginInterceptor(
    private val cookieJar: MonsterCookieJar,
    private val iksicaLoginService: IksicaLoginServiceInterface,
    private val userDao: UserDaoInterface
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (!cookieJar.isISSPTokenValid() && request.url.pathSegments.contains("student")) {
            /**
             * Running this as blocking as we're sure that this method will be run inside coroutine
             */
            runBlocking { refreshSession() }
        }

        val response: Response = chain.proceed(request)

        return response
    }

    private suspend fun refreshSession(){
        val realmModel = userDao.getUser()
        val user = User(realmModel.username, realmModel.password)
        iksicaLoginService.getAuthState()
        iksicaLoginService.login(user.email, user.password)
        iksicaLoginService.getAspNetSessionSAML()
    }

}