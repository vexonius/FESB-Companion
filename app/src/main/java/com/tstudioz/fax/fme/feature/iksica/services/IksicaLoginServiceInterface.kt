package com.tstudioz.fax.fme.feature.iksica.services

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface IksicaLoginServiceInterface {

    suspend fun getAuthState(): NetworkServiceResult.IksicaResult

    suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult

    suspend fun getAspNetSessionSAML(): NetworkServiceResult.IksicaResult

}