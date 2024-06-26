package com.tstudioz.fax.fme.feature.iksica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.IksicaBalance
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
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


    init {
        loadReceipts()
    }

    fun toggleShowItem(value: Boolean) {
        _showItem.postValue(value)
    }

    fun setItemToShow(receipt: Receipt) {
        _itemToShow.postValue(receipt)
    }


    fun loadReceipts() {
        viewModelScope.launch(Dispatchers.IO)  {
            try {
                repository.loadData()
                _receipts.postValue(repository.read().first)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        repository.loggedIn.observeOnce {
            if (it) {
                getReceipts(false)
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
                // ode dodati snackbar jer nekad moze failat bez da ide preko failure
                e.printStackTrace()
                delay(1000)
            } finally {
                if (isRefreshing){ _isRefreshing.postValue(false) }
            }
        }
    }

    fun getReceiptDetails(receipt: Receipt) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = repository.getRacun(receipt.urlSastavnica)
                _showItem.postValue(true)
                _itemToShow.postValue(receipt.copy(detaljiRacuna = list))
            } catch (e: Exception) {
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