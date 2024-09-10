package com.tstudioz.fax.fme.feature.studomat.view

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.models.Year
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.random.NetworkUtils
import io.realm.kotlin.internal.platform.runBlocking
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StudomatViewModel(
    private val repository: StudomatRepository,
    context: Context,
    private val sharedPreferences: SharedPreferences,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    val isRefreshing = MutableLiveData(false)

    val snackbarHostState
        get() = repository.snackbarHostState
    val subjectList
        get() = repository.subjectList
    val loading = repository.loadedTxt.map{ value ->
        value == "fetching" || value == "unset"
    }
    val student
        get() = repository.student
    val generated
        get() = repository.generated
    val years
        get() = repository.years
    val selectedYear
        get() = repository.selectedYear
    val offline
        get() = !networkUtils.isNetworkAvailable()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        runBlocking { repository.initRepo() }
    }

    suspend fun login(pulldownTriggered: Boolean = false) {
        if (networkUtils.isNetworkAvailable()) {
            repository.loadedTxt.postValue("fetching")
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


    fun initStudomat() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (!networkUtils.isNetworkAvailable()) {
                repository.loadedTxt.postValue("fetchingError")
                snackbarHostState.showSnackbar("Nema interneta")
                return@launch
            } else {
                login()
                when (val years = repository.getYears()) {
                    is StudomatRepositoryResult.YearsResult.Success -> {
                        years.data.firstOrNull()?.let { getChosenYear(it) }
                    }

                    is StudomatRepositoryResult.YearsResult.Failure -> {
                        repository.loadedTxt.postValue("fetchingError")
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    }
                }
            }
        }
    }

    fun getChosenYear(year: Year, pulldownTriggered: Boolean = false) {
        if (!networkUtils.isNetworkAvailable()) {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                val chosenYear = if (year.href.isEmpty() || year.title.isEmpty()) {
                    years.value?.firstOrNull()
                } else { year }
                repository.selectedYear.postValue(chosenYear)
                if (chosenYear != null) {
                    repository.getChosenYear(chosenYear, offline = true)
                }
            }
        } else {
            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
                if (pulldownTriggered) {
                    isRefreshing.postValue(true)
                }
                repository.selectedYear.postValue(year)
                login(pulldownTriggered)
                repository.getChosenYear(year)
                if (pulldownTriggered) {
                    isRefreshing.postValue(false)
                }
            }
        }
    }
}