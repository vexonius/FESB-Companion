package com.tstudioz.fax.fme.feature.studomat.view

import android.content.SharedPreferences
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
import com.tstudioz.fax.fme.feature.studomat.repository.StudomatRepository
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.networking.NetworkUtils
import io.realm.kotlin.internal.platform.runBlocking
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StudomatViewModel(
    private val repository: StudomatRepository,
    private val sharedPreferences: SharedPreferences,
    private val networkUtils: NetworkUtils
) : ViewModel() {

    val isRefreshing = MutableLiveData(false)

    var subjectList = MutableLiveData<List<StudomatSubject>>(emptyList())
    private var loadedTxt = MutableLiveData(StudomatState.UNSET)
    var student = MutableLiveData(Student())
    var generated = MutableLiveData("")
    var years = MutableLiveData<List<Year>>(emptyList())
    var selectedYear = MutableLiveData(Year("", ""))
    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    val username = sharedPreferences.getString("username", "") ?: ""
    val password = sharedPreferences.getString("password", "") ?: ""

    val loading = loadedTxt.map { it == StudomatState.FETCHING || it == StudomatState.UNSET }
    val offline
        get() = !networkUtils.isNetworkAvailable()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        loadData()
        initStudomat()
    }

    private fun loadData() {
        runBlocking {
            val yearsRealm = repository.readYears().sortedByDescending { it.title }
            val latestYearSubjects = repository.read(yearsRealm.firstOrNull()?.title?.substringBefore(" ") ?: "")
            years.postValue(yearsRealm)
            subjectList.postValue(latestYearSubjects)
            generated.postValue(sharedPreferences.getString("gen" + yearsRealm.firstOrNull()?.title, ""))
        }
    }

    suspend fun login(pulldownTriggered: Boolean = false): Boolean {
        if (networkUtils.isNetworkAvailable()) {
            loadedTxt.postValue(StudomatState.FETCHING)
            return when (val result = repository.loginUser(username, password)) {
                is StudomatRepositoryResult.LoginResult.Success -> {
                    student.postValue(result.data)
                    true
                }

                is StudomatRepositoryResult.LoginResult.Failure -> {
                    if (pulldownTriggered) {
                        isRefreshing.postValue(false)
                    }
                    if (!result.throwable.contains("Already logging in!")) {
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    }
                    loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                    false
                }
            }
        } else {
            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
            snackbarHostState.showSnackbar("Nema interneta")
            loadedTxt.postValue(StudomatState.FETCHING_ERROR)
            return false
        }
    }


    fun initStudomat() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (!networkUtils.isNetworkAvailable()) {
                loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                snackbarHostState.showSnackbar("Nema interneta")
                return@launch
            } else {
                if (!login()) return@launch
                when (val result = repository.getYears()) {
                    is StudomatRepositoryResult.YearsResult.Success -> {
                        result.data.firstOrNull()?.let { getChosenYear(it) }
                        years.postValue(result.data)
                        selectedYear.postValue(result.data.firstOrNull())
                        loadedTxt.postValue(StudomatState.FETCHED)
                    }

                    is StudomatRepositoryResult.YearsResult.Failure -> {
                        loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                    }
                }
            }
        }
    }

    fun getChosenYear(year: Year, pulldownTriggered: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (pulldownTriggered) {
                isRefreshing.postValue(true)
            }
            val chosenYear =
                if (year.href.isEmpty() || year.title.isEmpty()) {
                    years.value?.firstOrNull()
                } else {
                    year
                }
            selectedYear.postValue(chosenYear)
            subjectList.postValue(repository.read(year.title.substringBefore(" ")))
            sharedPreferences.getString("gen" + year.title, "").let { generated.postValue(it) }
            if (chosenYear != null) {
                if (networkUtils.isNetworkAvailable()) {
                    if (!login(pulldownTriggered)) return@launch
                    when (val result = repository.getChosenYear(chosenYear)) {
                        is StudomatRepositoryResult.ChosenYearResult.Success -> {
                            loadedTxt.postValue(StudomatState.FETCHED)
                            subjectList.postValue(result.data.first)
                            generated.postValue(result.data.second)
                        }

                        is StudomatRepositoryResult.ChosenYearResult.Failure -> {
                            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                            loadedTxt.postValue(StudomatState.FETCHING_ERROR)
                        }
                    }
                }
            }
            if (pulldownTriggered) {
                isRefreshing.postValue(false)
            }
        }
    }

    enum class StudomatState {
        UNSET,
        FETCHING,
        FETCHED,
        FETCHING_ERROR
    }
}