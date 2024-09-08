package com.tstudioz.fax.fme.feature.studomat.view

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Year
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.random.NetworkUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudomatViewModel(
    private val repository: StudomatRepository,
    context: Context,
    private val sharedPreferences: SharedPreferences,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    val snackbarHostState get() = repository.snackbarHostState
    val predmetList get() = repository.subjectList
    val loadedTxt get() = repository.loadedTxt
    val student get() = repository.student
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val generated get() = repository.generated
    val godine get() = repository.years
    val selectedGodina get() = repository.selectedGodina
    val offline get() = !networkUtils.isNetworkAvailable()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            repository.loadFromDb()
        }
    }

    suspend fun login(pulldownTriggered: Boolean = false) {
        if (networkUtils.isNetworkAvailable()) {
            loadedTxt.postValue("fetching")
            when (val result = repository.loginUser(
                sharedPreferences.getString("username", "") ?: "",
                sharedPreferences.getString("password", "") ?: "",
                false
            )) {
                is StudomatRepositoryResult.LoginResult.Success -> {
                    repository.student.postValue(result.data)
                }

                is StudomatRepositoryResult.LoginResult.Failure -> {
                    if (pulldownTriggered) {
                        isRefreshing.postValue(false)
                    }
                    repository.snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    repository.loadedTxt.postValue("fetchingError")
                }
            }
        } else {
            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
            repository.snackbarHostState.showSnackbar("Nema interneta")
            repository.loadedTxt.postValue("fetchingError")
        }
    }


    fun initStudomat(refresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (!networkUtils.isNetworkAvailable()) {
                loadedTxt.postValue("fetchingError")
                snackbarHostState.showSnackbar("Nema interneta")
                return@launch
            } else {
                login()
                when (val years = repository.getYears()) {
                    is StudomatRepositoryResult.YearsResult.Success -> {
                        if (refresh) {
                            selectedGodina.value?.let { getChosenYear(it) }
                        } else {
                            years.data.firstOrNull()?.let { getChosenYear(it) }
                        }
                    }

                    is StudomatRepositoryResult.YearsResult.Failure -> {
                        loadedTxt.postValue("fetchingError")
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    }
                }
            }

        }
    }

    fun getChosenYear(year: Year, pulldownTriggered: Boolean = false) {
        if (!networkUtils.isNetworkAvailable()) {
            repository.selectedGodina.postValue(year)
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                repository.getChosenYear(year, offline = true)
            }
        } else {
            if (pulldownTriggered) {
                isRefreshing.postValue(true)
            }
            repository.selectedGodina.postValue(year)
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                login(pulldownTriggered)
                repository.getChosenYear(year)
                if (pulldownTriggered) {
                    isRefreshing.postValue(false)
                }
            }
        }
    }
}