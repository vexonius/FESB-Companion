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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import com.tstudioz.fax.fme.util.SPKey
import com.tstudioz.fax.fme.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.tstudioz.fax.fme.util.PreferenceHelper.set

class SettingsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepositoryInterface,
    private val application: Application,
    private val dao: SettingsDao
) : AndroidViewModel(application) {

    val username: MutableLiveData<String> = MutableLiveData()
    val version: MutableLiveData<String> = MutableLiveData()
    val displayLicences = MutableLiveData(false)
    val intentEvent = SingleLiveEvent<Intent>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            username.postValue(userRepository.getCurrentUserName())
            version.postValue(getBuildVersion())
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences[SPKey.LOGGED_IN] = false
            sharedPreferences[SPKey.FIRST_TIME] = true
            sharedPreferences.edit().remove("gen").apply()
            dao.deleteAll()
        }
    }

    fun sendFeedbackEmail(titleId: Int) {
        val emailsend = getString(application.applicationContext, R.string.support_email)
        val emailsubject = "${getString(application.baseContext, titleId)} v${version.value}"
        val emailbody = ""
        val intentTitle = getString(application, R.string.send_mail_using)

        val intent = Intent(Intent.ACTION_SEND)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(emailsend))
            .putExtra(Intent.EXTRA_SUBJECT, emailsubject)
            .putExtra(Intent.EXTRA_TEXT, emailbody)
            .setType("message/rfc822")

        intentEvent.value = intent
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
        val dataPrivacyUrl = getString(context, R.string.data_privacy_url)
        customTabsIntent.launchUrl(
            context,
            Uri.parse(dataPrivacyUrl)
        )
    }

    fun displayLicensesDialog() {
        displayLicences.postValue(true)
    }

    fun hideLicensesDialog() {
        displayLicences.postValue(false)
    }
}
