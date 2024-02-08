package com.tstudioz.fax.fme.feature.login.repository

import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.weather.Current

interface UserRepositoryInterface {

    suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult
    suspend fun fetchWeatherDetails(url: String): Current?

}