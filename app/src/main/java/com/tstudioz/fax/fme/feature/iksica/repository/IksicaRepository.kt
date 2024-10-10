package com.tstudioz.fax.fme.feature.iksica.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.parseDetaljeRacuna
import com.tstudioz.fax.fme.feature.iksica.parseRacuni
import com.tstudioz.fax.fme.feature.iksica.parseStudentInfo
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult

class IksicaRepository(
    private val iksicaService: IksicaServiceInterface,
    private val iksicaDao: IksicaDaoInterface,
) : IksicaRepositoryInterface {

    override suspend fun getCardDataAndReceipts(): IksicaResult.CardAndReceiptsResult {
        val studentInfo = getStudentInfo()
        val oib = studentInfo.oib

        when(val receiptsResult = getReceipts(oib)) {
            is IksicaResult.ReceiptsResult.Success -> {
                val receipts = receiptsResult.data
                val studentData = studentInfo.with(receipts)

                insert(studentData)

                return IksicaResult.CardAndReceiptsResult.Success(studentData)
            }

            is IksicaResult.ReceiptsResult.Failure -> {
                return IksicaResult.CardAndReceiptsResult.Failure(receiptsResult.throwable)
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

    override suspend fun insert(model: StudentData) {
        iksicaDao.insert(model.toRealmModel())
    }

    override suspend fun getCache(): StudentData? {
        val model = iksicaDao.read() ?: return null

        return StudentData(model)
    }

    private suspend fun getStudentInfo(): StudentData {
        when (val result = iksicaService.getStudentInfo()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                val data = parseStudentInfo(result.data)

                return data
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, result.throwable.message ?: "AspNetSessionSAML fetching error")
                throw Exception(result.throwable.message ?: "AspNetSessionSAML fetching error")
            }
        }
    }

    private suspend fun getReceipts(oib: String): IksicaResult.ReceiptsResult {
        when (val result = iksicaService.getReceipts(oib)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                val receiptsList = parseRacuni(result.data)

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

}
