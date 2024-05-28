package com.tstudioz.fax.fme.feature.studomat.view

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.studomatisvu.model.dataclasses.Predmet
import com.example.studomatisvu.model.dataclasses.Student
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.models.NetworkServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudomatViewModel(
    private val repository: StudomatRepository,
    context: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    var predmetList = MutableLiveData<List<Predmet>>(emptyList())
        private set
    var loadedTxt = MutableLiveData("unset")
        private set
    var student = MutableLiveData(Student())
        private set
    var isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
        private set
    var generated = MutableLiveData("")
        private set
    var godine = MutableLiveData<List<Pair<String, String>>>(emptyList())
        private set
    var selectedGodina = MutableLiveData(Pair("", ""))
        private set
    var polozeniKrozUpisani = MutableLiveData(Pair(0, 0))
        private set

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
            when (val resultGetGodine = repository.getGodine()) {

                is NetworkServiceResult.StudomatResult.Success -> {
                    godine.postValue(resultGetGodine.data)
                    selectedGodina.postValue(resultGetGodine.data[0])
                    val odabrana =
                        if (refresh) {
                            selectedGodina.value
                        } else {
                            resultGetGodine.data[0]
                        }
                    if (odabrana != null) {
                        this@StudomatViewModel.getOdabranuGodinu(odabrana)
                    }
                    loadedTxt.postValue("fetchedNew")
                }

                is NetworkServiceResult.StudomatResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    loadedTxt.postValue("fetchingError")
                }
            }
        }
    }

    fun getOdabranuGodinu(pair: Pair<String, String>, pulldownTriggered: Boolean = false) {
        if (pulldownTriggered) {
            isRefreshing.postValue(true)
        }
        loadedTxt.postValue("fetching")
        selectedGodina.postValue(pair)
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
                    if (pulldownTriggered) {
                        isRefreshing.postValue(false)
                    }
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    loadedTxt.postValue("fetchingError")
                    return@launch
                }
            }
            when (val result = repository.getOdabranuGodinu(pair)) {
                is NetworkServiceResult.StudomatResult.Success -> {

                    predmetList.postValue(result.data.first)
                    generated.postValue(result.data.second)
                    polozeniKrozUpisani.postValue(result.data.third)

                    loadedTxt.postValue("fetchedNew")//for refresh listener
                    /*delay(50)
                    _loadedTxt.postValue("fetchedOld")*/
                }

                is NetworkServiceResult.StudomatResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    loadedTxt.postValue("fetchingError")
                }
            }

            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
        }
    }
}