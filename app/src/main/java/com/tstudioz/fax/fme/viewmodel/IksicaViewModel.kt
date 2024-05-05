package com.tstudioz.fax.fme.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.models.data.IksicaRepositoryInterface
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

    private var _loadingTxt = MutableLiveData<String>()
    val loadingTxt: LiveData<String> = _loadingTxt

    fun toggleShowItem(value: Boolean) {
        _showItem.postValue(value)
    }

    fun setItemToShow(receipt: Receipt) {
        _itemToShow.postValue(receipt)
    }


    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadingTxt.postValue("Getting AuthState...")
                repository.getAuthState()
                _loadingTxt.postValue("Logging in...")
                repository.login(email, password)
                _loadingTxt.postValue("Getting ASP.NET Session...")
                repository.getAspNetSessionSAML()
                _loadingTxt.postValue("Parsing Data...")
                _receipts.postValue(repository.getRacuni())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getRacun(receipt: Receipt) {
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

