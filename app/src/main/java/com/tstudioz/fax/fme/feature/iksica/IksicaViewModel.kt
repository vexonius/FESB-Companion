package com.tstudioz.fax.fme.feature.iksica

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.repository.Status
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class IksicaViewModel(
    private val repository: IksicaRepositoryInterface,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    val receipts = MutableLiveData<List<Receipt>>()
    val showItem = MutableLiveData<Boolean>()
    val itemToShow = MutableLiveData<Receipt>()
    val isRefreshing = MutableLiveData(false)
    val snackbarHostState = SnackbarHostState()

    private val loggedIn = MutableLiveData(false)
    val loginStatus = MutableLiveData(LoginStatus.UNSET)
    val iksicaBalance = MutableLiveData<IksicaBalance>()
    val studentDataIksica = MutableLiveData<StudentDataIksica>()
    val status = MutableLiveData(Status.UNSET)
    val receiptsStatus = MutableLiveData(Status.UNSET)

    fun toggleShowItem(value: Boolean) {
        showItem.postValue(value)
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        loadReceipts()
    }


    private fun loadReceipts() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                val data = repository.read()
                receipts.postValue(data.first)
                iksicaBalance.postValue(data.second)
                studentDataIksica.postValue(data.third)
            } catch (e: Exception) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Pogreška: " + e.message, duration = SnackbarDuration.Short)
                e.printStackTrace()
            }
            loginIksica()
        }
        loggedIn.observeOnce {
            if (it) {
                getReceipts(isRefresh = false)
            }
        }
    }

    private fun displayStatus(status: LoginStatus) {
        loginStatus.postValue(status)
    }

    private suspend fun loginIksica(): Boolean {
        if (loginStatus.value != LoginStatus.UNSET && loginStatus.value != LoginStatus.FAILURE) {
            return false
        }
        val user = userRepository.getCurrentUser()
        val email = user.username + "@fesb.hr"
        val password = user.password

        try {
            displayStatus(LoginStatus.AUTH_STATE)
            repository.getAuthState()
            displayStatus(LoginStatus.LOGIN)
            repository.login(email, password)
            displayStatus(LoginStatus.ASP_NET_SESSION)
            val (iksicaBal, studentDataIks) = repository.getAspNetSessionSAML()
            iksicaBalance.postValue(iksicaBal)
            studentDataIksica.postValue(studentDataIks)
            repository.insert(iksicaBal, studentDataIks)
            displayStatus(LoginStatus.SUCCESS)
            loggedIn.postValue(true)
            return true
        } catch (e: Exception) {
            loggedIn.postValue(false)
            loginStatus.postValue(LoginStatus.FAILURE)
            e.printStackTrace()
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar("Greška prilikom prijave: " + e.message, duration = SnackbarDuration.Short)
            return false
        }
    }

    fun getReceipts(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                if (isRefresh) {
                    isRefreshing.postValue(true)
                }
                if (loggedIn.value == false) {
                    if (!loginIksica()) {
                        return@launch
                    }
                }
                when (val receiptsNew = repository.getReceipts(studentDataIksica.value?.oib ?: "")) {
                    is IksicaResult.ReceiptsResult.Success -> {
                        receiptsStatus.postValue(Status.FETCHED)
                        if (receiptsNew.data.isEmpty()) {
                            receiptsStatus.postValue(Status.EMPTY)
                        }
                        receipts.postValue(receiptsNew.data)
                        repository.insert(receiptsNew.data)
                    }

                    is IksicaResult.ReceiptsResult.Failure -> {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        if (receiptsNew.throwable.message?.contains("Not logged in", false) == true) {
                            loggedIn.postValue(false)
                            loginStatus.postValue(LoginStatus.UNSET)
                        }
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            "Greška prilikom dohvaćanja liste računa",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            } catch (e: Exception) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(e.message ?: "Pogreška", duration = SnackbarDuration.Short)
                e.printStackTrace()
            } finally {
                if (isRefresh) {
                    isRefreshing.postValue(false)
                }
            }
        }
    }

    fun getReceiptDetails(receipt: Receipt) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (loggedIn.value == false) {
                if (!loginIksica()) {
                    return@launch
                }
            }
            try {
                status.postValue(Status.FETCHING)
                when (val details = repository.getReceipt(receipt.href)) {
                    is IksicaResult.ReceiptResult.Success -> {
                        status.postValue(Status.FETCHED)
                        showItem.postValue(true)
                        itemToShow.postValue(receipt.copy(receiptDetails = details.data))
                    }

                    is IksicaResult.ReceiptResult.Failure -> {
                        status.postValue(Status.FETCHING_ERROR)
                        snackbarHostState.currentSnackbarData?.dismiss()
                        if (details.throwable.message?.contains("Not logged in", false) == true) {
                            loggedIn.postValue(false)
                            loginStatus.postValue(LoginStatus.UNSET)
                        }
                        snackbarHostState.showSnackbar(
                            "Greška prilikom dohvaćanja detalja računa",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            } catch (e: Exception) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(e.message ?: "Pogreška", duration = SnackbarDuration.Short)
                e.printStackTrace()
            }
        }
    }
}

