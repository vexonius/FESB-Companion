package com.tstudioz.fax.fme.activities;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.adapters.EmployeeRVAdapter;
import com.tstudioz.fax.fme.database.Korisnik;
import com.tstudioz.fax.fme.database.Predavanja;
import com.tstudioz.fax.fme.fragments.Home;
import com.tstudioz.fax.fme.fragments.Kolegiji;
import com.tstudioz.fax.fme.fragments.Left;
import com.tstudioz.fax.fme.fragments.Prisutnost;
import com.tstudioz.fax.fme.fragments.Right;
import com.tstudioz.fax.fme.migrations.CredMigration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    public String date = null;
    public AlertDialog alertDialog;
    public long back_pressed;

    private AHBottomNavigation bottomNavigation;

    private Realm mainealm;
    private Realm realmLog;
    private Realm rlmLog;

    private OkHttpClient client;
    private Home hf;
    private Snackbar snack;

    public final RealmConfiguration mainRealmConfig = new RealmConfiguration.Builder()
            .name("glavni.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .build();


    public final RealmConfiguration CredRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(6)
            .migration(new CredMigration())
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolbar();
        setContentView(R.layout.activity_main);

        Realm.setDefaultConfiguration(mainRealmConfig);

        DateFormat df = new SimpleDateFormat("d.M.yyyy.");
        date = df.format(Calendar.getInstance().getTime());

        setUpBottomNav();
        setDefaultScreen();
        setFragmentTab();
        checkUser();

    }

    public void checkUser() {
        realmLog = Realm.getInstance(CredRealmCf);
        if (realmLog != null) {
            getMojRaspored();
        } else {
            SharedPreferences sharedPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("loged_in", false);
            editor.commit();

            Toast.makeText(this, "Potrebna je prijava!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }
    }

    public void setUpToolbar() {
        getSupportActionBar().setElevation(0.0f);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void setUpBottomNav() {
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item0 = new AHBottomNavigationItem(getString(R.string.homie), R.drawable.home, R.color.home_color);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.left), R.drawable.schedule, R.color.left_color);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.prisutnost), R.drawable.plus_attend, R.color.left_color);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.kolegiji), R.drawable.courses, R.color.left_color);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.right), R.drawable.mail, R.color.right_color);

        bottomNavigation.addItem(item0);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item2);

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        bottomNavigation.setForceTint(true);
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.white));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.inactive));
        bottomNavigation.setUseElevation(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setCurrentItem(0);
    }

    public void setDefaultScreen() {
        hf = new Home();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, hf);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void setFragmentTab() {
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                switch (position) {
                    case 0:
                        Home hf = new Home();
                        ft.replace(R.id.frame, hf);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setTitle("FESB Companion");
                        break;

                    case 1:
                        Left lf = new Left();
                        ft.replace(R.id.frame, lf);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setTitle("Raspored");
                        break;


                    case 2:
                        Prisutnost ik = new Prisutnost();
                        ft.replace(R.id.frame, ik);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setTitle("Prisutnost");
                        break;

                    case 3:

                        Kolegiji kol = new Kolegiji();
                        ft.replace(R.id.frame, kol);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setTitle("Kolegiji");
                        break;

                    case 4:

                        Right rt = new Right();
                        ft.replace(R.id.frame, rt);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setTitle("Mail");
                        break;
                    default:
                        Home hf0 = new Home();
                        ft.replace(R.id.frame, hf0);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setTitle("FESB Companion");
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.logout:
                userLogOut();
                deleteWebViewCookies();

                Intent nazadaNaLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(nazadaNaLogin);
                break;


            case R.id.refresMe:
                if (isNetworkAvailable()) {
                    getMojRaspored();
                } else {
                    showSnacOffline();
                }
                break;

            case R.id.legal:
                displayLicensesAlertDialog();
                break;

            case R.id.about:
                appInfo();
                break;

            case R.id.feedback:
                String version = getBuildVersion();

                ShareCompat.IntentBuilder.from(this)
                        .setType("message/rfc822")
                        .addEmailTo("info@tstud.io")
                        .setSubject("FESB Companion Feedback v"  + version)
                        .setText("")
                        .setChooserTitle("Pošalji email pomoću...")
                        .startChooser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteWebViewCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(getApplicationContext());
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    public void getMojRaspored() {

        Realm rlm = Realm.getInstance(CredRealmCf);
        Korisnik kor = rlm.where(Korisnik.class).findFirst();

        // Get calendar set to current date and time
        Calendar c = Calendar.getInstance();
        Calendar s = Calendar.getInstance();

        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        DateFormat dfday = new SimpleDateFormat("dd");
        DateFormat dfmonth = new SimpleDateFormat("MM");
        DateFormat dfyear = new SimpleDateFormat("yyyy");

        // Set the calendar to Saturday of the current week
        s.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        DateFormat sday = new SimpleDateFormat("dd");
        DateFormat smonth = new SimpleDateFormat("MM");
        DateFormat syear = new SimpleDateFormat("yyyy");

        client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId=" + kor.getUsername().toString() + "&MinDate=" + dfmonth.format(c.getTime()) + "%2F" + dfday.format(c.getTime()) + "%2F" + dfyear.format(c.getTime()) + "%2022%3A44%3A48&MaxDate=" + smonth.format(s.getTime()) + "%2F" + sday.format(s.getTime()) + "%2F" + syear.format(s.getTime()) + "%2022%3A44%3A48")
                .get()
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Exception caught", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Realm realm = Realm.getInstance(mainRealmConfig);

                try {
                    if (response.code() == 500) {
                        client.dispatcher().cancelAll();
                        invalidCreds();
                    }

                    Document doc = Jsoup.parse(response.body().string());

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<Predavanja> svaPredavanja = realm.where(Predavanja.class).findAll();
                            svaPredavanja.deleteAllFromRealm();
                        }
                    });

                    if (response.isSuccessful()) {
                        final Elements elements = doc.select("div.event");

                        try {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (final Element e : elements) {
                                        Predavanja predavanja = realm.createObject(Predavanja.class, UUID.randomUUID().toString());

                                        if (e.hasAttr("data-id")) {
                                            String attr = e.attr("data-id");
                                            predavanja.setObjectId(Integer.parseInt(attr));
                                        }

                                        predavanja.setPredavanjeIme(e.select("span.groupCategory").text());
                                        predavanja.setPredmetPredavanja((e.select("span.name.normal").text()));
                                        predavanja.setRasponVremena(e.select("div.timespan").text());
                                        predavanja.setGrupa(e.select("span.group.normal").text());
                                        predavanja.setGrupaShort(e.select("span.group.short").text());
                                        predavanja.setDvorana(e.select("span.resource").text());
                                        predavanja.setDetaljnoVrijeme(e.select("div.detailItem.datetime").text());
                                        predavanja.setProfesor(e.select("div.detailItem.user").text());
                                    }
                                }
                            });
                        } finally {
                            realm.close();
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // showList();
                            if (hf != null) {
                                hf.showList();
                            }
                        }
                    });

                } catch (IOException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });

    }

    public void showList() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        RelativeLayout np = (RelativeLayout) findViewById(R.id.nema_predavanja);

        mainealm = Realm.getInstance(mainRealmConfig);
        RealmResults<Predavanja> rezultati = mainealm.where(Predavanja.class).contains("detaljnoVrijeme", date).findAll();

        if (rezultati.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            np.setVisibility(View.VISIBLE);
        } else {
            EmployeeRVAdapter adapter = new EmployeeRVAdapter(rezultati);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

            np.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    public void userLogOut() {
        SharedPreferences mySPrefs = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
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

    public void invalidCreds() {
        SharedPreferences mySPrefs1 = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = mySPrefs1.edit();
        editor1.putBoolean("loged_in", false);
        editor1.apply();

        final Realm rlmLog1 = Realm.getInstance(CredRealmCf);
        rlmLog1.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                rlmLog1.deleteAll();
            }
        });

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void displayLicensesAlertDialog() {
        WebView view = (WebView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.licence_view, null);
        view.loadUrl("file:///android_asset/legal.html");
        alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void appInfo() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.info_layout);
        dialog.show();
    }

    public void showSnacOffline() {
        snack = Snackbar.make(findViewById(R.id.coordinatorLayout), "Niste povezani", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_nice));
        snack.show();
    }

    public void exitApp() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            snack = Snackbar.make(findViewById(R.id.coordinatorLayout), "Pritisnite nazad za izlazak iz aplikacije", Snackbar.LENGTH_SHORT);
            View viewto = snack.getView();
            viewto.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.grey_nice));
            snack.show();
        }
        back_pressed = System.currentTimeMillis();
    }

    public String getBuildVersion(){
        String ver = "undefined";
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            ver = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ver;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (realmLog != null)
            realmLog.close();

        if (rlmLog != null)
            rlmLog.close();

        if (mainealm != null)
            mainealm.close();
    }

}