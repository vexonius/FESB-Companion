package com.tstudioz.fax.fme.feature.iksica.services

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface IksicaServiceInterface {

    suspend fun getStudentInfo(): NetworkServiceResult.IksicaResult

    suspend fun getReceipts(oib: String): NetworkServiceResult.IksicaResult

    suspend fun getReceipt(url: String): NetworkServiceResult.IksicaResult
}
