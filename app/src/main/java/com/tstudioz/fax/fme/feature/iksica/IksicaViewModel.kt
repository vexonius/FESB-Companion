package com.tstudioz.fax.fme.feature.iksica

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
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

    init {
        getReceipts()
    }

    fun toggleShowItem(value: Boolean) {
        _showItem.postValue(value)
    }

    fun setItemToShow(receipt: Receipt) {
        _itemToShow.postValue(receipt)
    }


    fun getReceipts() {
        viewModelScope.launch {
            try {
                _receipts.postValue(repository.read().first)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        repository.loggedIn.observeOnce() {
            if (it) {
                try {
                    viewModelScope.launch(Dispatchers.IO) {
                        val receiptsNew = repository.getReceipts()
                        _receipts.postValue(receiptsNew)
                        repository.insert(receiptsNew)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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