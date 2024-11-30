package com.tstudioz.fax.fme.feature.settings

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepositoryInterface
import com.tstudioz.fax.fme.feature.settings.model.EmailModalModel
import com.tstudioz.fax.fme.util.SPKey
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

    fun displayLicensesDialog() {
        displayLicences.postValue(true)
    }

    fun hideLicensesDialog() {
        displayLicences.postValue(false)
    }

    fun getSupportEmailModalModel(): EmailModalModel {
        val title = getString(application, R.string.send_mail_using)
        val subject = "${getString(application, R.string.feedback_email_subject)} v${version.value}"

        return EmailModalModel(feedbackRecipientAddress, title, subject, "")
    }

    fun getBugReportEmailModalModel(): EmailModalModel {
        val title = getString(application, R.string.send_mail_using)
        val subject = "${getString(application, R.string.report_bug_email_subject)} v${version.value}"

        return EmailModalModel(feedbackRecipientAddress, title, subject, "")
    }

    companion object {
        const val pivacyUrl = "https://privacy.etino.dev"
        const val feedbackRecipientAddress = "support@fesbcompanion.xyz"
    }

}
