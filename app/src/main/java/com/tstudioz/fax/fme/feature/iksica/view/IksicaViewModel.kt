package com.tstudioz.fax.fme.feature.iksica.view

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class IksicaViewModel(
    private val repository: IksicaRepositoryInterface,
    private val application: Application
) : ViewModel() {

    val snackbarHostState = SnackbarHostState()

    private val _studentData = MutableLiveData<StudentData?>(null)
    val studentData: LiveData<StudentData?> = _studentData

    private val _receiptSelected = MutableLiveData<IksicaReceiptState>(IksicaReceiptState.None)
    val receiptSelected: LiveData<IksicaReceiptState> = _receiptSelected

    private val _viewState = MutableLiveData<IksicaViewState>(IksicaViewState.Initial)
    val viewState: LiveData<IksicaViewState> = _viewState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Iksica", throwable.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(application.getString(R.string.error_general_iksica))
        }
    }

    init {
        loadReceiptsFromCache()
    }

    private fun loadReceiptsFromCache() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _viewState.postValue(IksicaViewState.Loading)
            val model = repository.getCache()
            if (model == null) {
                _viewState.postValue(IksicaViewState.Empty)
                return@launch
            }

            _viewState.postValue(IksicaViewState.Success(model))
            _studentData.postValue(model)
        }
    }

    fun getReceipts() {
        _studentData.value?.let { _viewState.value = IksicaViewState.Fetching(it) }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (val result = repository.getCardDataAndReceipts()) {
                is IksicaResult.CardAndReceiptsResult.Success -> {
                    val model = result.data
                    _viewState.postValue(IksicaViewState.Success(model))
                    _studentData.postValue(model)
                }

                is IksicaResult.CardAndReceiptsResult.Failure -> {
                    snackbarHostState.showSnackbar(
                        application.getString(R.string.error_fetching_receipts_iksica),
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
            _receiptSelected.postValue(IksicaReceiptState.Fetching)
            when (val details = repository.getReceipt(receipt.url)) {
                is IksicaResult.ReceiptResult.Success -> {
                    _receiptSelected.postValue(IksicaReceiptState.Success(receipt.copy(receiptDetails = details.data)))
                }

                is IksicaResult.ReceiptResult.Failure -> {
                    _receiptSelected.postValue(IksicaReceiptState.Error(details.throwable.message.toString()))
                    snackbarHostState.showSnackbar(
                        application.getString(R.string.error_receipt_details_iksica),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    fun hideReceiptDetails() {
        _receiptSelected.postValue(IksicaReceiptState.None)
    }
}