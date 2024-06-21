package com.tstudioz.fax.fme.feature.iksica.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDao
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import com.tstudioz.fax.fme.models.util.parseDetaljeRacuna
import com.tstudioz.fax.fme.models.util.parseRacuni
import com.tstudioz.fax.fme.models.util.parseStudentInfo


class IksicaRepository(
    private val iksicaService: IksicaServiceInterface,
    private val iksicaDao: IksicaDaoInterface
) : IksicaRepositoryInterface {

    val _loggedIn = MutableLiveData<Boolean>(false)
    override val loggedIn: MutableLiveData<Boolean> = _loggedIn


    override suspend fun getAuthState(): NetworkServiceResult.IksicaResult {
        return when (val result = iksicaService.getAuthState()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "AuthState fetched")
                result
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "TimetableInfo fetching error")
                throw Exception("TimetableInfo fetching error")
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
                Log.e(TAG, "Login error")
                throw Exception("Login error")
            }
        }
    }

    override suspend fun getAspNetSessionSAML(): Pair<IksicaBalance, StudentDataIksica> {
        when (val result = iksicaService.getAspNetSessionSAML()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "AspNetSessionSAML fetched")
                val info = parseStudentInfo(result.data)
                _loggedIn.postValue(true)
                return info
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "AspNetSessionSAML fetching error")
                throw Exception("AspNetSessionSAML fetching error")
            }
        }
    }

    override suspend fun getReceipts(): List<Receipt> {
        return when (val result = iksicaService.getRacuni()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Racuni fetched")
                parseRacuni(result.data)
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "Racuni fetching error")
                throw Exception("Racuni fetching error")
            }
        }
    }

    override suspend fun getRacun(url: String): MutableList<ReceiptItem> {
        return when (val result = iksicaService.getRacun(url)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Racuni fetched")
                parseDetaljeRacuna(result.data)
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "Racuni fetching error")
                throw Exception("Racuni fetching error")
            }
        }
    }

    override suspend fun insert(receipts: List<Receipt>) {
        iksicaDao.insert(receipts)
    }

    override suspend fun insert(iksicaBalance: IksicaBalance, studentDataIksica: StudentDataIksica){
        iksicaDao.insert(iksicaBalance, studentDataIksica)
    }

    override suspend fun read(): Triple<List<Receipt>, IksicaBalance?, StudentDataIksica?> {
        return iksicaDao.read()
    }


}
