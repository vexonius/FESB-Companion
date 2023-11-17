package com.tstudioz.fax.fme.fragments

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
import com.tstudioz.fax.fme.activities.LoginActivity
import com.tstudioz.fax.fme.database.Korisnik
import io.realm.Realm

class SettingsFragment : PreferenceFragmentCompat() {
    private var rlmLog: Realm? = null
    private val alertDialog: AlertDialog? = null
    private var btmDialog: BottomSheetDialog? = null
    private var korisnik: String? = null
    private var mySPrefs: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    /* public class MySettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.app_prefrences, rootKey);

            CheckBoxPreference amoledPreference = findPreference("amoled_theme");

            if (amoledPreference != null) {
                boolean isAmoledThemeEnabled = amoledPreference.isChecked();
                if (isAmoledThemeEnabled) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String darkModeString = getString(R.string.dark_mode);
        if (key != null && key.equals(darkModeString)) {
            if (sharedPreferences != null) {
                String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
                String selectedMode = sharedPreferences.getString(darkModeString, darkModeValues[0]);

                switch (selectedMode) {
                    case darkModeValues[1]:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case darkModeValues[2]:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                }
            }
        }
    }*/
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.app_prefrences)
        mySPrefs = requireActivity().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE)
        val prefLogOut = findPreference("logout") as Preference?
        rlmLog = Realm.getDefaultInstance()
        try {
            korisnik = rlmLog?.where(Korisnik::class.java)?.findFirst()!!.getUsername()
        } catch (e: Exception) {
            Log.e("settings exp", e.message!!)
        } finally {
            rlmLog?.close()
        }
        prefLogOut!!.summary = "Prijavljeni ste kao $korisnik"
        prefLogOut.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            userLogOut()
            deleteWebViewCookies()
            goToLoginScreen()
            true
        }
        val weather_units = findPreference<Preference>("units") as CheckBoxPreference?
        weather_units!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            editor = mySPrefs?.edit()
            if (weather_units.isChecked) {
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
        prefFeedback!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendFeedMail("[FEEDBACK] FESB Companion", "")
            true
        }
        val prefBugreport = findPreference("bug_report") as Preference?
        prefBugreport!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendFeedMail("[BUG REPORT] FESB Companion", "")
            true
        }
        val prefBeta = findPreference("betta_particp") as Preference?
        prefBeta!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            sendFeedMail(
                "[BETA] Prijava beta testera za FESB Companion",
                "Slanjem ovog maila prihvaćam sudjelovanje u internom beta testiranju s ovom email adresom. Upozorenje: beta verzije znaju biti nestabilne i nepouzdane."
            )
            true
        }
        val prefMvp = findPreference("mvp") as Preference?
        prefMvp!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mvpDialog()
            true
        }
        val prefInfo = findPreference("version") as Preference?
        prefInfo!!.summary = buildVersion
        prefInfo.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            i++
            if (i > 6) Toast.makeText(activity, ":)", Toast.LENGTH_SHORT).show()
            true
        }
        val prefLicence = findPreference("legal") as Preference?
        prefLicence!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            displayLicensesAlertDialog()
            true
        }
        val prefDev = findPreference("developer") as Preference?
        prefDev!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
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
        prefPriv!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
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

    fun userLogOut() {
        editor = mySPrefs!!.edit()
        editor?.putBoolean("loged_in", false)
        editor?.apply()
        rlmLog = Realm.getDefaultInstance()
        try {
            rlmLog?.executeTransaction(Realm.Transaction { rlmLog?.deleteAll() })
        } finally {
            rlmLog?.close()
        }
    }

    fun deleteWebViewCookies() {
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

    fun goToLoginScreen() {
        val nazadNaLogin = Intent(activity, LoginActivity::class.java)
        startActivity(nazadNaLogin)
    }

    val buildVersion: String
        get() {
            var ver = "undefined"
            try {
                val pInfo = activity!!.packageManager.getPackageInfo(
                    activity!!.packageName, 0
                )
                ver = pInfo.versionName
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
        btmDialog = BottomSheetDialog(activity!!)
        btmDialog!!.setCancelable(true)
        btmDialog!!.setContentView(view)
        btmDialog!!.setCanceledOnTouchOutside(true)
        btmDialog!!.show()
    }

    private fun mvpDialog() {
        val view =
            LayoutInflater.from(activity).inflate(R.layout.licence_view, null) as NestedScrollView
        val wv = view.findViewById<View>(R.id.webvju) as WebView
        wv.loadUrl("file:///android_asset/mvp.html")
        btmDialog = BottomSheetDialog(activity!!)
        btmDialog!!.setCancelable(true)
        btmDialog!!.setContentView(view)
        btmDialog!!.setCanceledOnTouchOutside(true)
        btmDialog!!.show()
    }

    fun sendFeedMail(title: String, body: String?) {
        val version = buildVersion
        ShareCompat.IntentBuilder.from(activity!!)
            .setType("message/rfc822")
            .addEmailTo("info@tstud.io")
            .setSubject("$title v$version")
            .setText(body)
            .setChooserTitle("Pošalji email pomoću...")
            .startChooser()
    }

    companion object {
        private var i = 0
    }
}