package com.tstudioz.fax.fme.feature.studomat.view

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Student
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudomatViewModel(
    private val repository: StudomatRepository,
    context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val snackbarHostState get() = repository.snackbarHostState
    val predmetList get() = repository.predmetList
    val loadedTxt get() = repository.loadedTxt
    val student get() = repository.student
    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val generated get() = repository.generated
    val godine get() = repository.godine
    val selectedGodina get() = repository.selectedGodina
    val polozeniKrozUpisani get() = repository.polozeniKrozUpisani


    fun getStudomatData(refresh: Boolean = false) {
        loadedTxt.postValue("fetching")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.loginUser(
                sharedPreferences.getString("username", "") ?: "",
                sharedPreferences.getString("password", "") ?: "",
                false
            )) {
                is StudomatRepositoryResult.LoginResult.Success -> {
                    student.postValue(result.data as Student)
                }

                is StudomatRepositoryResult.LoginResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    loadedTxt.postValue("fetchingError")
                    return@launch
                }
            }
            repository.getYears()
            val odabrana =
                if (refresh) {
                    selectedGodina.value
                } else {
                    godine.value?.getOrNull(0) // suspiucious
                }
            if (odabrana != null) {
                getOdabranuGodinu(odabrana)
            }
        }
    }

    fun getOdabranuGodinu(pair: Pair<String, String>, pulldownTriggered: Boolean = false) {
        if (pulldownTriggered) {
            isRefreshing.postValue(true)
        }
        repository.loadedTxt.postValue("fetching")
        repository.selectedGodina.postValue(pair)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.loginUser(
                sharedPreferences.getString("username", "") ?: "",
                sharedPreferences.getString("password", "") ?: "",
                false
            )) {
                is StudomatRepositoryResult.LoginResult.Success -> {
                    repository.student.postValue(result.data as Student)
                }

                is StudomatRepositoryResult.LoginResult.Failure -> {
                    if (pulldownTriggered) {
                        isRefreshing.postValue(false)
                    }
                    repository.snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    repository.loadedTxt.postValue("fetchingError")
                    return@launch
                }
            }
            repository.getOdabranuGodinu(pair)

            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
        }
    }
}