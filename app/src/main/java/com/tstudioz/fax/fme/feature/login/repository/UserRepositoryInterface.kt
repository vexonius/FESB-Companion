package com.tstudioz.fax.fme.feature.login.repository

import com.tstudioz.fax.fme.database.models.Meni
import com.tstudioz.fax.fme.database.models.NoteRealm
import com.tstudioz.fax.fme.feature.login.repository.models.UserRepositoryResult
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.home.WeatherFeature

interface UserRepositoryInterface {

    suspend fun attemptLogin(username: String, password: String): UserRepositoryResult.LoginResult
    suspend fun fetchWeatherDetails(): WeatherFeature?
    suspend fun fetchMenzaDetails(url: String): MenzaResult
    suspend fun readMenza(): List<Meni>
}
