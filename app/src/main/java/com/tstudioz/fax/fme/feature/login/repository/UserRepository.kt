package com.tstudioz.fax.fme.feature.login.repository

import android.content.SharedPreferences
import android.util.Log
import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.feature.login.services.UserServiceInterface
import com.tstudioz.fax.fme.feature.menza.service.MenzaServiceInterface
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.dao.interfaces.MenzaDaoInterface
import com.tstudioz.fax.fme.feature.menza.parseMenza
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.data.UserDaoInterface
import com.tstudioz.fax.fme.feature.weather.WeatherNetworkInterface
import com.tstudioz.fax.fme.models.util.PreferenceHelper.set
import com.tstudioz.fax.fme.models.util.SPKey
import com.tstudioz.fax.fme.feature.weather.WeatherFeature
import kotlinx.serialization.json.Json

class UserRepository(
    private val userService: UserServiceInterface,
    private val weatherNetworkService: WeatherNetworkInterface,
    private val menzaNetworkService: MenzaServiceInterface,
    private val menzaDao: MenzaDaoInterface,
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
                return UserRepositoryResult.LoginResult.Failure(Throwable("User Login failed!"))
            }
        }
    }

    override suspend fun fetchWeatherDetails(url: String): WeatherFeature? {
        return when (val result = weatherNetworkService.fetchWeatherDetails(url)) {
            is NetworkServiceResult.WeatherResult.Success -> {
                val test = Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
                test.decodeFromString<WeatherFeature>(result.data)
            }

            is NetworkServiceResult.WeatherResult.Failure -> {
                Log.e(TAG, "Timetable fetching error")
                null
            }
        }
    }

    override suspend fun fetchMenzaDetails(url: String): MenzaResult {
        return when (val result = menzaNetworkService.fetchMenza(url)) {
            is NetworkServiceResult.MenzaResult.Success -> {
                val parsed = parseMenza(result.data)
                if (parsed != null) {
                    menzaDao.insert(parsed)
                    MenzaResult.Success(parsed)
                } else {
                    Log.e(TAG, "Menies parsing error")
                    MenzaResult.Failure(Throwable("Menies parsing error"))
                }
            }

            is NetworkServiceResult.MenzaResult.Failure -> {
                Log.e(TAG, "Menies fetching error")
                MenzaResult.Failure(Throwable("Menies fetching error"))
            }
        }
    }

    override suspend fun readMenza(): List<Meni> {
        return menzaDao.getCachedMenza()
    }

    companion object {
        private val TAG = this.javaClass.canonicalName
    }

}
