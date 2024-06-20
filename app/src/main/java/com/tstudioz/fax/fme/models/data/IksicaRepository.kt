package com.tstudioz.fax.fme.models.data

import android.content.ContentValues.TAG
import android.util.Log
import com.tstudioz.fax.fme.database.models.IksicaSaldo
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.models.interfaces.IksicaServiceInterface
import com.tstudioz.fax.fme.models.util.parseDetaljeRacuna
import com.tstudioz.fax.fme.models.util.parseRacuni
import com.tstudioz.fax.fme.models.util.parseStudentInfo


class IksicaRepository(
    private val iksicaService: IksicaServiceInterface,
) : IksicaRepositoryInterface {


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

    override suspend fun getAspNetSessionSAML(): Pair<IksicaSaldo, StudentDataIksica> {
        return when (val result = iksicaService.getAspNetSessionSAML()) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "AspNetSessionSAML fetched")
                parseStudentInfo(result.data)
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "AspNetSessionSAML fetching error")
                throw Exception("AspNetSessionSAML fetching error")
            }
        }
    }

    override suspend fun getRacuni(): List<Receipt> {
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

    /*suspend fun loginAndGetData(email: String, password: String): List<Receipt> {
        suspend fun getData(
            username: String,
            password: String,
            forceLogin: Boolean,
            timeout: Boolean = false
        ): Result.LoginResult {

            if (username == "" || password == "") {
                return Result.LoginResult.Error("Username or password is empty")
            }
            if (timeout && (System.currentTimeMillis() - iksicaService.lastTimeGotData) < 60000) {
                return Result.LoginResult.Refresh("Data fresh enough, not refreshing")
            }

            try {
                if ((System.currentTimeMillis() - iksicaService.lastTimeLoggedIn) > 3600000 || forceLogin) {
                    when (val result = iksicaService.getAuthState()) {
                        is NetworkServiceResult.IksicaResult..Success -> {}
                        is NetworkServiceResult.IksicaResult.Error -> return Result.LoginResult.Error("Error getting SAMLRequest: ${result.error}")
                    }
                    when (val result = iksicaService.login(username, password)) {
                        is NetworkServiceResult.IksicaResult.Success -> {}
                        is NetworkServiceResult.IksicaResult.Error -> return Result.LoginResult.Error("Error getting SAMLResponse: ${result.error}")
                    }
                    when (val result = iksicaService.getAspNetSessionSAML()) {
                        is NetworkServiceResult.IksicaResult.Success -> {}
                        is NetworkServiceResult.IksicaResult.Error -> return Result.LoginResult.Error("Error sending SAMLRequest to ISVU: ${result.error}")
                    }
                    when (val result = iksicaService.getRacuni()) {
                        is NetworkServiceResult.IksicaResult.Success -> {}
                        is NetworkServiceResult.IksicaResult.Error -> return Result.LoginResult.Error("Error getting data:${result.error}")
                    }
                }

                return when (val result = iksicaService.getUgovoriData()) {
                    is NetworkServiceResult.IksicaResult.Success -> {
                        if (forceLogin) { iksicaService.resetLastTimeGotData() }
                        Result.LoginResult.Success(result.data)
                    }

                    is NetworkServiceResult.IksicaResult.Error -> Result.LoginResult.Error("Error getting data:${result.error}")
                }
            } catch (e: Exception) {
                iksicaService.resetLastTimeLoggedIn()
                return Result.LoginResult.Error("Error: ${e.message}")
            }
        }
    }*/
}
