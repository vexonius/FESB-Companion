package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.IksicaSaldo
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.models.NetworkServiceResult

interface IksicaRepositoryInterface {

    suspend fun getAuthState(): NetworkServiceResult.IksicaResult

    suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult

    suspend fun getAspNetSessionSAML(): Pair<IksicaSaldo, StudentDataIksica>

    suspend fun getRacuni(): List<Receipt>

    suspend fun getRacun(url: String): MutableList<ReceiptItem>


}
