package com.tstudioz.fax.fme.feature.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.common.user.UserRepository
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.login.services.UserService
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey
import io.realm.kotlin.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val dbManager: DatabaseManagerInterface,
    val userRepository: UserRepository
) : ViewModel() {

    var realm: Realm? = null

    fun getLoggedInUser(): String? {
        return userRepository.getCurrentUserName()
    }

    fun deleteRealmAndSharedPrefs() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedPreferences[SPKey.LOGGED_IN] = false
            realm = Realm.open(dbManager.getDefaultConfiguration())

            try {
                realm?.writeBlocking { this.deleteAll() }
            } finally {
                realm?.close()
            }
        }
    }

    fun deleteWebViewCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    fun goToLoginScreen(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    fun sendFeedbackEmail(context: Context, title: String, body: String?) {
        val version = getBuildVersion(context)
        ShareCompat.IntentBuilder.from(context as android.app.Activity)
            .setType("message/rfc822")
            .addEmailTo("support@fesbcompanion.xyz")
            .setSubject("$title v$version")
            .setText(body)
            .setChooserTitle("Pošalji email pomoću...")
            .startChooser()
    }

    fun getBuildVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "undefined"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "undefined"
        }
    }

    fun launchCustomTab(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.setToolbarColor(
            ContextCompat.getColor(context, R.color.colorPrimaryDark)
        ).build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun displayLicensesDialog(context: Context, bottomSheetDialog: BottomSheetDialog) {
        val view = LayoutInflater.from(context).inflate(R.layout.licence_view, null) as NestedScrollView
        val webView = view.findViewById<WebView>(R.id.webvju)
        webView.loadUrl("file:///android_asset/legal.html")

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.show()
    }
}
