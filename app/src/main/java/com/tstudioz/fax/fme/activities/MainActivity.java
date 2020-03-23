package com.tstudioz.fax.fme.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.tstudioz.fax.fme.Application.FESBCompanion;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Korisnik;
import com.tstudioz.fax.fme.database.Predavanja;
import com.tstudioz.fax.fme.databinding.ActivityMainBinding;
import com.tstudioz.fax.fme.fragments.Home;
import com.tstudioz.fax.fme.fragments.Kolegiji;
import com.tstudioz.fax.fme.fragments.Mail;
import com.tstudioz.fax.fme.fragments.Prisutnost;
import com.tstudioz.fax.fme.fragments.TimeTable;

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
import io.realm.exceptions.RealmException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    public String date = null;
    public long back_pressed;

    private Realm realmLog;

    private OkHttpClient client;
    private Home hf;
    private Snackbar snack;
    private BottomSheetDialog bottomSheet;
    private SharedPreferences shPref;
    private SharedPreferences.Editor editor;

    private ActivityMainBinding binding;
    private AHBottomNavigation bottomNavigation;

    public final RealmConfiguration mainRealmConfig = new RealmConfiguration.Builder()
            .name("glavni.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpToolbar();
        getDate();

        setUpBottomNav();

        isThereAction();

        setFragmentTab();
        checkUser();
        checkVersion();
        shouldShowGDPRDialog();
    }

    public void isThereAction() {
        if (getIntent().getAction() != null) {
            showShortcutView();
        } else {
            setDefaultScreen();
        }
    }

    public void checkUser() {
        realmLog = Realm.getDefaultInstance();
        if (realmLog != null) {
            Korisnik korisnik = null;
            try {
                korisnik = realmLog.where(Korisnik.class).findFirst();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                realmLog.close();
            }

            if (korisnik != null) {
                getMojRaspored();
            } else {
                invalidCreds();
            }


        } else {
            shPref = FESBCompanion.getInstance().getSP();
            editor = shPref.edit();
            editor.putBoolean("loged_in", false);
            editor.commit();

            Toast.makeText(this, "Potrebna je prijava!", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, LoginActivity.class));

        }
    }

    public void setUpToolbar() {
        getSupportActionBar().hide();
       // getSupportActionBar().setElevation(0.0f);
       // getSupportActionBar().setDisplayShowHomeEnabled(false);

    }

    public void setUpBottomNav() {
        bottomNavigation = binding.bottomNavigation;

        AHBottomNavigationItem item0 = new AHBottomNavigationItem(getString(R.string.homie), R.drawable.home, R.color.home_color);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.timetable), R.drawable.schedule, R.color.left_color);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.prisutnost), R.drawable.plus_attend, R.color.left_color);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.kolegiji), R.drawable.courses, R.color.left_color);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.mail), R.drawable.mail, R.color.right_color);

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
        binding.bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                beginFragTransaction(position);
                return true;
            }
        });
    }

    public void beginFragTransaction(int pos) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (pos) {
            case 0:
                Home hf = new Home();
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                ft.replace(R.id.frame, hf);
                ft.addToBackStack(null);
                ft.commit();

                break;

            case 1:
                TimeTable lf = new TimeTable();
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                ft.replace(R.id.frame, lf);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Raspored");
                break;


            case 2:
                Prisutnost ik = new Prisutnost();
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                ft.replace(R.id.frame, ik);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Prisutnost");
                break;

            case 3:

                Kolegiji kol = new Kolegiji();
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                ft.replace(R.id.frame, kol);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Kolegiji");
                break;

            case 4:

                Mail rt = new Mail();
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                ft.replace(R.id.frame, rt);
                ft.addToBackStack(null);
                ft.commit();
                getSupportActionBar().setTitle("Mail");
                break;

            default:
                Home hf0 = new Home();
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
                ft.replace(R.id.frame, hf0);
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
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
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.refresMe:
                if (isNetworkAvailable()) {
                    getMojRaspored();
                } else {
                    showSnacOffline();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    public void getMojRaspored() {

        realmLog = Realm.getDefaultInstance();
        Korisnik kor = realmLog.where(Korisnik.class).findFirst();


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

        client = FESBCompanion.getInstance().getOkHttpInstance();

        final Request request = new Request.Builder()
                .url("https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId" +
                        "=" + kor.getUsername().toString() + "&MinDate=" + dfmonth.format(c.getTime()) + "%2F" + dfday.format(c.getTime()) + "%2F" + dfyear.format(c.getTime()) + "%2022%3A44%3A48&MaxDate=" + smonth.format(s.getTime()) + "%2F" + sday.format(s.getTime()) + "%2F" + syear.format(s.getTime()) + "%2022%3A44%3A48")
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
                            RealmResults<Predavanja> svaPredavanja =
                                    realm.where(Predavanja.class).findAll();
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
                                        Predavanja predavanja =
                                                realm.createObject(Predavanja.class,
                                                        UUID.randomUUID().toString());

                                        if (e.hasAttr("data-id")) {
                                            String attr = e.attr("data-id");
                                            predavanja.setObjectId(Integer.parseInt(attr));
                                        }

                                        predavanja.setPredavanjeIme(e.select("span.groupCategory").text());
                                        predavanja.setPredmetPredavanja((e.select("span.name" +
                                                ".normal").text()));
                                        predavanja.setRasponVremena(e.select("div.timespan").text());
                                        predavanja.setGrupa(e.select("span.group.normal").text());
                                        predavanja.setGrupaShort(e.select("span.group.short").text());
                                        predavanja.setDvorana(e.select("span.resource").text());
                                        predavanja.setDetaljnoVrijeme(e.select("div.detailItem" +
                                                ".datetime").text());
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

        realmLog.close();
    }


    public void invalidCreds() {
        shPref = FESBCompanion.getInstance().getSP();
        editor = shPref.edit();
        editor.putBoolean("loged_in", false);
        editor.apply();

        realmLog = Realm.getDefaultInstance();
        try {
            realmLog.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmLog.deleteAll();
                }
            });
        } catch (RealmException ex) {
            Log.e("MainActivity", ex.toString());
        } finally {
            realmLog.close();
        }

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void getDate() {
        DateFormat df = new SimpleDateFormat("d.M.yyyy.");
        date = df.format(Calendar.getInstance().getTime());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }


    public void showSnacOffline() {
        snack = Snackbar.make(findViewById(R.id.coordinatorLayout), "Niste povezani",
                Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red_nice));
        snack.show();
    }

    public void exitApp() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish();
        } else {
            snack = Snackbar.make(findViewById(R.id.coordinatorLayout), "Pritisnite nazad za " +
                    "izlazak iz aplikacije", Snackbar.LENGTH_SHORT);
            View viewto = snack.getView();
            viewto.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.grey_nice));
            snack.show();
        }
        back_pressed = System.currentTimeMillis();
    }

    public void showShortcutView() {
        int shortPosition = 0;

        if (getIntent().getAction().equals("podsjetnik")) {
            Intent newIntent = new Intent(MainActivity.this, NoteActivity.class);
            newIntent.putExtra("mode", 2);
            newIntent.putExtra("task_key", "");
            startActivity(newIntent);
        } else {
            switch (getIntent().getAction()) {
                case "raspored":
                    shortPosition = 1;
                    break;
                case "prisutnost":
                    shortPosition = 2;
                    break;
            }
            beginFragTransaction(shortPosition);
        }

    }

    public void checkVersion() {
        shPref = FESBCompanion.getInstance().getSP();
        int staraVerzija = shPref.getInt("version_number", 14);
        int trenutnaVerzija = getVersionCode();

        if (staraVerzija < trenutnaVerzija) {
            showChangelog();

            editor = shPref.edit();
            editor.putInt("version_number", trenutnaVerzija);
            editor.commit();
        } else {
            return;
        }
    }

    public int getVersionCode() {
        int versionCode = 0;
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private void showChangelog() {
        NestedScrollView view =
                (NestedScrollView) LayoutInflater.from(this).inflate(R.layout.licence_view, null);
        WebView wv = (WebView) view.findViewById(R.id.webvju);
        wv.loadUrl("file:///android_asset/changelog.html");
        bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setCancelable(true);
        bottomSheet.setContentView(view);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.show();
    }

    private void shouldShowGDPRDialog() {
        shPref = FESBCompanion.getInstance().getSP();
        Boolean bool = shPref.getBoolean("GDPR_agreed", false);
        if (!bool) {
            showGDPRCompliance();

            editor = shPref.edit();
            editor.putBoolean("GDPR_agreed", true);
            editor.commit();
        }

    }

    private void showGDPRCompliance() {
        ConstraintLayout view =
                (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.gdpr_layout, null);

        TextView heading = (TextView) view.findViewById(R.id.terms_heading);
        TextView desc = (TextView) view.findViewById(R.id.terms_text);

        Typeface typeBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        heading.setTypeface(typeBold);

        Typeface typeRegular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        desc.setTypeface(typeRegular);

        TextView more = (TextView) view.findViewById(R.id.button_more);
        more.setTypeface(typeBold);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent =
                            builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark)).build();
                    customTabsIntent.launchUrl(view.getContext(), Uri.parse("http://tstud" +
                            ".io/privacy"));
                } catch (Exception ex) {
                    Toast.makeText(view.getContext(), "Ažurirajte Chrome preglednik za pregled " +
                            "web stranice", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView ok = (TextView) view.findViewById(R.id.button_ok);
        ok.setTypeface(typeBold);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.dismiss();
            }
        });

        bottomSheet = new BottomSheetDialog(this);
        bottomSheet.setCancelable(false);
        bottomSheet.setContentView(view);
        bottomSheet.setCanceledOnTouchOutside(false);
        bottomSheet.show();
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


}