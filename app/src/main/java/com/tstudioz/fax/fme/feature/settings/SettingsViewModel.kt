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
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.SPKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val application: Application,
    private val userRepository: UserRepositoryInterface,
    private val sharedPreferences: SharedPreferences
) : AndroidViewModel(application) {

    val username: MutableLiveData<String> = MutableLiveData()
    val version: MutableLiveData<String> = MutableLiveData()
    val displayLicences = MutableLiveData(false)
    val routeToLogin = MutableStateFlow(false)
    val eventsGlowing: MutableLiveData<Boolean> = MutableLiveData(
        sharedPreferences[SPKey.EVENTS_GLOW, true]
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            username.postValue(userRepository.getCurrentUserName())
            version.postValue(getBuildVersion())
        }
    }

    /**
     * Method below will remove all user data from database,
     * which will trigger session delegate flow event and
     * router will route to login screen
     */
    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteAllUserData()
            routeToLogin.emit(true)
        }
    }

    fun makeEventsGlow(value: Boolean) {
        sharedPreferences[SPKey.EVENTS_GLOW] = value
        eventsGlowing.postValue(value)
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
