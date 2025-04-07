package com.tstudioz.fax.fme.networking.interceptors

import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.feature.studomat.services.StudomatLoginServiceInterface
import com.tstudioz.fax.fme.networking.cookies.MonsterCookieJar
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

    private val loginMutex = Mutex()
    @Volatile private var ongoingRefresh: CompletableDeferred<Unit>? = null

    private suspend fun refreshSession() {
        if (loginMutex.isLocked) {
            ongoingRefresh?.await()
            return
        }
        val refreshJob = CompletableDeferred<Unit>().also { ongoingRefresh = it }
        loginMutex.withLock {
            try {
                val realmModel = userDao.getUser() ?: throw IllegalStateException("User not found in database")
                val user = User(realmModel.username, realmModel.password)
                with(studomatLoginService) {
                    getSamlRequest()
                    sendSamlResponseToAAIEDU()
                    getSamlResponse(user.email, user.password)
                    sendSAMLToDecrypt()
                    sendSAMLToISVU()
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