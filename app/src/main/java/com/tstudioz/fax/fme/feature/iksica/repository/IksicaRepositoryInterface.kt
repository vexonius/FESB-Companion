package com.tstudioz.fax.fme.feature.iksica.repository

import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.IksicaModel
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult

interface IksicaRepositoryInterface {

    suspend fun getStudentInfo(): Pair<IksicaBalance, StudentData>

    suspend fun getReceipts(oib:String): IksicaResult.ReceiptsResult

    suspend fun getReceipt(url: String): IksicaResult.ReceiptResult

    suspend fun insert(receipts: List<Receipt>)

    suspend fun insert(iksicaBalance: IksicaBalance, studentData: StudentData)

    suspend fun read(): IksicaModel

}
