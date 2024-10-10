package com.tstudioz.fax.fme.feature.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepositoryInterface,
    private val application: Application,
    private val dao: SettingsDao
) : AndroidViewModel(application) {

    val username: MutableLiveData<String> = MutableLiveData()
    val version: MutableLiveData<String> = MutableLiveData()
    val displayLicences = MutableLiveData(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            username.postValue(userRepository.getCurrentUserName())
            version.postValue(getBuildVersion())
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences.edit().clear().apply()
            dao.deleteAll()
        }
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    fun sendFeedbackEmail(context: Context, titleId: Int) {
        val emailsend = getString(context, R.string.support_email)
        val emailsubject = "${getString(context, titleId)} v${version.value}"
        val emailbody = ""

        val intent = Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(emailsend))
            .putExtra(Intent.EXTRA_SUBJECT, emailsubject)
            .putExtra(Intent.EXTRA_TEXT, emailbody)
            .setType("message/rfc822")
        startActivity(
            context,
            Intent.createChooser(intent, getString(context, R.string.send_mail_using)),
            null
        )
    }

    private fun getBuildVersion(): String {
        return try {
            val packageInfo = application.applicationContext.packageManager.getPackageInfo(
                application.applicationContext.packageName,
                0
            )
            packageInfo.versionName ?: "undefined"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "undefined"
        }
    }

    fun launchCustomTab(context: Context) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.setToolbarColor(
            ContextCompat.getColor(context, R.color.colorPrimaryDark)
        ).build()
        customTabsIntent.launchUrl(
            context,
            Uri.parse(getString(context, R.string.data_privacy_url))
        )
    }

    fun displayLicensesDialog() {
        displayLicences.postValue(true)
    }

    fun hideLicensesDialog() {
        displayLicences.postValue(false)
    }
}
