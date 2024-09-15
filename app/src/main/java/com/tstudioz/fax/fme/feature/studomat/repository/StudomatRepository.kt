package com.tstudioz.fax.fme.feature.studomat.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.data.parseStudent
import com.tstudioz.fax.fme.feature.studomat.data.parseCurrentYear
import com.tstudioz.fax.fme.feature.studomat.data.parseYears
import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.models.NetworkServiceResult
import com.tstudioz.fax.fme.random.NetworkUtils

class StudomatRepository(
    private val studomatService: StudomatService,
    private val studomatDao: StudomatDao,
    private val networkUtils: NetworkUtils,
    private val sharedPreferences: SharedPreferences
) {

    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    var subjectList = MutableLiveData<List<StudomatSubject>>(emptyList())
    var loadedTxt = MutableLiveData("unset")
    var student = MutableLiveData(Student())
    var generated = MutableLiveData("")
    var years = MutableLiveData<List<Year>>(emptyList())
    var selectedYear = MutableLiveData(Year("", ""))

    suspend fun loginUser(
        username: String,
        password: String,
        forceLogin: Boolean
    ): StudomatRepositoryResult.LoginResult {

        if (username == "" || password == "") {
            return StudomatRepositoryResult.LoginResult.Failure("Username or password is empty")
        }

        try {
            Log.d("StudomatRepository", "loginUser: ${System.currentTimeMillis() - studomatService.lastTimeLoggedIn}")
            Log.d("StudomatRepository", "System.currentTimeMillis(): ${System.currentTimeMillis()}")
            Log.d("StudomatRepository", "lastTimeLoggedIn: ${studomatService.lastTimeLoggedIn}")

            if ((System.currentTimeMillis() - studomatService.lastTimeLoggedIn) > 3600000 || forceLogin) { //check this, mabye it should be less time? or check the login status with a network call
                Log.d("StudomatRepository", "loginUser: logging in")
                studomatService.getSamlRequest()
                studomatService.sendSamlResponseToAAIEDU()
                studomatService.getSamlResponse(username, password)
                studomatService.sendSAMLToDecrypt()
                studomatService.sendSAMLToISVU()
            }
            return when (val result = studomatService.getStudomatData()) {
                is NetworkServiceResult.StudomatResult.Success -> {
                    StudomatRepositoryResult.LoginResult.Success(
                        parseStudent(result.data)
                    )
                }

                is NetworkServiceResult.StudomatResult.Failure -> {
                    Log.d("StudomatRepository", "loginUser: ${result.throwable.message}")
                    StudomatRepositoryResult.LoginResult.Failure("Failure getting data:${result.throwable.message}")
                }
            }
        } catch (t: Throwable) { //should throwable be here?? or just Exception or should i change the way i handle exceptions
            studomatService.resetLastTimeLoggedInCount()
            Log.d("StudomatRepository", "loginUser: ${t.message}")
            return StudomatRepositoryResult.LoginResult.Failure("Failure: ${t.message}")
        }
    }

    suspend fun getYears(offline: Boolean = false): StudomatRepositoryResult.YearsResult {
        if (offline) {
            return StudomatRepositoryResult.YearsResult.Failure("No internet connection")
        }
        return when (val result = studomatService.getUpisaneGodine()) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val resultGetGodine = parseYears(result.data)
                studomatDao.insertYears(resultGetGodine)
                years.postValue(resultGetGodine)
                selectedYear.postValue(resultGetGodine.firstOrNull())
                loadedTxt.postValue("fetchedNew")
                Log.d("StudomatRepository", "getYears: $resultGetGodine")
                StudomatRepositoryResult.YearsResult.Success(resultGetGodine)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                loadedTxt.postValue("fetchingError")
                Log.d("StudomatRepository", "getYears: ${result.throwable.message}")
                StudomatRepositoryResult.YearsResult.Failure("Failure getting data:${result.throwable.message}")
            }
        }
    }

    suspend fun getChosenYear(year: Year, offline: Boolean = false) {
        subjectList.postValue(studomatDao.read(year.title.substringBefore(" ")))
        sharedPreferences.getString("gen" + year.title, "").let {
            generated.postValue(it)
        }
        if (offline) {
            return
        }
        when (val data1 = studomatService.getTrenutnuGodinuData(year.href)) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val result = parseCurrentYear(data1.data)
                loadedTxt.postValue("fetchedNew")
                subjectList.postValue(result.first)
                generated.postValue(result.second)
                sharedPreferences.edit().putString("gen" + year.title, result.second).apply()
                studomatDao.insert(result.first)
                Log.d("StudomatRepository", "getOdabranuGodinu: ${result.first}")
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                loadedTxt.postValue("fetchingError")
                Log.d("StudomatRepository", "getOdabranuGodinu: ${data1.throwable.message}")
            }
        }
    }

    suspend fun initRepo() {
        val yearsRealm = studomatDao.readYears().sortedByDescending { it.title }
        val latestYearSubjects = studomatDao.read(yearsRealm.firstOrNull()?.title?.substringBefore(" ") ?: "")
        years.postValue(yearsRealm)
        subjectList.postValue(latestYearSubjects)
        generated.postValue(sharedPreferences.getString("gen" + yearsRealm.firstOrNull()?.title, ""))
    }
}