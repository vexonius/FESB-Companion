package com.tstudioz.fax.fme.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.LoginActivity;
import com.tstudioz.fax.fme.database.Korisnik;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;


public class SettingsFragment extends PreferenceFragmentCompat {
    private Realm rlmLog;
    private AlertDialog alertDialog;
    private BottomSheetDialog btmDialog;
    private String korisnik;
    private static int i = 0;
    private SharedPreferences mySPrefs;
    private  SharedPreferences.Editor editor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.app_prefrences);

        mySPrefs = getActivity().getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);

        Preference prefLogOut = (Preference) findPreference("logout");
        rlmLog = Realm.getDefaultInstance();
        try {
            korisnik = rlmLog.where(Korisnik.class).findFirst().getUsername();
        } catch (Exception e) {
            Log.e("settings exp", e.getMessage());
        } finally {
            rlmLog.close();
        }
        prefLogOut.setSummary("Prijavljeni ste kao " + korisnik);
        prefLogOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                userLogOut();
                deleteWebViewCookies();
                goToLoginSCreen();
                return true;
            }
        });

        final CheckBoxPreference weather_units = (CheckBoxPreference) findPreference("units");
        weather_units.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                editor = mySPrefs.edit();
                if (weather_units.isChecked()) {
                    editor.putString("weather_units", "&units=ca");
                    editor.apply();
                } else {
                    editor.putString("weather_units", "&units=us");
                    editor.apply();
                }
                editor.commit();
                return true;
            }
        });


        Preference prefFeedback = (Preference) findPreference("feedback");
        prefFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendFeedMail("[FEEDBACK] FESB Companion", "");
                return true;
            }
        });

        Preference prefBugreport = (Preference) findPreference("bug_report");
        prefBugreport.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendFeedMail("[BUG REPORT] FESB Companion", "");
                return true;
            }
        });

        Preference prefBeta = (Preference)findPreference("betta_particp");
        prefBeta.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                sendFeedMail("[BETA] Prijava beta testera za FESB Companion", "Slanjem ovog maila prihvaćam sudjelovanje u internom beta testiranju s ovom email adresom. Upozorenje: beta verzije znaju biti nestabilne i nepouzdane.");
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
        prefInfo.setSummary(getBuildVersion());
        prefInfo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                i++;

                if (i > 6)
                    Toast.makeText(getActivity(), ":)", Toast.LENGTH_SHORT).show();

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

        Preference prefPriv = (Preference) findPreference("privacy");
        prefPriv.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)).build();
                    customTabsIntent.launchUrl(getActivity(), Uri.parse("http://tstud.io/privacy"));
                } catch (Exception e){
                    Toast.makeText(getContext(), "Ažurirajte Chrome preglednik", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }

    public void userLogOut() {
        editor = mySPrefs.edit();
        editor.putBoolean("loged_in", false);
        editor.apply();

        rlmLog = Realm.getDefaultInstance();
        try {
            rlmLog.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    rlmLog.deleteAll();
                }
            });
        } finally {
            rlmLog.close();
        }
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
        Intent nazadNaLogin = new Intent(getActivity(), LoginActivity.class);
        startActivity(nazadNaLogin);
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

    public void sendFeedMail(String title, String body) {
        String version = getBuildVersion();
        ShareCompat.IntentBuilder.from(getActivity())
                .setType("message/rfc822")
                .addEmailTo("info@tstud.io")
                .setSubject(title + " v" + version)
                .setText(body)
                .setChooserTitle("Pošalji email pomoću...")
                .startChooser();
    }
}
