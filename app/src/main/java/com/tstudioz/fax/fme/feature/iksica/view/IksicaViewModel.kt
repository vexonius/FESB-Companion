package com.tstudioz.fax.fme.feature.iksica.view

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.models.IksicaBalance
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class IksicaViewModel(private val repository: IksicaRepositoryInterface) : ViewModel() {

    val snackbarHostState = SnackbarHostState()

    val _receipts = MutableLiveData<List<Receipt>>(emptyList())
    val receipts: LiveData<List<Receipt>> = _receipts

    val _itemToShow = MutableLiveData<Receipt?>(null)
    val itemToShow: LiveData<Receipt?> = _itemToShow

    val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val _iksicaBalance = MutableLiveData<IksicaBalance?>(null)
    val iksicaBalance: LiveData<IksicaBalance?> = _iksicaBalance

    val _studentData = MutableLiveData<StudentData?>(null)
    val studentData: LiveData<StudentData?> = _studentData

    val _viewState = MutableLiveData<IksicaViewState>(IksicaViewState.Initial)
    val viewState: LiveData<IksicaViewState> = _viewState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Iksica", throwable.message.toString())
    }

    init {
        loadReceiptsFromCache()
        getReceipts()
    }

    private fun loadReceiptsFromCache() {
        _viewState.value = IksicaViewState.Loading
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val model = repository.read()
            _receipts.postValue(model.receipts)
            _studentData.postValue(model.studentData)
            _iksicaBalance.postValue(model.balance)
        }
    }

    fun getReceipts() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val info = repository.getStudentInfo()
            val oib = info.second.oib

            _iksicaBalance.postValue(info.first)
            _studentData.postValue(info.second)

            when (val receipts = repository.getReceipts(oib)) {
                is IksicaResult.ReceiptsResult.Success -> {
                    if (receipts.data.isEmpty()) { _viewState.postValue(IksicaViewState.Empty) }

                    _receipts.postValue(receipts.data)
                    _viewState.postValue(IksicaViewState.Success(receipts.data))
                }

                is IksicaResult.ReceiptsResult.Failure -> {
                    snackbarHostState.showSnackbar(
                        "Greška prilikom dohvaćanja liste računa",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    fun getReceiptDetails(receipt: Receipt?) {
        if (receipt == null) {
            hideReceiptDetails()
            return
        }

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (val details = repository.getReceipt(receipt.url)) {
                is IksicaResult.ReceiptResult.Success -> {
                    _itemToShow.postValue(receipt.copy(receiptDetails = details.data))
                }

                is IksicaResult.ReceiptResult.Failure -> {
                    snackbarHostState.showSnackbar(
                        "Greška prilikom dohvaćanja detalja računa",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    fun hideReceiptDetails() {
        _itemToShow.value = null
    }
}