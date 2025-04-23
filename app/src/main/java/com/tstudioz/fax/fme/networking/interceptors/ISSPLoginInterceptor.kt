package com.tstudioz.fax.fme.networking.interceptors

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.feature.iksica.services.IksicaLoginServiceInterface
import com.tstudioz.fax.fme.feature.login.dao.UserDao
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ISSPLoginInterceptor(
    private val cookieJar: MonsterCookieJar,
    private val iksicaLoginService: IksicaLoginServiceInterface,
    private val userDao: UserDao
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        if (!cookieJar.isISSPTokenValid() && request.url.pathSegments.contains("student")) {
            runBlocking { refreshSession() }
        }

        val response: Response = chain.proceed(request)

        return response
    }

    private val loginMutex = Mutex()
    @Volatile
    private var ongoingRefresh: CompletableDeferred<Unit>? = null

    private suspend fun refreshSession() {
        if (loginMutex.isLocked) {
            ongoingRefresh?.await()
            return
        }
        val refreshJob = CompletableDeferred<Unit>().also { ongoingRefresh = it }
        loginMutex.withLock {
            try {
                val user = User(userDao.getUser())
                with(iksicaLoginService) {
                    getAuthState()
                    login(user.email, user.password)
                    getAspNetSessionSAML()
                }
                refreshJob.complete(Unit)
            } catch (e: Exception) {
                refreshJob.completeExceptionally(e)
                throw e
            } finally {
                ongoingRefresh = null
            }
        }
    }

}