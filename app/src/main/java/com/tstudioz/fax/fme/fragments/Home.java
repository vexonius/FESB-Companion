package com.tstudioz.fax.fme.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.tstudioz.fax.fme.Application.FESBCompanion;
import com.tstudioz.fax.fme.BuildConfig;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.IndexActivity;
import com.tstudioz.fax.fme.activities.MenzaActivity;
import com.tstudioz.fax.fme.adapters.EmployeeRVAdapter;
import com.tstudioz.fax.fme.adapters.LeanTaskAdapter;
import com.tstudioz.fax.fme.database.LeanTask;
import com.tstudioz.fax.fme.database.Predavanja;
import com.tstudioz.fax.fme.databinding.HomeTabBinding;
import com.tstudioz.fax.fme.weather.Current;
import com.tstudioz.fax.fme.weather.Forecast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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


    private static final String myApiKey = BuildConfig.DARKSKY_API_KEY;
    private static final double mLatitude = 43.511287;
    private static final double mLongitude = 16.469252;

    private String date = null;
    private String units;

    private Forecast mForecast;
    private Snackbar snack;
    private Realm mrealm, taskRealm;
    private HomeTabBinding binding;

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

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        binding = HomeTabBinding.inflate(inflater, container, false);

        getActivity().setActionBar(binding.customToolbar);

        setHasOptionsMenu(true);
        setCyanStatusBarColor();
        getDate();

         try {
             start();
         } catch (IOException | JSONException e) {
             e.printStackTrace();
         }

        taskRealm = Realm.getInstance(realmTaskConfiguration);
        loadNotes();

        loadAdsOnHome();
        loadIksicaAd();
        loadMenzaView();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        setCyanStatusBarColor();
        showList();
    }

    private void getDate(){
        DateFormat df = new SimpleDateFormat("d.M.yyyy.");
        date = df.format(Calendar.getInstance().getTime());
    }

    private void getForecast(String url) {
        // OkHttp stuff
        OkHttpClient client = FESBCompanion.getInstance().getOkHttpInstance();
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
        Log.d("Dark", forecastUrl);
        final String testUrl = "http://34.65.18.132/forecast";

        if (isNetworkAvailable()) {
            getForecast(testUrl);
        } else {
            showSnacOffline();
        }

    }


    private void updateDisplay() {
        Current current = mForecast.getCurrent();

        String pTemperatura = current.getTemperature() + "°";
        String pHumidity = current.getHumidity() + "";
        String pWind = current.getWind() + " km/h";
        String pPrecip = current.getPrecipChance() + "%";
        String pSummary = current.getSummary();

        binding.temperaturaVrijednost.setText(pTemperatura);
        binding.vlaznostVrijednost.setText(pHumidity);
        binding.oborineVrijednost.setText(pPrecip);
        binding.trenutniVjetar.setText(pWind);
        binding.opis.setText(pSummary);

        binding.shimmerWeather.setVisibility(View.GONE);
        binding.cardHome.setVisibility(View.VISIBLE);

        Drawable drawable = getResources().getDrawable(current.getIconId());
        binding.vrijemeImage.setImageDrawable(drawable);

    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        jsonData = jsonData.replaceAll("\\\\\"", "\"");
        jsonData = jsonData.substring(1, jsonData.length()-1);
        Log.d("REGEX OUTPUT", jsonData);
        Forecast forecast = new Forecast();
        forecast.setCurrent(getCurrentDetails(jsonData));

        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        JSONObject currently = forecast.getJSONObject("currently");

        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipChance(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setWind(currently.getDouble("windSpeed"));
        current.setTemperature(currently.getDouble("temperature"));

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
            binding.rv.setVisibility(View.INVISIBLE);
            binding.nemaPredavanja.setVisibility(View.VISIBLE);
        } else {
            binding.nemaPredavanja.setVisibility(View.INVISIBLE);
            EmployeeRVAdapter adapter = new EmployeeRVAdapter(rezultati);
            binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            ViewCompat.setNestedScrollingEnabled(binding.rv, false);
            binding.rv.setAdapter(adapter);

            binding.nemaPredavanja.setVisibility(View.GONE);
            binding.rv.setVisibility(View.VISIBLE);
        }

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
        binding.recyclerTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        ViewCompat.setNestedScrollingEnabled(binding.recyclerTask, false);
        binding.recyclerTask.setAdapter(leanTaskAdapter);

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
        binding.adViewHome.setVisibility(View.GONE);
      //   }
    }

    public void loadIksicaAd() {
        binding.iksicaAd.setOnClickListener(new View.OnClickListener() {
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
        binding.menzaRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenzaActivity.class));
            }
        });

        binding.eindexRelative.setOnClickListener(new View.OnClickListener() {
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
