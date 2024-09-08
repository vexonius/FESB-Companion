package com.tstudioz.fax.fme.feature.studomat.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.feature.studomat.dao.StudomatDao
import com.tstudioz.fax.fme.feature.studomat.data.parseStudent
import com.tstudioz.fax.fme.feature.studomat.data.parseTrenutnuGodinu
import com.tstudioz.fax.fme.feature.studomat.data.parseUpisaneGodine
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Student
import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Year
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
    var selectedGodina = MutableLiveData(Year("", ""))
    var polozeniKrozUpisani = MutableLiveData(Pair(0, 0))

    suspend fun loginUser(
        username: String,
        password: String,
        forceLogin: Boolean
    ): StudomatRepositoryResult.LoginResult {

        if (username == "" || password == "") {
            return StudomatRepositoryResult.LoginResult.Failure("Username or password is empty")
        }

        try {
            if ((System.currentTimeMillis() - studomatService.lastTimeLoggedIn) > 3600000 || forceLogin) {
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
                val resultGetGodine = parseUpisaneGodine(result.data)
                insertYears(resultGetGodine)
                years.postValue(resultGetGodine)
                selectedGodina.postValue(resultGetGodine.firstOrNull())
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
        subjectList.postValue(read(year.title.substringBefore(" ")))
        sharedPreferences.getString("gen" + year.title, "").let {
            generated.postValue(it)
        }
        if (offline) {
            return
        }
        when (val data1 = studomatService.getTrenutnuGodinuData(year.href)) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val result = parseTrenutnuGodinu(data1.data)
                subjectList.postValue(result.first)
                generated.postValue(result.second)
                sharedPreferences.edit().putString("gen" + year.title, result.second).apply()
                polozeniKrozUpisani.postValue(result.third)
                loadedTxt.postValue("fetchedNew")
                Log.d("StudomatRepository", "getOdabranuGodinu: ${result.first}")
                insert(result.first)
            }

            is NetworkServiceResult.StudomatResult.Failure -> {
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                loadedTxt.postValue("fetchingError")
                Log.d("StudomatRepository", "getOdabranuGodinu: ${data1.throwable.message}")
            }
        }
    }

    suspend fun loadFromDb() {
        val yearsRealm = readYears().sortedByDescending { it.title }
        years.postValue(yearsRealm)
        subjectList.postValue(read(yearsRealm.firstOrNull()?.title?.substringBefore(" ") ?: ""))
        generated.postValue(sharedPreferences.getString("gen" + yearsRealm.firstOrNull()?.title, ""))
    }

    suspend fun insert(subjects: List<StudomatSubject>) {
        studomatDao.insert(subjects)
    }

    suspend fun insertYears(years: List<Year>) {
        studomatDao.insertYears(years)
    }

    suspend fun read(year: String): List<StudomatSubject> {
        return studomatDao.read(year)
    }

    suspend fun readYears(): List<Year> {
        return studomatDao.readYears()
    }
}