package com.tstudioz.fax.fme.feature.iksica

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class IksicaViewModel(
    private val repository: IksicaRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val _receipts = MutableLiveData<List<Receipt>>(emptyList())
    val _showItem = MutableLiveData(false)
    val _itemToShow = MutableLiveData<Receipt>()
    val _isRefreshing = MutableLiveData(false)
    val _snackbarHostState = SnackbarHostState()

    private val loggedIn = MutableLiveData(false)
    val loginStatus = MutableLiveData(LoginStatus.UNSET)
    val iksicaBalance = MutableLiveData<IksicaBalance>()
    val studentDataIksica = MutableLiveData<StudentDataIksica>()
    val status = MutableLiveData(IksicaViewState.INITIAL)
    val receiptsStatus = MutableLiveData(IksicaViewState.INITIAL)

    fun toggleShowItem(value: Boolean) {
        _showItem.postValue(value)
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Iksica", throwable.message.toString())
    }

    init {
        loadReceipts()
        getReceipts()
    }

    private fun loadReceipts() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val info = repository.getStudentInfo()
            Log.d("Iksica data", info.toString())
        }
    }

    fun getReceipts() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val info = repository.getStudentInfo()
            val oib = info.second.oib

            when (val receipts = repository.getReceipts(oib)) {
                is IksicaResult.ReceiptsResult.Success -> {
                    if (receipts.data.isEmpty()) { receiptsStatus.postValue(IksicaViewState.EMPTY) }

                    _receipts.postValue(receipts.data)
                    receiptsStatus.postValue(IksicaViewState.SUCCESS)
                }

                is IksicaResult.ReceiptsResult.Failure -> {
                    _snackbarHostState.showSnackbar(
                        "Greška prilikom dohvaćanja liste računa",
                         duration = SnackbarDuration.Short
                    )
                }
            }
        }

        fun getReceiptDetails(receipt: Receipt) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                status.postValue(IksicaViewState.LOADING)
                when (val details = repository.getReceipt(receipt.url)) {
                    is IksicaResult.ReceiptResult.Success -> {
                        status.postValue(IksicaViewState.SUCCESS)
                        _itemToShow.postValue(receipt.copy(receiptDetails = details.data))
                    }

                    is IksicaResult.ReceiptResult.Failure -> {
                        status.postValue(IksicaViewState.ERROR)
                        _snackbarHostState.showSnackbar(
                            "Greška prilikom dohvaćanja detalja računa",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }
}