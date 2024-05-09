package com.tstudioz.fax.fme.feature.merlin.services

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.database.Course
import com.tstudioz.fax.fme.models.util.ColorDeserializer
import com.tstudioz.fax.fme.models.util.LocalDateDeserializer
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import java.time.LocalDate

class MerlinService(private val client: OkHttpClient) : MerlinServiceInterface {

    private var AuthState = ""
    private var SAMLResponse = ""
    var sesskey = ""

    override suspend fun runSegmented(email: String, password: String): MerlinNetworkServiceResult.MerlinNetworkResult {
        getAuthState()
        login(email, password)
        getSimpleAuthToken()
        return getEnrolledCourses()
    }
    override suspend fun getAuthState() : MerlinNetworkServiceResult.MerlinNetworkResult {
        val request = Request.Builder()
            .url("https://moodle.srce.hr/2023-2024/auth/simplesaml/index.php?sp=default-sp&edugain=0")
            .build()

        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")

        AuthState = doc.select("form.login-form").attr("action").split("AuthState=")[1]
        return if (response.isSuccessful) {
            MerlinNetworkServiceResult.MerlinNetworkResult.Success("Success getAuthState")
        } else {
            MerlinNetworkServiceResult.MerlinNetworkResult.Failure("Failure getAuthState")
        }
    }

    override suspend fun login(email: String, password: String) : MerlinNetworkServiceResult.MerlinNetworkResult{
        val formBody1 = FormBody.Builder()
            .add("username", email)
            .add("password", password)
            .add("AuthState", AuthState)
            .add("Submit", "")
            .build()
        val request1 = Request.Builder()
            .url("https://login.aaiedu.hr/sso/module.php/core/loginuserpass?AuthState=${AuthState}")
            .post(formBody1)
            .build()

        val response = client.newCall(request1).execute()
        val doc1 = Jsoup.parse(response.body?.string() ?: "")
        SAMLResponse = doc1.select("input[name=SAMLResponse]").attr("value")

        return if (response.isSuccessful) {
            MerlinNetworkServiceResult.MerlinNetworkResult.Success("Success merlin login")
        } else {
            MerlinNetworkServiceResult.MerlinNetworkResult.Failure("Failure merlin login")
        }
    }

    override suspend fun getSimpleAuthToken() : MerlinNetworkServiceResult.MerlinNetworkResult{
        val formBody = FormBody.Builder()
            .add("SAMLResponse", SAMLResponse)
            .add("Submit", "")
            .build()
        val request = Request.Builder()
            .url("https://moodle.srce.hr/simplesaml/module.php/saml/sp/saml2-acs.php/default-sp")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")

        sesskey =
            doc.select("script").toArray().firstOrNull { it.toString().contains("sesskey") }.toString()
                .split("sesskey\":\"")[1].split("\"")[0]
        val sesskeyTimeout =
            doc.select("script").toArray().firstOrNull { it.toString().contains("sessiontimeout") }.toString()
                .split("sessiontimeout\":\"")[1].split("\"")[0]

        return if (response.isSuccessful) {
            MerlinNetworkServiceResult.MerlinNetworkResult.Success("Success merlin getSimpleAuthToken")
        } else {
            MerlinNetworkServiceResult.MerlinNetworkResult.Failure("Failure merlin getSimpleAuthToken")
        }
    }

    override suspend fun getEnrolledCourses() : MerlinNetworkServiceResult.MerlinNetworkResult{
        val request = Request.Builder()
            .url("https://moodle.srce.hr/2023-2024/lib/ajax/service.php?sesskey=${sesskey}&info=core_course_get_enrolled_courses_by_timeline_classification")
            .post("[{\"index\":0,\"methodname\":\"core_course_get_enrolled_courses_by_timeline_classification\",\"args\":{\"offset\":0,\"limit\":0,\"classification\":\"all\",\"sort\":\"ul.timeaccess desc\",\"customfieldname\":\"\",\"customfieldvalue\":\"\"}}]".toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")

        val json = doc.select("body").text().split("data\":{\"courses\":")[1].split(",\"nextoffset\":7}}]")[0]

        val gson = GsonBuilder()
            .create()
        val courses = gson.fromJson(json, Array<Course>::class.java).toList()



        val ids = doc.toString().split("\"id\":").forEachIndexed() { index, it ->
            if (index != 0)
                getCourseDetails(it.split(',')[0].toInt())
        }

        return if (response.isSuccessful) {
            MerlinNetworkServiceResult.MerlinNetworkResult.Success(courses)
        } else {
            MerlinNetworkServiceResult.MerlinNetworkResult.Failure("Failure merlin getEnrolledCourses")
        }
    }

    override suspend fun getCourseDetails(courseID: Int) {
        val request = Request.Builder()
            .url("https://moodle.srce.hr/2023-2024/lib/ajax/service.php?sesskey=${sesskey}&info=core_courseformat_get_state\n")
            .post("[{\"index\":0,\"methodname\":\"core_courseformat_get_state\",\"args\":{\"courseid\":${courseID}}}]".toRequestBody())
            //.post("[{ \"index\": 0,\"methodname\":\"core_courseformat_get_state\",\"args\": { \"action\": \"section_content_collapsed\",\"courseid\": \"${courseID}\",\"ids\": [] } }]".toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")

        if (response.isSuccessful) {
            println("Success getEnrolledCourses")
        } else {
            println("Failure getEnrolledCourses")
        }
    }
}