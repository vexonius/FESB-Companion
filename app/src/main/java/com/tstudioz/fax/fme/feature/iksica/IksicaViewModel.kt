package com.tstudioz.fax.fme.feature.iksica

import android.app.Application
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.repository.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class IksicaViewModel(
    application: Application,
    private val repository: IksicaRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    val receipts = MutableLiveData<List<Receipt>>()
    val showItem = MutableLiveData<Boolean>()
    val itemToShow = MutableLiveData<Receipt>()
    val isRefreshing = MutableLiveData<Boolean>(false)
    val snackbarHostState = SnackbarHostState()

    private val loggedIn = MutableLiveData<Boolean>(false)
    val loadingTxt = MutableLiveData<String>()
    val iksicaBalance = MutableLiveData<IksicaBalance>()
    val studentDataIksica = MutableLiveData<StudentDataIksica>()
    val status = MutableLiveData<Status>(Status.UNSET)
    val receiptsStatus = MutableLiveData<Status>(Status.UNSET)

    init {
        loadReceipts()
    }

    fun toggleShowItem(value: Boolean) {
        showItem.postValue(value)
    }


    private fun loadReceipts() {
        viewModelScope.launch(Dispatchers.IO) {
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

    private fun displayText(text: String) {
        loadingTxt.postValue(text)
    }

    private suspend fun loginIksica() {
        val email = (sharedPreferences.getString("username", "") ?: "") + "@fesb.hr"
        val password = sharedPreferences.getString("password", "") ?: ""
        try {
            displayText("Getting AuthState...")
            repository.getAuthState()
            displayText("Logging in...")
            repository.login(email, password)
            displayText("Getting ASP.NET Session...")
            val (iksicaBal, studentDataIks) = repository.getAspNetSessionSAML()
            iksicaBalance.postValue(iksicaBal)
            studentDataIksica.postValue(studentDataIks)
            repository.insert(iksicaBal, studentDataIks)
            displayText("Parsing Data...")
            loggedIn.postValue(true)
        } catch (e: Exception) {
            loggedIn.postValue(false)
            e.printStackTrace()
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar("Greška prilikom prijave: " + e.message, duration = SnackbarDuration.Short)
        }
    }

    fun getReceipts(isRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loggedIn.value == false) {
                loginIksica()
            }
            try {
                if (isRefresh) {
                    isRefreshing.postValue(true)
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
        viewModelScope.launch(Dispatchers.IO) {
            if (loggedIn.value == false) {
                loginIksica()
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

private fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            if (value is Boolean && value) {
                removeObserver(this)
            } else if (value !is Boolean) {
                removeObserver(this)
            }
            observer(value)
        }
    })
}
