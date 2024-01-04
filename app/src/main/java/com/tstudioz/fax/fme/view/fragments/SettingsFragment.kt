package com.tstudioz.fax.fme.view.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.view.activities.LoginActivity
import com.tstudioz.fax.fme.database.Korisnik
import io.realm.Realm


class SettingsFragment : PreferenceFragmentCompat() {
    private var rlmLog: Realm? = null
    private val alertDialog: AlertDialog? = null
    private var btmDialog: BottomSheetDialog? = null
    private var korisnik: String? = null
    private var mySPrefs: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    /*private val modeChangeListener = object : Preference.OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            rlmLog = Realm.getDefaultInstance()
            newValue as? Boolean

            Log.i("newValue", newValue.toString())
            updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
            when (newValue) {
                true -> {
                    updateTheme(R.style.AppTheme)
                }
                false -> {
                    //updateTheme(R.style.AppTheme_Custom)
                }
                else -> {
                    if (BuildCompat.isAtLeastQ()) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    }
                }
            }
            return true
        }
    }
    private fun updateTheme(nightMode: Int): Boolean {
        editor = mySPrefs?.edit()
        editor?.putString("Theme_mode", nightMode.toString())
        editor?.apply()
        if(editor !=null)
            editor?.commit()
        requireActivity().setTheme(nightMode)
        requireActivity().recreate()
        return true
    }*/
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_prefrences)
        mySPrefs = requireActivity().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE)

        //val preference = findPreference<Preference>("amoled_theme")
        //preference?.onPreferenceChangeListener = modeChangeListener

        val prefLogOut = findPreference("logout") as Preference?
        rlmLog = Realm.getDefaultInstance()
        try {
            korisnik = rlmLog?.where(Korisnik::class.java)?.findFirst()?.username
        } catch (e: Exception) {
            e.message?.let { Log.e("settings exp", it )}
        } finally {
            rlmLog?.close()
        }
        prefLogOut?.summary = "Prijavljeni ste kao $korisnik"
        prefLogOut?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            userLogOut()
            deleteWebViewCookies()
            goToLoginScreen()
            true
        }
        val weatherUnits = findPreference<Preference>("units") as CheckBoxPreference?
        weatherUnits?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            editor = mySPrefs?.edit()
            if (weatherUnits != null && weatherUnits.isChecked) {
                editor?.putString("weather_units", "&units=ca")
                editor?.apply()
            } else {
                editor?.putString("weather_units", "&units=us")
                editor?.apply()
            }
            if(editor !=null)
                editor?.commit()
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
        val prefBeta = findPreference("betta_particp") as Preference?
        prefBeta?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendFeedMail(
                "[BETA] Prijava beta testera za FESB Companion",
                "Slanjem ovog maila prihvaćam sudjelovanje u internom beta testiranju s ovom email adresom. Upozorenje: beta verzije znaju biti nestabilne i nepouzdane."
            )
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

    private fun userLogOut() {
        editor = mySPrefs?.edit()
        editor?.putBoolean("logged_in", false)
        editor?.apply()
        rlmLog = Realm.getDefaultInstance()
        try {
            rlmLog?.executeTransaction(Realm.Transaction { rlmLog?.deleteAll() })
        } finally {
            rlmLog?.close()
        }
    }

    private fun deleteWebViewCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        } else {
            val cookieSyncMngr = CookieSyncManager.createInstance(activity)
            cookieSyncMngr.startSync()
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncMngr.stopSync()
            cookieSyncMngr.sync()
        }
    }

    private fun goToLoginScreen() {
        val nazadNaLogin = Intent(activity, LoginActivity::class.java)
        startActivity(nazadNaLogin)
    }

    private val buildVersion: String
        get() {
            var ver = "undefined"
            try {
                val pInfo = activity?.packageName?.let {
                    activity?.packageManager?.getPackageInfo(
                        it, 0
                    )
                }
                if (pInfo != null) {
                    ver = pInfo.versionName
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ver
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
        val view = LayoutInflater.from(activity).inflate(R.layout.licence_view, null) as NestedScrollView
        val wv = view.findViewById<View>(R.id.webvju) as WebView
        wv.loadUrl("file:///android_asset/mvp.html")
        btmDialog = activity?.let { BottomSheetDialog(it)}
        btmDialog?.setCancelable(true)
        btmDialog?.setContentView(view)
        btmDialog?.setCanceledOnTouchOutside(true)
        btmDialog?.show()
    }

    private fun sendFeedMail(title: String, body: String?) {
        val version = buildVersion
        activity?.let { ShareCompat.IntentBuilder.from(it)}
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