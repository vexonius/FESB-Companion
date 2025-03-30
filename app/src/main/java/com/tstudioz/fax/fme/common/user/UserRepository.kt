package com.tstudioz.fax.fme.common.user

import android.content.SharedPreferences
import com.tstudioz.fax.fme.common.user.models.User
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
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
    private val sessionDelegate: SessionDelegateInterface
) : UserRepositoryInterface {

    override suspend fun attemptLogin(user: User): UserRepositoryResult.LoginResult {
        return when (val result = userService.loginUser(user)) {
            is NetworkServiceResult.LoginResult.Success -> {
                val user = result.data
                userDao.insert(user.toRoomModel())
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
        userDao.deleteAllUserData()
        sharedPreferences[SPKey.LOGGED_IN] = false
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
