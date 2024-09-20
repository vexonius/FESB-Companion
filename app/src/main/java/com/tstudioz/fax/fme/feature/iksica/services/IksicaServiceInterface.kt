package com.tstudioz.fax.fme.feature.iksica.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.Request
import org.jsoup.Jsoup

interface IksicaServiceInterface {

    suspend fun getAuthState(): NetworkServiceResult.IksicaResult

    suspend fun login( email: String, password: String): NetworkServiceResult.IksicaResult

    suspend fun getAspNetSessionSAML() : NetworkServiceResult.IksicaResult

    suspend fun getReceipts(oib: String) :NetworkServiceResult.IksicaResult

    suspend fun getRacun(url: String) : NetworkServiceResult.IksicaResult
}
