package com.tstudioz.fax.fme.feature.settings

import android.content.SharedPreferences
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SettingsService(private val client: OkHttpClient, private val sharedPreferences: SharedPreferences) {

    fun sendBluetoothAddr(bleAddr: String): String {
        try {
            val name = sharedPreferences.getString("student", "")
            val json = JSONObject()
            json.put("name", name ?: "")
            json.put("mac_address", bleAddr)

            val requestBody = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("http://52.158.40.190/add_student")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            response.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return "Failed"
        }
        return "Success"
    }
}