package com.tstudioz.fax.fme.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.LoginActivity;
import com.tstudioz.fax.fme.migrations.CredMigration;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by etino7 on 12/01/2018.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    private Realm rlmLog;
    private AlertDialog alertDialog;
    private BottomSheetDialog btmDialog;

    public final RealmConfiguration CredRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(7)
            .migration(new CredMigration())
            .build();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_prefrences);

        Preference prefLogOut = (Preference) findPreference("logout");
        prefLogOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                userLogOut();
                deleteWebViewCookies();
                goToLoginSCreen();
                return true;
            }
        });

        Preference prefFeedback = (Preference) findPreference("feedback");
        prefFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
               sendFeedMail("[FEEDBACK] FESB Companion");
                return true;
            }
        });

        Preference prefBugreport = (Preference) findPreference("bug_report");
        prefBugreport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendFeedMail("[BUG REPORT] FESB Companion");
                return true;
            }
        });

        Preference prefMvp = (Preference) findPreference("mvp");
        prefMvp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mvpDialog();
                return true;
            }
        });

        Preference prefInfo = (Preference) findPreference("version");
        prefInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // TODO: smiley
                return true;
            }
        });

        Preference prefLicence = (Preference) findPreference("legal");
        prefLicence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                displayLicensesAlertDialog();
                return true;
            }
        });

        Preference prefDev = (Preference) findPreference("developer");
        prefDev.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)).build();
                customTabsIntent.launchUrl(getActivity(), Uri.parse("http://tstud.io/"));
                return true;
            }
        });

    }

    public void userLogOut() {
        SharedPreferences mySPrefs = getActivity().getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = mySPrefs.edit();
        editor.putBoolean("loged_in", false);
        editor.apply();

        rlmLog = Realm.getInstance(CredRealmCf);
        rlmLog.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                rlmLog.deleteAll();
            }
        });
    }

    public void deleteWebViewCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(getActivity());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public void goToLoginSCreen() {
        Intent nazadaNaLogin = new Intent(getActivity(), LoginActivity.class);
        startActivity(nazadaNaLogin);
    }

    public String getBuildVersion() {
        String ver = "undefined";
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ver = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ver;
    }


    private void displayLicensesAlertDialog() {
        NestedScrollView view = (NestedScrollView) LayoutInflater.from(getActivity()).inflate(R.layout.licence_view, null);
        WebView wv = (WebView) view.findViewById(R.id.webvju);
        wv.loadUrl("file:///android_asset/legal.html");
        btmDialog = new BottomSheetDialog(getActivity());
        btmDialog.setCancelable(true);
        btmDialog.setContentView(view);
        btmDialog.setCanceledOnTouchOutside(true);
        btmDialog.show();
    }

    private void mvpDialog() {
        NestedScrollView view = (NestedScrollView) LayoutInflater.from(getActivity()).inflate(R.layout.licence_view, null);
        WebView wv = (WebView) view.findViewById(R.id.webvju);
        wv.loadUrl("file:///android_asset/mvp.html");
        btmDialog = new BottomSheetDialog(getActivity());
        btmDialog.setCancelable(true);
        btmDialog.setContentView(view);
        btmDialog.setCanceledOnTouchOutside(true);
        btmDialog.show();

    }

    public void sendFeedMail(String title){
        String version = getBuildVersion();
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("message/rfc822")
                .addEmailTo("info@tstud.io")
                .setSubject(title + " v" + version)
                .setText("")
                .setChooserTitle("Pošalji email pomoću...")
                .startChooser();
    }
}
