package com.tstudioz.fax.fme.feature.studomat.services

import android.util.Log
import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class StudomatLoginService(private val client: OkHttpClient): StudomatLoginServiceInterface {

    private var samlRequest = ""
    private var authState = ""
    private var samlResponseEncrypted = ""
    private var samlResponseDecrypted = ""

    override fun getSamlRequest(): NetworkServiceResult.StudomatResult {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/saml2/authenticate/isvu")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        val isSuccessful = response.isSuccessful
        response.close()

        samlRequest = doc?.selectFirst("input[name=SAMLRequest]")?.attr("value").toString()

        return if (isSuccessful && samlRequest != "") {
            Log.d("StudomatService", "getSamlRequest: $samlRequest")
            NetworkServiceResult.StudomatResult.Success("SAMLRequest got!")
        } else {
            Log.d("StudomatService", "getSamlRequest: Couldn't get SAMLRequest!")
            throw Throwable("Couldn't get SAMLRequest!")
        }
    }

    override fun sendSamlResponseToAAIEDU(): NetworkServiceResult.StudomatResult {
        val formBody = FormBody.Builder()
            .add("SAMLRequest", samlRequest)
            .build()
        val request = Request.Builder()
            .url("https://login.aaiedu.hr/isvu/saml2/idp/SSOService.php")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        val isSuccessful = response.isSuccessful
        response.close()

        authState = doc?.selectFirst("form.login-form")?.attr("action")?.substringAfter("AuthState=").toString()

        return if (isSuccessful && authState != "") {
            Log.d("StudomatService", "sendSamlResponseToAAIEDU: $authState")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse sent to AAIEDU!")
        } else {
            Log.d("StudomatService", "sendSamlResponseToAAIEDU: Couldn't send SAMLResponse to AAIEDU!")
            throw Throwable("Couldn't send SAMLResponse to AAIEDU!")
        }
    }

    override fun getSamlResponse(
        email: String,
        password: String
    ): NetworkServiceResult.StudomatResult {

        val formBody = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .add("AuthState", authState)
            .add("Submit", "")
            .build()
        val request = Request.Builder()
            .url("https://login.aaiedu.hr/sso/module.php/core/loginuserpass?AuthState=$authState")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        val isSuccessful = response.isSuccessful
        response.close()

        samlResponseEncrypted = doc?.selectFirst("input[name=SAMLResponse]")?.attr("value").toString()

        return if (isSuccessful && samlResponseEncrypted != "") {
            Log.d("StudomatService", "getSamlResponse: $samlResponseEncrypted")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse got!")
        } else {
            Log.d("StudomatService", "getSamlResponse: Couldn't get SAMLResponse!")
            throw Throwable("Couldn't get SAMLResponse!")
        }
    }

    override fun sendSAMLToDecrypt(): NetworkServiceResult.StudomatResult {

        val formBody = FormBody.Builder()
            .add("SAMLResponse", samlResponseEncrypted)
            .build()

        val request = Request.Builder()
            .url("https://login.aaiedu.hr/isvu/module.php/saml/sp/saml2-acs.php/default-sp")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        val isSuccessful = response.isSuccessful
        response.close()

        samlResponseDecrypted = doc?.selectFirst("input[name=SAMLResponse]")?.attr("value").toString()

        return if (isSuccessful && samlResponseDecrypted != "") {
            Log.d("StudomatService", "sendSAMLToDecrypt: $samlResponseDecrypted")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse decrypted!")
        } else {
            Log.d("StudomatService", "sendSAMLToDecrypt: Couldn't decrypt SAMLResponse!")
            throw Throwable("Couldn't decrypt SAMLResponse!")
        }
    }

    override fun sendSAMLToISVU(): NetworkServiceResult.StudomatResult {

        val formBody = FormBody.Builder()
            .add("SAMLResponse", samlResponseDecrypted)
            .build()

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/login/saml2/sso/isvu")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val isSuccessful = response.isSuccessful
        response.close()

        return if (isSuccessful) {
            Log.d("StudomatService", "sendSAMLToISVU: SAMLResponse sent to ISVU!")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse sent to ISVU!")
        } else {
            Log.d("StudomatService", "sendSAMLToISVU: Couldn't send SAMLResponse to ISVU!")
            throw Throwable("Couldn't send SAMLResponse to ISVU!")
        }
    }
}