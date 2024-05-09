package com.tstudioz.fax.fme.feature.merlin.services

import com.tstudioz.fax.fme.models.NetworkServiceResult
import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup

interface MerlinServiceInterface {

    suspend fun runSegmented(email: String, password: String): MerlinNetworkServiceResult.MerlinNetworkResult

    suspend fun getAuthState() : MerlinNetworkServiceResult.MerlinNetworkResult

    suspend fun login(email: String, password: String) : MerlinNetworkServiceResult.MerlinNetworkResult

    suspend fun getSimpleAuthToken() : MerlinNetworkServiceResult.MerlinNetworkResult

    suspend fun getEnrolledCourses() : MerlinNetworkServiceResult.MerlinNetworkResult

    suspend fun getCourseDetails(courseID: Int): MerlinNetworkServiceResult.MerlinNetworkResult
}

