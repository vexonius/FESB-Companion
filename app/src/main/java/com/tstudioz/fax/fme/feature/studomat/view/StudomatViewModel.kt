package com.tstudioz.fax.fme.feature.studomat.view

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.example.studomatisvu.model.dataclasses.Exam
import com.example.studomatisvu.model.dataclasses.Predmet
import com.example.studomatisvu.model.dataclasses.Student
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.models.NetworkServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudomatViewModel(private val repository: StudomatRepository, context: Context, private val sharedPreferences: SharedPreferences) : ViewModel() {

    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    private val _exam = MutableLiveData<Exam>().apply { value = Exam() }
    val exam: LiveData<Exam> = _exam

    private val _predmetList = MutableLiveData<List<Predmet>>().apply { value = emptyList() }
    val predmetList: LiveData<List<Predmet>> = _predmetList.distinctUntilChanged()

    private val _loadedTxt = MutableLiveData<String>().apply { value = "unset" }
    val loadedTxt: LiveData<String> = _loadedTxt

    private val _student = MutableLiveData<Student>().apply { value = Student() }
    val student: LiveData<Student> = _student

    private val _isRefreshing: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _generated = MutableLiveData<String>().apply { value = "" }
    val generated: LiveData<String> = _generated

    private val _godine =
        MutableLiveData<List<Pair<String, String>>>().apply { value = emptyList() }
    val godine: LiveData<List<Pair<String, String>>> = _godine

    private val _selectedGodina =
        MutableLiveData<Pair<String, String>>().apply { value = Pair("", "") }
    val selectedGodina: LiveData<Pair<String, String>> = _selectedGodina

    private val _polozeniKrozUpisani =
        MutableLiveData<Pair<Int, Int>>().apply { value = Pair(0, 0) }
    val polozeniKrozUpisani: LiveData<Pair<Int, Int>> = _polozeniKrozUpisani

    fun getStudomatData(refresh: Boolean = false) {
        _loadedTxt.postValue("fetching")
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.loginUser(
                sharedPreferences.getString("username", "") ?: "",
                sharedPreferences.getString("password", "") ?: "",
                false
            )) {
                is StudomatRepositoryResult.LoginResult.Success -> {
                    _student.postValue(result.data as Student)
                }

                is StudomatRepositoryResult.LoginResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    _loadedTxt.postValue("fetchingError")
                    return@launch
                }
            }
            when (val resultGetGodine = repository.getGodine()) {

                is NetworkServiceResult.StudomatResult.Success -> {

                    _godine.postValue(resultGetGodine.data)
                    _selectedGodina.postValue(resultGetGodine.data[0])
                    val odabrana =
                        if (refresh) { selectedGodina.value }
                    else { resultGetGodine.data[0] } //od ima cudan bug di baca gresku jer misi da se ne moze pristupit preko [0]
                    if (odabrana != null) {
                        this@StudomatViewModel.getOdabranuGodinu(odabrana)
                    }

                    _loadedTxt.postValue("fetchedNew")//for refresh listener
                    /*delay(50)
                    _loadedTxt.postValue("fetchedOld")*/
                }

                is NetworkServiceResult.StudomatResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    _loadedTxt.postValue("fetchingError")
                }
            }
        }
    }

    fun getOdabranuGodinu(pair: Pair<String, String>, pulldownTriggered: Boolean = false) {
        if (pulldownTriggered) {
            _isRefreshing.postValue(true)
        }
        _loadedTxt.postValue("fetching")
        _selectedGodina.postValue(pair)
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.loginUser(
                sharedPreferences.getString("username", "") ?: "",
                sharedPreferences.getString("password", "") ?: "",
                false
            )) {
                is StudomatRepositoryResult.LoginResult.Success -> { _student.postValue(result.data as Student) }

                is StudomatRepositoryResult.LoginResult.Failure -> {
                    if (pulldownTriggered) { _isRefreshing.postValue(false) }
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    _loadedTxt.postValue("fetchingError")
                    return@launch
                }
            }
            when (val result = repository.getOdabranuGodinu(pair)) {
                is NetworkServiceResult.StudomatResult.Success -> {

                    _predmetList.postValue(result.data.first)
                    _generated.postValue(result.data.second)
                    _polozeniKrozUpisani.postValue(result.data.third)

                    _loadedTxt.postValue("fetchedNew")//for refresh listener
                    /*delay(50)
                    _loadedTxt.postValue("fetchedOld")*/
                }

                is NetworkServiceResult.StudomatResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    _loadedTxt.postValue("fetchingError")
                }
            }

            if (pulldownTriggered) {
                _isRefreshing.postValue(false)
            }
        }
    }
}