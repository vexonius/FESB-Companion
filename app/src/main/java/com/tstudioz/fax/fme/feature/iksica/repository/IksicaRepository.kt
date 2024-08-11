package com.tstudioz.fax.fme.feature.iksica.repository

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.dao.IksicaDaoInterface
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.feature.iksica.services.IksicaServiceInterface
import com.tstudioz.fax.fme.models.util.parseDetaljeRacuna
import com.tstudioz.fax.fme.models.util.parseRacuni
import com.tstudioz.fax.fme.models.util.parseStudentInfo


class IksicaRepository(
    private val iksicaService: IksicaServiceInterface,
    private val iksicaDao: IksicaDaoInterface,
    private val sharedPreferences: SharedPreferences
) : IksicaRepositoryInterface {

    private val _loggedIn = MutableLiveData<Boolean>(false)
    private val _loadingTxt = MutableLiveData<String>()
    private val _iksicaBalance = MutableLiveData<IksicaBalance>()
    private val _studentDataIksica = MutableLiveData<StudentDataIksica>()
    private val _status = MutableLiveData<Status>(Status.UNSET)

    override val loggedIn: MutableLiveData<Boolean> = _loggedIn
    override val loadingTxt: LiveData<String> = _loadingTxt
    override val iksicaBalance: LiveData<IksicaBalance> = _iksicaBalance
    override val studentDataIksica: LiveData<StudentDataIksica> = _studentDataIksica
    override val status: LiveData<Status> = _status

    override val snackbarHostState = SnackbarHostState()

    override suspend fun loadData() {
        try {
            val (_, iksicaBalance, studentDataIksica) = read()
            if (iksicaBalance != null) {
                _iksicaBalance.postValue(iksicaBalance!!)
            }
            if (studentDataIksica != null) {
                _studentDataIksica.postValue(studentDataIksica!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayText(text: String) {
        _loadingTxt.postValue(text)
    }


    override suspend fun loginIksica() {
        val email = (sharedPreferences.getString("username", "") ?: "") + "@fesb.hr"
        val password = sharedPreferences.getString("password", "") ?: ""
        try {
            displayText("Getting AuthState...")
            getAuthState()
            displayText("Logging in...")
            login(email, password)
            displayText("Getting ASP.NET Session...")
            val (iksicaBalance, studentDataIksica) = getAspNetSessionSAML()
            _iksicaBalance.postValue(iksicaBalance)
            _studentDataIksica.postValue(studentDataIksica)
            insert(iksicaBalance, studentDataIksica)
            displayText("Parsing Data...")
        } catch (e: Exception) {
            e.printStackTrace()
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar("Greška prilikom prijave: " + e.message, duration = SnackbarDuration.Short)
        }
    }


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
                _loggedIn.postValue(false)
                Log.e(TAG, result.throwable.message ?: "Login error")
                throw Exception(result.throwable.message ?: "Login error")
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
                _loggedIn.postValue(false)
                Log.e(TAG, result.throwable.message ?: "AspNetSessionSAML fetching error")
                throw Exception(result.throwable.message ?: "AspNetSessionSAML fetching error")
            }
        }
    }

    override suspend fun getReceipts(): List<Receipt> {
        if (_loggedIn.value == false) {
            loginIksica()
        }
        return when (val result = iksicaService.getRacuni(studentDataIksica.value?.oib ?: "")) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Racuni fetched")
                parseRacuni(result.data)
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "Racuni fetching error")
                if (result.throwable.message?.contains("Not logged in", false) == true) {
                    _loggedIn.postValue(false)
                }
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja liste računa", duration = SnackbarDuration.Short)
                throw Exception("Racuni fetching error: " + result.throwable.message)
            }
        }
    }

    override suspend fun getRacun(url: String): MutableList<ReceiptItem> {
        _status.postValue(Status.FETCHING)
        if (_loggedIn.value == false) {
            loginIksica()
        }
        return when (val result = iksicaService.getRacun(url)) {
            is NetworkServiceResult.IksicaResult.Success -> {
                Log.d(TAG, "Racun fetched")
                _status.postValue(Status.FETCHED)
                parseDetaljeRacuna(result.data)
            }
            is NetworkServiceResult.IksicaResult.Failure -> {
                Log.e(TAG, "Racun fetching error")
                if (result.throwable.message?.contains("Not logged in", false) == true) {
                    _loggedIn.postValue(false)
                }
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja detalja računa", duration = SnackbarDuration.Short)
                _status.postValue(Status.FETCHING_ERROR)
                throw Exception("Racun fetching error" + result.throwable.message)
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


enum class Status {
    FETCHING,
    FETCHED,
    FETCHED_NEW,
    FETCHING_ERROR,
    UNSET
}
