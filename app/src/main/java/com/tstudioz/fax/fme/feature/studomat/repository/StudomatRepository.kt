package com.tstudioz.fax.fme.feature.studomat.repository

import com.example.studomatisvu.model.data.parseStudent
import com.example.studomatisvu.model.data.parseTrenutnuGodinu
import com.example.studomatisvu.model.data.parseUpisaneGodine
import com.example.studomatisvu.model.dataclasses.Predmet
import com.tstudioz.fax.fme.feature.studomat.repository.models.StudomatRepositoryResult
import com.tstudioz.fax.fme.feature.studomat.services.StudomatService
import com.tstudioz.fax.fme.models.NetworkServiceResult

class StudomatRepository(private val studomatService: StudomatService) {

    suspend fun loginUser(username: String, password: String, forceLogin:Boolean): StudomatRepositoryResult.LoginResult {

        if (username == "" || password == ""){
            return StudomatRepositoryResult.LoginResult.Failure("Username or password is empty")
        }

        try {
            if ((System.currentTimeMillis() - studomatService.lastTimeLoggedIn )> 3600000 || forceLogin) {
                when (val result = studomatService.getSamlRequest()) {
                    is NetworkServiceResult.StudomatResult.Success -> {}
                    is NetworkServiceResult.StudomatResult.Failure -> return StudomatRepositoryResult.LoginResult.Failure("Failure getting SAMLRequest: ${result.error}")
                }
                when (val result = studomatService.getSamlResponse(username, password)) {
                    is NetworkServiceResult.StudomatResult.Success -> {}
                    is NetworkServiceResult.StudomatResult.Failure -> return StudomatRepositoryResult.LoginResult.Failure("Failure getting SAMLResponse: ${result.error}")
                }
                when (val result = studomatService.sendSAMLToDecrypt()) {
                    is NetworkServiceResult.StudomatResult.Success -> {}
                    is NetworkServiceResult.StudomatResult.Failure -> return StudomatRepositoryResult.LoginResult.Failure("Failure getting SAMLRequestDecrypted: ${result.error}")
                }
                when (val result = studomatService.sendSAMLToISVU()) {
                    is NetworkServiceResult.StudomatResult.Success -> {}
                    is NetworkServiceResult.StudomatResult.Failure -> return StudomatRepositoryResult.LoginResult.Failure("Failure sending SAMLRequest to ISVU: ${result.error}")
                }
            }
            return when (val result = studomatService.getStudomatData()) {
                is NetworkServiceResult.StudomatResult.Success -> { StudomatRepositoryResult.LoginResult.Success(parseStudent(result.data)) }
                is NetworkServiceResult.StudomatResult.Failure -> StudomatRepositoryResult.LoginResult.Failure("Failure getting data:${result.error}")
            }

        } catch (e: Exception) {
            studomatService.resetLastTimeLoggedInCount()
            return StudomatRepositoryResult.LoginResult.Failure("Failure: ${e.message}")
        }
    }

    suspend fun getGodine(): NetworkServiceResult.StudomatResult<List<Pair<String, String>>> {
        return when (val result = studomatService.getUpisaneGodine()) {
            is NetworkServiceResult.StudomatResult.Success -> NetworkServiceResult.StudomatResult.Success(
                parseUpisaneGodine(result.data)
            )

            is NetworkServiceResult.StudomatResult.Failure -> NetworkServiceResult.StudomatResult.Failure(
                "Failure getting upisane godine: ${result.error}"
            )
        }
    }

    suspend fun getOdabranuGodinu(pair: Pair<String, String>): NetworkServiceResult.StudomatResult<Triple<MutableList<Predmet>, String, Pair<Int, Int>>> {
        return when (val data1 = studomatService.getTrenutnuGodinuData(pair.second)) {
            is NetworkServiceResult.StudomatResult.Success ->  NetworkServiceResult.StudomatResult.Success(parseTrenutnuGodinu(data1.data))
            is NetworkServiceResult.StudomatResult.Failure -> NetworkServiceResult.StudomatResult.Failure("Failure getting trenutna godina: ${data1.error}")
        }

    }
}