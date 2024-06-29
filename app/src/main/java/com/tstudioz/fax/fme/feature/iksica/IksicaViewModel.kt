package com.tstudioz.fax.fme.feature.iksica

import android.app.Application
import androidx.compose.material3.SnackbarDuration
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@InternalCoroutinesApi
class IksicaViewModel(
    application: Application,
    private val repository: IksicaRepositoryInterface
) : AndroidViewModel(application) {

    private var _receipts = MutableLiveData<List<Receipt>>()
    val receipts: LiveData<List<Receipt>> = _receipts

    private var _showItem = MutableLiveData<Boolean>()
    val showItem: LiveData<Boolean> = _showItem

    private val _itemToShow = MutableLiveData<Receipt>()
    val itemToShow: LiveData<Receipt> = _itemToShow

    private val _isRefreshing = MutableLiveData<Boolean>(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    val loadingTxt: LiveData<String> = repository.loadingTxt
    val iksicaBalance: LiveData<IksicaBalance> = repository.iksicaBalance
    val studentDataIksica: LiveData<StudentDataIksica> = repository.studentDataIksica
    val snackbarHostState = repository.snackbarHostState
    val status: LiveData<Status> = repository.status
    val haveRightPassword: LiveData<Boolean> = repository.haveRightPassword
    private val _showLoading = MutableLiveData<Boolean>().apply { value = false }
    val showLoading: LiveData<Boolean> = _showLoading

    init {
        loadReceipts()
    }

    fun toggleShowItem(value: Boolean) {
        _showItem.postValue(value)
    }

    fun loginIksica(username:String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _showLoading.postValue(true)
            repository.loginIksica(username, password)
            _showLoading.postValue(false)
        }
    }


    private fun loadReceipts() {
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                repository.loadData()
                _receipts.postValue(repository.read().first)
            } catch (e: Exception) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar("Pogreška: " + e.message, duration = SnackbarDuration.Short)
                e.printStackTrace()
            }
        }
        repository.loggedIn.observeOnce {
            if (it) {
                getReceipts(isRefreshing = false)
            }
        }
    }

    fun getReceipts( isRefreshing: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                if (isRefreshing){ _isRefreshing.postValue(true) }
                val receiptsNew = repository.getReceipts()
                _receipts.postValue(receiptsNew)
                repository.insert(receiptsNew)
            } catch (e: Exception) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(e.message ?: "Pogreška", duration = SnackbarDuration.Short)
                e.printStackTrace()
            } finally {
                if (isRefreshing){ _isRefreshing.postValue(false) }
            }
        }
    }

    fun getReceiptDetails(receipt: Receipt) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val detalji = repository.getRacun(receipt.urlSastavnica)
                _showItem.postValue(true)
                _itemToShow.postValue(receipt.copy(detaljiRacuna = detalji))
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
