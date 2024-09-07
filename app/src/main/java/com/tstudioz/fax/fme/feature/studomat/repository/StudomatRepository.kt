package com.tstudioz.fax.fme.feature.studomat.repository

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.MutableLiveData
import com.tstudioz.fax.fme.feature.studomat.data.parseStudent
import com.tstudioz.fax.fme.feature.studomat.data.parseTrenutnuGodinu
import com.tstudioz.fax.fme.feature.studomat.data.parseUpisaneGodine
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Predmet
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Student
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.models.NetworkServiceResult

class StudomatRepository(private val studomatService: StudomatService) {

    val snackbarHostState: SnackbarHostState = SnackbarHostState()

    var predmetList = MutableLiveData<List<Predmet>>(emptyList())
    var loadedTxt = MutableLiveData("unset")
    var student = MutableLiveData(Student())
    var generated = MutableLiveData("")
    var godine = MutableLiveData<List<Pair<String, String>>>(emptyList())
    var selectedGodina = MutableLiveData(Pair("", ""))
    var polozeniKrozUpisani = MutableLiveData(Pair(0, 0))

    suspend fun loginUser(username: String, password: String, forceLogin:Boolean): StudomatRepositoryResult.LoginResult {

        if (username == "" || password == ""){
            return StudomatRepositoryResult.LoginResult.Failure("Username or password is empty")
        }

        try {
            if ((System.currentTimeMillis() - studomatService.lastTimeLoggedIn )> 3600000 || forceLogin) {
                studomatService.getSamlRequest()
                studomatService.sendSamlResponseToAAIEDU()
                studomatService.getSamlResponse(username, password)
                studomatService.sendSAMLToDecrypt()
                studomatService.sendSAMLToISVU()
            }
            return when (val result = studomatService.getStudomatData()) {
                is NetworkServiceResult.StudomatResult.Success -> { StudomatRepositoryResult.LoginResult.Success(
                    parseStudent(result.data)
                ) }
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

    suspend fun getYears() {
        when (val result = studomatService.getUpisaneGodine()) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val resultGetGodine = parseUpisaneGodine(result.data)
                godine.postValue(resultGetGodine)
                selectedGodina.postValue(resultGetGodine.firstOrNull())
                loadedTxt.postValue("fetchedNew")
                Log.d("StudomatRepository", "getYears: $resultGetGodine")
            }
            is NetworkServiceResult.StudomatResult.Failure -> {
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                loadedTxt.postValue("fetchingError")
                Log.d("StudomatRepository", "getYears: ${result.throwable.message}")
            }
        }
    }

    suspend fun getOdabranuGodinu(pair: Pair<String, String>){
        when (val data1 = studomatService.getTrenutnuGodinuData(pair.second)) {
            is NetworkServiceResult.StudomatResult.Success -> {
                val result = parseTrenutnuGodinu(data1.data)
                predmetList.postValue(result.first)
                generated.postValue(result.second)
                polozeniKrozUpisani.postValue(result.third)
                loadedTxt.postValue("fetchedNew")
                //for refresh listener
                /*delay(50)
                _loadedTxt.postValue("fetchedOld")*/
                Log.d("StudomatRepository", "getOdabranuGodinu: ${result.first}")
            }
            is NetworkServiceResult.StudomatResult.Failure -> {
                snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
                loadedTxt.postValue("fetchingError")
                Log.d("StudomatRepository", "getOdabranuGodinu: ${data1.throwable.message}")
            }
        }

    }
}