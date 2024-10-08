package com.tstudioz.fax.fme.feature.iksica.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.feature.iksica.models.IksicaModel
import com.tstudioz.fax.fme.feature.iksica.parseDetaljeRacuna
import com.tstudioz.fax.fme.feature.iksica.parseRacuni
import com.tstudioz.fax.fme.feature.iksica.parseStudentInfo
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult

class IksicaRepository(
    private val iksicaService: IksicaServiceInterface,
    private val iksicaDao: IksicaDaoInterface,
) : IksicaRepositoryInterface {

    override suspend fun getStudentInfo(): Pair<IksicaBalance, StudentData> {
        when (val result = iksicaService.getStudentInfo()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                val data = parseStudentInfo(result.data)
                insert(data.first, data.second)

                return data
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, result.throwable.message ?: "AspNetSessionSAML fetching error")
                throw Exception(result.throwable.message ?: "AspNetSessionSAML fetching error")
            }
        }
    }

    override suspend fun getReceipts(oib: String): IksicaResult.ReceiptsResult {
        when (val result = iksicaService.getReceipts(oib)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                val receiptsList = parseRacuni(result.data)
                insert(receiptsList)

                return IksicaResult.ReceiptsResult.Success(receiptsList)
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "Receipts fetching error")
                if (result.throwable.message?.contains("nema računa u zadnjih 30 dana", false) == true) {
                    return IksicaResult.ReceiptsResult.Success(emptyList())
                }
                if (result.throwable.message?.contains("Not logged in", false) == true) {
                    return IksicaResult.ReceiptsResult.Failure(Throwable("Not logged in"))
                }

                return IksicaResult.ReceiptsResult.Failure(Throwable("Receipts fetching error: " + result.throwable.message))
            }
        }
    }

    override suspend fun getReceipt(url: String): IksicaResult.ReceiptResult {
        return when (val result = iksicaService.getReceipt(url)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                IksicaResult.ReceiptResult.Success(parseDetaljeRacuna(result.data))
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "Receipt fetching error")
                if (result.throwable.message?.contains("Not logged in", false) == true) {
                    return IksicaResult.ReceiptResult.Failure(Throwable("Not logged in"))
                }
                IksicaResult.ReceiptResult.Failure(Throwable("Receipt fetching error" + result.throwable.message))
            }
        }
    }

    override suspend fun insert(receipts: List<Receipt>) {
        iksicaDao.insert(receipts)
    }

    override suspend fun insert(iksicaBalance: IksicaBalance, studentData: StudentData) {
        iksicaDao.insert(iksicaBalance, studentData)
    }

    override suspend fun getCache(): IksicaModel {
        return iksicaDao.read()
    }

}
