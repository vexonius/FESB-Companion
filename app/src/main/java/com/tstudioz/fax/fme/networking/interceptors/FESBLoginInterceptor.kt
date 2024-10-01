package com.tstudioz.fax.fme.networking.interceptors

import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal class FESBLoginInterceptor(
    private val cookieJar: MonsterCookieJar,
    private val userService: UserServiceInterface,
    private val userDao: UserDaoInterface
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        cookieJar.clear()
        if (!cookieJar.isFESBTokenValid() && request.url.host.contains("fesb.unist.hr")) {
            /**
             * Running this as blocking as we're sure that this method will be run inside coroutine
             */
            runBlocking { refreshSession() }
        }

        val response: Response = chain.proceed(request)

        return response
    }

    private suspend fun refreshSession() {
        val user = userDao.getUser()
        userService.loginUser(user.username, user.password)
    }

}