package com.tstudioz.fax.fme.feature.iksica.repository

import com.tstudioz.fax.fme.feature.iksica.models.IksicaModel
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataRealm
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.models.StudentData

interface IksicaRepositoryInterface {

    suspend fun getCardDataAndReceipts(): IksicaResult.CardAndReceiptsResult

    suspend fun getReceipt(url: String): IksicaResult.ReceiptResult

    suspend fun insert(model: StudentData)

    suspend fun getCache(): StudentData?

}
