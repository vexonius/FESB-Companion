package com.tstudioz.fax.fme.common.user

import android.content.SharedPreferences
import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
import com.tstudioz.fax.fme.common.user.models.UserRoom
import com.tstudioz.fax.fme.database.AppDatabase
import com.tstudioz.fax.fme.feature.login.dao.UserDao
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.networking.session.SessionDelegateInterface
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey

class UserRepository(
    private val userService: UserServiceInterface,
    private val userDao: UserDao,
    private val sharedPreferences: SharedPreferences,
    private val sessionDelegate: SessionDelegateInterface,
    private val appDatabase: AppDatabase,
) : UserRepositoryInterface {

    override suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult {
        return when (val result = userService.loginUser(username, password)) {
            is NetworkServiceResult.LoginResult.Success -> {
                val user = result.data
                userDao.insert(UserRoom(user))
                sharedPreferences[SPKey.LOGGED_IN] = true

                UserRepositoryResult.LoginResult.Success(result.data)
            }

            is NetworkServiceResult.LoginResult.Failure -> {
                UserRepositoryResult.LoginResult.Failure(Throwable("User Login failed!"))
            }
        }
    }

    override suspend fun getCurrentUserName(): String {
        return userDao.getUser().username
    }

    override suspend fun getCurrentUser(): User {
        return User(userDao.getUser())
    }

    override suspend fun deleteAllUserData() {
        sessionDelegate.clearSession()
        appDatabase.clearAllTables()
        sharedPreferences[SPKey.LOGGED_IN] = false
    }
}
