package com.tstudioz.fax.fme.feature.login.repository

import android.content.SharedPreferences
import android.util.Log
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.UserDaoInterface
import com.tstudioz.fax.fme.models.interfaces.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.util.PreferenceHelper.set
import com.tstudioz.fax.fme.models.util.SPKey
import com.tstudioz.fax.fme.models.util.parseWeatherDetails
import com.tstudioz.fax.fme.weather.Current

class UserRepository(
    private val userService: UserServiceInterface,
    private val weatherNetworkService: WeatherNetworkInterface,
    private val userDao: UserDaoInterface,
    private val sharedPreferences: SharedPreferences
    ) : UserRepositoryInterface {

    override suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult {
        when (val result = userService.loginUser(username, password)) {
            is NetworkServiceResult.LoginResult.Success -> {
                val user = result.data
                userDao.insert(user.toRealmModel())
                sharedPreferences[SPKey.LOGGED_IN] = true

                return UserRepositoryResult.LoginResult.Success(result.data)
            }
            is NetworkServiceResult.LoginResult.Failure -> {
                Log.e(TAG, "User Login Failed!")
                throw Exception("User Login Failed!")
            }
        }
    }

    override suspend fun fetchWeatherDetails(url : String): Current? {
        return when(val result = weatherNetworkService.fetchWeatherDetails(url)){
            is NetworkServiceResult.WeatherResult.Success -> parseWeatherDetails(result.data)
            is NetworkServiceResult.WeatherResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                //throw Exception("Timetable fetching error")
                null
            }
        }
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
