package com.tstudioz.fax.fme.common.user

import android.content.SharedPreferences
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
import com.tstudioz.fax.fme.database.models.UserRealm
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.session.SessionDelegateInterface
import com.tstudioz.fax.fme.routing.AppRouter
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey
import org.koin.java.KoinJavaComponent.inject

class UserRepository(
    private val userService: UserServiceInterface,
    private val userDao: UserDaoInterface,
    private val sharedPreferences: SharedPreferences,
    private val sessionDelegate: SessionDelegateInterface
) : UserRepositoryInterface {

    private val router by inject<AppRouter>(AppRouter::class.java)

    override suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult {
        return when (val result = userService.loginUser(username, password)) {
            is NetworkServiceResult.LoginResult.Success -> {
                val user = result.data
                userDao.insert(user.toRealmModel())
                sharedPreferences[SPKey.LOGGED_IN] = true

                UserRepositoryResult.LoginResult.Success(result.data)
            }

            is NetworkServiceResult.LoginResult.Failure -> {
                UserRepositoryResult.LoginResult.Failure(Throwable("User Login failed!"))
            }
        }
    }

    override suspend fun getCurrentUserName(): String {
        when (val model = userDao.getUser()) {
            null -> {
                logout()
                throw IllegalStateException("User not found in database")
            }

            else -> return model.username
        }
    }

    override suspend fun getCurrentUser(): UserRealm {
        when (val model = userDao.getUser()) {
            null -> {
                logout()
                throw IllegalStateException("User not found in database")
            }

            else -> return model
        }
    }

    override suspend fun deleteAllUserData() {
        sessionDelegate.clearSession()
        userDao.deleteAllUserData()
        sharedPreferences[SPKey.LOGGED_IN] = false
    }

    suspend fun logout() {
        deleteAllUserData()
        router.routeToLogin()
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
