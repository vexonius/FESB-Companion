package com.tstudioz.fax.fme.feature.iksica.repository

import com.tstudioz.fax.fme.feature.iksica.models.IksicaData
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptRoom
import com.tstudioz.fax.fme.feature.iksica.models.StudentData

interface IksicaRepositoryInterface {

    suspend fun getCardDataAndReceipts(): IksicaResult.CardAndReceiptsResult

    suspend fun getReceipt(url: String): IksicaResult.ReceiptResult

    suspend fun insert(model: StudentData)

    suspend fun insert(model: List<Receipt>)

    suspend fun getCache(): IksicaData?

}
