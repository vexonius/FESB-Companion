package com.tstudioz.fax.fme.feature.studomat.services

import com.tstudioz.fax.fme.models.NetworkServiceResult

interface StudomatLoginServiceInterface {

    fun getSamlRequest(): NetworkServiceResult.StudomatResult

    fun sendSamlResponseToAAIEDU(): NetworkServiceResult.StudomatResult

    fun getSamlResponse(email: String, password: String): NetworkServiceResult.StudomatResult

    fun sendSAMLToDecrypt(): NetworkServiceResult.StudomatResult

    fun sendSAMLToISVU(): NetworkServiceResult.StudomatResult

}