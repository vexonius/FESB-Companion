package com.tstudioz.fax.fme.feature.iksica.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.util.parseDetaljeRacuna
import com.tstudioz.fax.fme.models.util.parseRacuni
import com.tstudioz.fax.fme.models.util.parseStudentInfo


class IksicaRepository(
    private val iksicaService: IksicaServiceInterface,
    private val iksicaDao: IksicaDaoInterface,
) : IksicaRepositoryInterface {

    override suspend fun getAuthState(): NetworkServiceResult.IksicaResult {
        return when (val result = iksicaService.getAuthState()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "AuthState fetched")
                result
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, result.throwable.message ?: "AuthState fetching error")
                throw Exception(result.throwable.message ?: "AuthState fetching error")
            }
        }
    }

    override suspend fun login(email: String, password: String): NetworkServiceResult.IksicaResult {
        return when (val result = iksicaService.login(email, password)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Login success")
                result
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, result.throwable.message ?: "Login error")
                throw Exception(result.throwable.message ?: "Login error")
            }
        }
    }

    override suspend fun getAspNetSessionSAML(): Pair<IksicaBalance, StudentDataIksica> {
        when (val result = iksicaService.getAspNetSessionSAML()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "AspNetSessionSAML fetched")
                return parseStudentInfo(result.data)
            }

            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, result.throwable.message ?: "AspNetSessionSAML fetching error")
                throw Exception(result.throwable.message ?: "AspNetSessionSAML fetching error")
            }
        }
    }

    override suspend fun getReceipts(oib: String): IksicaResult.ReceiptsResult {
        return when (val result = iksicaService.getReceipts(oib)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Recepts fetched")
                IksicaResult.ReceiptsResult.Success(parseRacuni(result.data))
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
        return when (val result = iksicaService.getRacun(url)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Receipt fetched")
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

    override suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica) {
        iksicaDao.insert(iksicaBalance, studentDataIksica)
    }

    override suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?> {
        return iksicaDao.read()
    }

}

enum class Status {
    FETCHING,
    FETCHED,
    FETCHED_NEW,
    FETCHING_ERROR,
    EMPTY,
    UNSET
}
