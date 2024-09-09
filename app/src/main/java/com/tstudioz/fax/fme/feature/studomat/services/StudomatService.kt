package com.tstudioz.fax.fme.feature.studomat.services

import android.util.Log
import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class StudomatService {
    private val client: OkHttpClient = OkHttpClient.Builder().cookieJar(object : CookieJar {
        private val cookieStore = HashMap<String, MutableList<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            if (cookieStore[url.host] == null)
                cookieStore[url.host] = mutableListOf<Cookie>()
            cookies.forEach { cookie ->
                cookieStore[url.host]?.find { it.name == cookie.name }?.let {
                    cookieStore[url.host]?.remove(it)
                }
                cookieStore[url.host]?.add(cookie)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: ArrayList()
        }
    }).build()

    private var samlRequest = ""
    private var authState = ""
    private var samlResponseEncrypted = ""
    private var samlResponseDecrypted = ""
    var lastTimeLoggedIn = 0L

    fun resetLastTimeLoggedInCount() {
        lastTimeLoggedIn = 0L
    }

    fun getSamlRequest(): NetworkServiceResult.StudomatResult {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/saml2/authenticate/isvu")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        samlRequest = doc?.selectFirst("input[name=SAMLRequest]")?.attr("value").toString()
        return if (samlRequest != "") {
            Log.d("StudomatService", "getSamlRequest: $samlRequest")
            NetworkServiceResult.StudomatResult.Success("SAMLRequest got!")
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getSamlRequest: Couldn't get SAMLRequest!")
            //NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get SAMLRequest!"))
            throw Throwable("Couldn't get SAMLRequest!")
        }
    }

    fun sendSamlResponseToAAIEDU(): NetworkServiceResult.StudomatResult {
        val formBody = FormBody.Builder()
            .add("SAMLRequest", samlRequest)
            .build()
        val request = Request.Builder()
            .url("https://login.aaiedu.hr/isvu/saml2/idp/SSOService.php")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }

        authState = doc?.selectFirst("form.login-form")?.attr("action")?.substringAfter("AuthState=").toString()
        return if (response.isSuccessful && authState != "") {
            Log.d("StudomatService", "sendSamlResponseToAAIEDU: $authState")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse sent to AAIEDU!")
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "sendSamlResponseToAAIEDU: Couldn't send SAMLResponse to AAIEDU!")
            //NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't send SAMLResponse to AAIEDU!"))
            throw Throwable("Couldn't send SAMLResponse to AAIEDU!")
        }
    }

    fun getSamlResponse(
        username: String,
        password: String
    ): NetworkServiceResult.StudomatResult {

        val formBody = FormBody.Builder()
            .add("username", "$username@fesb.hr")
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
        samlResponseEncrypted = doc?.selectFirst("input[name=SAMLResponse]")?.attr("value").toString()
        return if (samlResponseEncrypted != "") {
            Log.d("StudomatService", "getSamlResponse: $samlResponseEncrypted")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse got!")
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getSamlResponse: Couldn't get SAMLResponse!")
            //NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get SAMLResponse!"))
            throw Throwable("Couldn't get SAMLResponse!")
        }
    }

    fun sendSAMLToDecrypt(): NetworkServiceResult.StudomatResult {

        val formBody = FormBody.Builder()
            .add("SAMLResponse", samlResponseEncrypted)
            .build()

        val request = Request.Builder()
            .url("https://login.aaiedu.hr/isvu/module.php/saml/sp/saml2-acs.php/default-sp")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        samlResponseDecrypted = doc?.selectFirst("input[name=SAMLResponse]")?.attr("value").toString()

        return if (samlResponseDecrypted != "") {
            Log.d("StudomatService", "sendSAMLToDecrypt: $samlResponseDecrypted")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse decrypted!")
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "sendSAMLToDecrypt: Couldn't decrypt SAMLResponse!")
            //NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't decrypt SAMLResponse!"))
            throw Throwable("Couldn't decrypt SAMLResponse!")
        }
    }

    fun sendSAMLToISVU(): NetworkServiceResult.StudomatResult {

        val formBody = FormBody.Builder()
            .add("SAMLResponse", samlResponseDecrypted)
            .build()

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/login/saml2/sso/isvu")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            Log.d("StudomatService", "sendSAMLToISVU: SAMLResponse sent to ISVU!")
            NetworkServiceResult.StudomatResult.Success("SAMLResponse sent to ISVU!")
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "sendSAMLToISVU: Couldn't send SAMLResponse to ISVU!")
            //NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't send SAMLResponse to ISVU!"))
            throw Throwable("Couldn't send SAMLResponse to ISVU!")
        }
    }

    fun getStudomatData(): NetworkServiceResult.StudomatResult {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/index")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""

        return if (response.code == 200) {
            lastTimeLoggedIn = System.currentTimeMillis()
            Log.d("StudomatService", "getStudomatData: ${body.substring(0, 100)}")
            NetworkServiceResult.StudomatResult.Success(body)
        } else if (Jsoup.parse(body).title() == "Studomat - Prijava") {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Not logged in!"))
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get Studomat data!"))
        }
    }

    fun getUpisaneGodine(): NetworkServiceResult.StudomatResult {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/studiranje/upisanegodine")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""

        return if (body != "") {
            Log.d("StudomatService", "getUpisaneGodine: ${body.substring(0, 100)}")
            NetworkServiceResult.StudomatResult.Success(body)
        } else if (Jsoup.parse(body).title() == "Studomat - Prijava") {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getStudomatData: Couldn't get Studomat data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Not logged in!"))
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getUpisaneGodine: Couldn't get upisane godine data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get upisane godine data!"))
        }
    }

    fun getTrenutnuGodinuData(href: String): NetworkServiceResult.StudomatResult {
        val request = Request.Builder()
            .url("https://www.isvu.hr$href")
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: ""
        return if (response.isSuccessful) {
            Log.d("StudomatService", "getTrenutnuGodinuData: ${body.substring(0, 100)}")
            NetworkServiceResult.StudomatResult.Success(body)
        } else {
            lastTimeLoggedIn = 0L
            Log.d("StudomatService", "getTrenutnuGodinuData: Couldn't get current year data!")
            NetworkServiceResult.StudomatResult.Failure(Throwable("Couldn't get current year data!"))
        }
    }
}