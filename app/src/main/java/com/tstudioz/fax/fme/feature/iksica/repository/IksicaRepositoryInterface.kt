package com.tstudioz.fax.fme.feature.iksica.repository

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.IksicaResult
import com.tstudioz.fax.fme.models.NetworkServiceResult

interface IksicaRepositoryInterface {


    suspend fun getAuthState(): NetworkServiceResult.IksicaResult

    suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult

    suspend fun getAspNetSessionSAML(): Pair<IksicaBalance, StudentDataIksica>

    suspend fun getReceipts(oib:String): IksicaResult.ReceiptsResult

    suspend fun getReceipt(url: String): IksicaResult.ReceiptResult

    suspend fun insert(receipts: List<Receipt>)

    suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica)

    suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?>

}
