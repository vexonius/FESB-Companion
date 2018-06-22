package com.tstudioz.fax.fme.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.IndexActivity;
import com.tstudioz.fax.fme.activities.MenzaActivity;
import com.tstudioz.fax.fme.activities.NoteActivity;
import com.tstudioz.fax.fme.adapters.EmployeeRVAdapter;
import com.tstudioz.fax.fme.adapters.LeanTaskAdapter;
import com.tstudioz.fax.fme.database.LeanTask;
import com.tstudioz.fax.fme.database.Predavanja;
import com.tstudioz.fax.fme.weather.Current;
import com.tstudioz.fax.fme.weather.Forecast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class Home extends Fragment {

    public String date = null;

    String myApiKey = "e39d50a0b9c65d5c7f2739eff093e6f5";
    String units;

    double mLatitude = 43.511287;
    double mLongitude = 16.469252;

    private Forecast mForecast;
    Snackbar snack;
    Realm mrealm, taskRealm;

    private AdView homeAdView;

    @BindView(R.id.temperatura_vrijednost) TextView mTemperatureLabel;
    @BindView(R.id.vlaznost_vrijednost) TextView mHumidityValue;
    @BindView(R.id.opis) TextView mSummaryLabel;
    @BindView(R.id.txtloc) TextView lokacija;
    @BindView(R.id.pr) TextView danp;
    @BindView(R.id.vrijeme_image) ImageView mIconImageView;
    @BindView(R.id.oborine_vrijednost) TextView mPrecipValue;
    @BindView(R.id.trenutni_vjetar) TextView mWindLabel;
    @BindView(R.id.card_home) RelativeLayout mCardHome;
    @BindView(R.id.progressCircle) ProgressBar mProgressCircle;
    @BindView(R.id.task) RelativeLayout mtask;
    @BindView(R.id.taskHeader) TextView mtaskTekst;
    @BindView(R.id.recyclerTask) RecyclerView mRecyclerTask;
    @BindView(R.id.rv) RecyclerView recyclerView;
    @BindView(R.id.nema_predavanja) RelativeLayout np;
    @BindView(R.id.relative_parent_home) RelativeLayout parentRelative;
    @BindView(R.id.menzaHeader) TextView mnzHeader;
    @BindView(R.id.iksica_ad) RelativeLayout iksicaPromoImage;
    @BindView(R.id.iksicaText) TextView mIksicaText;
    @BindView(R.id.iksica_text_description) TextView mIksicaDescription;
    @BindView(R.id.menza_text) TextView mMenzaText;
    @BindView(R.id.menza_text_description) TextView mMenzaDescription;
    @BindView(R.id.menza_relative) RelativeLayout mMenzaRelative;
    @BindView(R.id.eindexText) TextView eIndeksText;
    @BindView(R.id.eindex_text_description) TextView eIndeksDescription;
    @BindView(R.id.eindex_Relative) RelativeLayout eIndeksRelative;

    public RealmConfiguration realmTaskConfiguration = new RealmConfiguration.Builder()
            .name("tasks.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .build();

    public final RealmConfiguration mainRealmConfig = new RealmConfiguration.Builder()
            .name("glavni.realm")
            .schemaVersion(3)
            .deleteRealmIfMigrationNeeded()
            .build();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View view = inflater.inflate(R.layout.home_tab, container, false);

        setHasOptionsMenu(true);
        setCyanStatusBarColor();
        ButterKnife.bind(this, view);

        DateFormat df = new SimpleDateFormat("d.M.yyyy.");
        date = df.format(Calendar.getInstance().getTime());

         try {
             start();
         } catch (IOException | JSONException e) {
             e.printStackTrace();
         }

        setFancyFonts();

        taskRealm = Realm.getInstance(realmTaskConfiguration);
        loadNotes();

        homeAdView = view.findViewById(R.id.adViewHome);
        loadAdsOnHome();
        loadIksicaAd();
        loadMenzaView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        setCyanStatusBarColor();
        showList();
    }

    private void getForecast(String url) {
        // OkHttp stuff
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Exception caught", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String jsonData = response.body().string();
                    if (response.isSuccessful()) {
                        mForecast = parseForecastDetails(jsonData);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDisplay();
                            }
                        });
                    } else {
                        alertUserAboutError();
                    }
                } catch (IOException | JSONException e) {
                    Log.e(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void start() throws IOException, JSONException {

        SharedPreferences shared = getActivity().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE);
        units = shared.getString("weather_units", "&units=ca");

        // get your own API KEY from developer.forecast.io and fill it in.
        final String forecastUrl = "https://api.forecast.io/forecast/" + myApiKey + "/" + mLatitude + "," + mLongitude + "?lang=hr" + units;

        if (isNetworkAvailable()) {
            getForecast(forecastUrl);
        } else {
            showSnacOffline();
        }

    }


    private void updateDisplay() {
        mProgressCircle.setVisibility(View.GONE);
        mCardHome.setVisibility(View.VISIBLE);

        Current current = mForecast.getCurrent();

        String pTemperatura = current.getTemperature() + "°";
        String pHumidity = current.getHumidity() + "";
        String pWind = current.getWind() + " km/h";
        String pPrecip = current.getPrecipChance() + "%";
        String pSummary = current.getSummary();

        mTemperatureLabel.setText(pTemperatura);
        mHumidityValue.setText(pHumidity);
        mPrecipValue.setText(pPrecip);
        mWindLabel.setText(pWind);
        mSummaryLabel.setText(pSummary);

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);

    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));

        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        // String timezone = forecast.getString("timezone");

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        //current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setWind(currently.getDouble("windSpeed"));
        current.setTemperature(currently.getDouble("temperature"));
        //current.setTimeZone(timezone);


        return current;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    public void showList() {
        mrealm = Realm.getInstance(mainRealmConfig);
        RealmResults<Predavanja> rezultati = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", date).findAll();

        if (rezultati.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            np.setVisibility(View.VISIBLE);
        } else {
            np.setVisibility(View.INVISIBLE);
            EmployeeRVAdapter adapter = new EmployeeRVAdapter(rezultati);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            ViewCompat.setNestedScrollingEnabled(recyclerView, false);
            recyclerView.setAdapter(adapter);

            np.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    public void setFancyFonts() {
        Typeface typeLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");
        lokacija.setTypeface(typeLight);
        mWindLabel.setTypeface(typeLight);
        mHumidityValue.setTypeface(typeLight);
        mPrecipValue.setTypeface(typeLight);
        mMenzaText.setTypeface(typeLight);
        mMenzaDescription.setTypeface(typeLight);
        mIksicaText.setTypeface(typeLight);
        mIksicaDescription.setTypeface(typeLight);
        eIndeksText.setTypeface(typeLight);
        eIndeksDescription.setTypeface(typeLight);

        Typeface typeRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        danp.setTypeface(typeRegular);
        mtaskTekst.setTypeface(typeRegular);
        mnzHeader.setTypeface(typeRegular);
        mSummaryLabel.setTypeface(typeRegular);

        Typeface typeBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Bold.ttf");
        mTemperatureLabel.setTypeface(typeBold);
    }

    public void setCyanStatusBarColor() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.dark_cyan)));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.darker_cyan));
        }
    }

    public void loadNotes() {
        RealmResults<LeanTask> tasks = taskRealm.where(LeanTask.class).findAll();

        final LeanTask dodajNovi = new LeanTask();
        dodajNovi.setId("ACTION_ADD");
        dodajNovi.setTaskTekst("Dodaj novi podsjetnik");

        taskRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(dodajNovi);
            }
        });

        LeanTaskAdapter leanTaskAdapter = new LeanTaskAdapter(tasks);
        mRecyclerTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        ViewCompat.setNestedScrollingEnabled(mRecyclerTask, false);
        mRecyclerTask.setAdapter(leanTaskAdapter);

    }


    public void loadAdsOnHome() {

      //   if(isNetworkAvailable()) {
      //       homeAdView.setVisibility(View.VISIBLE);
      //       AdRequest adRequest = new AdRequest.Builder().build();
      //       homeAdView.setAdListener(new AdListener(){
      //           @Override
      //           public void onAdFailedToLoad(int errorCode){
      //               homeAdView.setVisibility(View.GONE);
      //           }
      //       });
      //       homeAdView.loadAd(adRequest);
      //   } else {
        homeAdView.setVisibility(View.GONE);
      //   }
    }

    public void loadIksicaAd() {
        iksicaPromoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = "com.tstud.iksica";
                try {
                    Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(appPackageName);
                    startActivity(intent);
                } catch (Exception anfe) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException ex) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            }
        });
    }

    public void loadMenzaView(){
        mMenzaRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenzaActivity.class));
            }
        });

        eIndeksRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IndexActivity.class));
            }
        });
    }

    public void showSnacOffline() {
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Niste povezani", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

    public void alertUserAboutError() {
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Došlo je do pogreške pri dohvaćanju prognoze", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }

        if (mrealm != null) {
            mrealm.close();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (taskRealm != null) {
            taskRealm.close();
        }
    }

}
