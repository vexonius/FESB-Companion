package com.tstudioz.fax.fme.common.user

import android.content.SharedPreferences
import com.tstudioz.fax.fme.common.user.models.UserRepositoryResult
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.login.dao.UserDaoInterface
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey

class UserRepository(
    private val userService: UserServiceInterface,
    private val userDao: UserDaoInterface,
    private val sharedPreferences: SharedPreferences
) : UserRepositoryInterface {

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

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
