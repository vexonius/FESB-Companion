package com.tstudioz.fax.fme.feature.studomat.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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
    private var samlResponseEncrypted = ""
    private var samlResponseDecrypted = ""
    var lastTimeLoggedIn = 0L

    fun resetLastTimeLoggedInCount() {
        lastTimeLoggedIn = 0L
    }

    fun getSamlRequest(): NetworkServiceResult.StudomatResult<String> {

        val request = Request.Builder()
            .url("https://www.isvu.hr/studomat/saml2/authenticate/isvu")
            .build()

        val response = client.newCall(request).execute()
        val doc = response.body?.string()?.let { Jsoup.parse(it) }
        samlRequest = doc?.select("input[name=SAMLRequest]")?.attr("value").toString()
        return if (samlRequest != "") {
            NetworkServiceResult.StudomatResult.Success("SAMLRequest got!")
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't get SAMLRequest!")
        }
    }

    fun getSamlResponse(
        username: String,
        password: String
    ): NetworkServiceResult.StudomatResult<String> {

        val formBody15 = FormBody.Builder()
            .add("SAMLRequest", samlRequest)
            .build()
        val request15 = Request.Builder()
            .url("https://login.aaiedu.hr/isvu/saml2/idp/SSOService.php")
            .post(formBody15)
            .build()

        val response15 = client.newCall(request15).execute()
        val doc15 = response15.body?.string()?.let { Jsoup.parse(it) }

        val loginLink = doc15?.select("form.login-form")?.attr("action").toString()
        val formBody18 = FormBody.Builder()
            .add("username", "$username@fesb.hr")
            .add("password", password)
            .add("AuthState", loginLink.split("AuthState=")[1])
            .add("Submit", "")
            .build()
        val request18 = Request.Builder()
            .url(loginLink)
            .post(formBody18)
            .build()

        val response18 = client.newCall(request18).execute()
        val doc18 = response18.body?.string()?.let { Jsoup.parse(it) }
        samlResponseEncrypted = doc18?.select("input[name=SAMLResponse]")?.attr("value").toString()
        return if (samlResponseEncrypted != "") {
            NetworkServiceResult.StudomatResult.Success("SAMLResponse got!")
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't get SAMLResponse!")
        }
    }

    fun sendSAMLToDecrypt(): NetworkServiceResult.StudomatResult<String> {

        val formBody3 = FormBody.Builder()
            .add("SAMLResponse", samlResponseEncrypted)
            .build()

        val request3 = Request.Builder()
            .url("https://login.aaiedu.hr/isvu/module.php/saml/sp/saml2-acs.php/default-sp")
            .post(formBody3)
            .build()

        val response3 = client.newCall(request3).execute()
        val doc3 = response3.body?.string()?.let { Jsoup.parse(it) }
        samlResponseDecrypted = doc3?.select("input[name=SAMLResponse]")?.attr("value").toString()

        return if (samlResponseDecrypted != "") {
            NetworkServiceResult.StudomatResult.Success("SAMLResponse decrypted!")
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't decrypt SAMLResponse!")
        }
    }

    fun sendSAMLToISVU(): NetworkServiceResult.StudomatResult<String> {

        val formBody4 = FormBody.Builder()
            .add("SAMLResponse", samlResponseDecrypted)
            .build()

        val request4 = Request.Builder()
            .url("https://www.isvu.hr/studomat/login/saml2/sso/isvu")
            .post(formBody4)
            .build()

        val response4 = client.newCall(request4).execute()
        return if (response4.isSuccessful) {
            NetworkServiceResult.StudomatResult.Success("SAMLResponse sent to ISVU!")
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't send SAMLResponse to ISVU!")
        }
    }

    fun getStudomatData(): NetworkServiceResult.StudomatResult<Document> {

        val request5 = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/index")
            .build()
        val response5 = client.newCall(request5).execute()
        val doc = response5.body?.string()?.let { Jsoup.parse(it) } ?: Jsoup.parse("")

        return if (response5.code == 200) {
            lastTimeLoggedIn = System.currentTimeMillis()
            NetworkServiceResult.StudomatResult.Success(doc)
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't get Studomat data!")
        }
    }

    fun getUpisaneGodine(): NetworkServiceResult.StudomatResult<Document> {

        val request8 = Request.Builder()
            .url("https://www.isvu.hr/studomat/hr/studiranje/upisanegodine")
            .build()
        val response8 = client.newCall(request8).execute()
        val doc8 = response8.body?.string()?.let { Jsoup.parse(it) } ?: Jsoup.parse("")
        return if (doc8.toString() != "") {
            NetworkServiceResult.StudomatResult.Success(doc8)
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't get upisane godine data!")
        }
    }

    fun getTrenutnuGodinuData(href: String): NetworkServiceResult.StudomatResult<Document> {
        val request8 = Request.Builder()
            .url("https://www.isvu.hr$href")
            .build()
        val response8 = client.newCall(request8).execute()
        return if (response8.isSuccessful) {
            NetworkServiceResult.StudomatResult.Success(
                response8.body?.string()?.let { Jsoup.parse(it) } ?: Jsoup.parse("")
            )
        } else {
            lastTimeLoggedIn = 0L
            NetworkServiceResult.StudomatResult.Failure("Couldn't get current year data!")
        }
    }
}