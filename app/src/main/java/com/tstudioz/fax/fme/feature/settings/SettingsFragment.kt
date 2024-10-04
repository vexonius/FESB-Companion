package com.tstudioz.fax.fme.feature.settings

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import com.tstudioz.fax.fme.util.PreferenceHelper.set
import com.tstudioz.fax.fme.util.SPKey
import io.realm.kotlin.Realm
import org.koin.android.ext.android.inject


class SettingsFragment : PreferenceFragmentCompat() {
    private var realm: Realm? = null
    private var btmDialog: BottomSheetDialog? = null
    private var korisnik: String? = null
    private val dbManager: DatabaseManagerInterface by inject()
    private val sharedPreferences: SharedPreferences by inject()


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_prefrences)

        val prefLogOut = findPreference("logout") as Preference?
        try {
            korisnik = sharedPreferences.getString("username", "")
        } catch (e: Exception) {
            e.message?.let { Log.e("settings exp", it) }
        }
        prefLogOut?.summary = "Prijavljeni ste kao $korisnik"
        prefLogOut?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            deleteRealmAndShpref()
            deleteWebViewCookies()
            goToLoginScreen()
            true
        }
        val prefFeedback = findPreference("feedback") as Preference?
        prefFeedback?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendFeedMail("[FEEDBACK] FESB Companion", "")
            true
        }
        val prefBugreport = findPreference("bug_report") as Preference?
        prefBugreport?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendFeedMail("[BUG REPORT] FESB Companion", "")
            true
        }
        val prefMvp = findPreference("mvp") as Preference?
        prefMvp?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mvpDialog()
            true
        }
        val prefInfo = findPreference("version") as Preference?
        prefInfo?.summary = buildVersion
        prefInfo?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            i++
            if (i > 6) Toast.makeText(activity, ":)", Toast.LENGTH_SHORT).show()
            true
        }
        val prefLicence = findPreference("legal") as Preference?
        prefLicence?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            displayLicensesAlertDialog()
            true
        }
        val prefDev = findPreference("developer") as Preference?
        prefDev?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.setToolbarColor(
                ContextCompat.getColor(
                    requireActivity(), R.color.colorPrimaryDark
                )
            ).build()
            customTabsIntent.launchUrl(requireActivity(), Uri.parse("http://tstud.io/"))
            true
        }
        val prefPriv = findPreference("privacy") as Preference?
        prefPriv?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            try {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.setToolbarColor(
                    ContextCompat.getColor(
                        requireActivity(), R.color.colorPrimaryDark
                    )
                ).build()
                customTabsIntent.launchUrl(requireActivity(), Uri.parse("http://tstud.io/privacy"))
            } catch (e: Exception) {
                Toast.makeText(context, "Ažurirajte Chrome preglednik", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    private fun deleteRealmAndShpref() {
        sharedPreferences[SPKey.LOGGED_IN] = false
        realm = Realm.open(dbManager.getDefaultConfiguration())

        try {
            realm?.writeBlocking { this.deleteAll() }
        } finally {
            realm?.close()
        }
    }

    private fun deleteWebViewCookies() {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
    }

    private fun goToLoginScreen() {
        val nazadNaLogin = Intent(activity, LoginActivity::class.java)
        startActivity(nazadNaLogin)
        activity?.finish()
    }

    private val buildVersion: String
        get() {
            var version = "undefined"
            try {
                val packageInfo = activity?.packageName?.let {
                    activity?.packageManager?.getPackageInfo(
                        it, 0
                    )
                }
                if (packageInfo != null) {
                    version = packageInfo.versionName
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return version
        }

    private fun displayLicensesAlertDialog() {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.licence_view, null) as NestedScrollView
        val wv = view.findViewById<View>(R.id.webvju) as WebView
        wv.loadUrl("file:///android_asset/legal.html")
        btmDialog = activity?.let { BottomSheetDialog(it) }
        btmDialog?.setCancelable(true)
        btmDialog?.setContentView(view)
        btmDialog?.setCanceledOnTouchOutside(true)
        btmDialog?.show()
    }

    private fun mvpDialog() {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.licence_view, null) as NestedScrollView
        val wv = view.findViewById<View>(R.id.webvju) as WebView
        wv.loadUrl("file:///android_asset/mvp.html")
        btmDialog = activity?.let { BottomSheetDialog(it) }
        btmDialog?.setCancelable(true)
        btmDialog?.setContentView(view)
        btmDialog?.setCanceledOnTouchOutside(true)
        btmDialog?.show()
    }

    private fun sendFeedMail(title: String, body: String?) {
        val version = buildVersion
        activity?.let { ShareCompat.IntentBuilder.from(it) }
            ?.setType("message/rfc822")
            ?.addEmailTo("info@tstud.io")
            ?.setSubject("$title v$version")
            ?.setText(body)
            ?.setChooserTitle("Pošalji email pomoću...")
            ?.startChooser()
    }

    companion object {
        private var i = 0
    }
}