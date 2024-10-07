package com.tstudioz.fax.fme.feature.iksica.repository

import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.IksicaResult

interface IksicaRepositoryInterface {

    suspend fun getStudentInfo(): Pair<IksicaBalance, StudentDataIksica>

    suspend fun getReceipts(oib:String): IksicaResult.ReceiptsResult

    suspend fun getReceipt(url: String): IksicaResult.ReceiptResult

    suspend fun insert(receipts: List<Receipt>)

    suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica)

    suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?>

}
