package com.tstudioz.fax.fme.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.ads.NativeExpressAdView;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.activities.NoteActivity;
import com.tstudioz.fax.fme.adapters.EmployeeRVAdapter;
import com.tstudioz.fax.fme.database.Predavanja;
import com.tstudioz.fax.fme.weather.Current;
import com.tstudioz.fax.fme.weather.Forecast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import static android.content.ContentValues.TAG;


public class Home extends Fragment{

    public String date = null;

    String myApiKey = "e39d50a0b9c65d5c7f2739eff093e6f5";

    double mLatitude = 43.511287;
    double mLongitude = 16.469252;

    private Forecast mForecast;

    @InjectView(R.id.temperatura_vrijednost) TextView mTemperatureLabel;
    @InjectView(R.id.vlaznost_vrijednost) TextView mHumidityValue;
    @InjectView(R.id.opis) TextView mSummaryLabel;
    @InjectView(R.id.vrijeme_image) ImageView mIconImageView;
    @InjectView(R.id.oborine_vrijednost) TextView mPrecipValue;
    @InjectView(R.id.trenutni_vjetar) TextView mWindLabel;
    @InjectView(R.id.card_home) RelativeLayout mCardHome;
    @InjectView(R.id.progressCircle) ProgressBar mProgressCircle;
    @InjectView(R.id.task) RelativeLayout mtask;
    @InjectView(R.id.taskText) TextView mtasktext;
    @InjectView(R.id.list_progressbar) ProgressBar pbar1;
    @InjectView(R.id.rv) RecyclerView recyclerView;
    @InjectView(R.id.nema_predavanja) RelativeLayout np;
    @InjectView(R.id.relative_parent_home) RelativeLayout parentRelative;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Realm.init(getActivity().getApplicationContext());

        //Set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.home_tab,
                container, false);

        setHasOptionsMenu(true);

        ButterKnife.inject(this, view);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        DateFormat df = new SimpleDateFormat("d.M.yyyy.");
        date = df.format(Calendar.getInstance().getTime());

        NativeExpressAdView adView = (NativeExpressAdView)view.findViewById(R.id.adView);

    //    if(!isNetworkAvailable()){
            adView.setVisibility(View.GONE);
     /*   }else{

            adView.setVisibility(View.VISIBLE);
            AdRequest request = new AdRequest.Builder()
                    .addTestDevice("0F0806B7833336104F00247BA81C120D")
                    .build();
            adView.loadAd(request);
        }
                   */

          try {
                start();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }



        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Setting custom fonts
        TextView textLokacija = (TextView) view.findViewById(R.id.txtloc);
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");
        textLokacija.setTypeface(type);

        TextView textVjetar = (TextView)view.findViewById(R.id.trenutni_vjetar);
        Typeface typev = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Light.ttf");
        textVjetar.setTypeface(typev);

        TextView textvlaga = (TextView)view.findViewById(R.id.vlaznost_vrijednost);
        Typeface typevl = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Light.ttf");
        textvlaga.setTypeface(typevl);

        TextView textoborine = (TextView)view.findViewById(R.id.oborine_vrijednost);
        Typeface typeob = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Light.ttf");
        textoborine.setTypeface(typeob);

        TextView tasks = (TextView)view.findViewById(R.id.taskText);
        Typeface typeta = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Regular.ttf");
        tasks.setTypeface(typeta);

        TextView tempe = (TextView)view.findViewById(R.id.temperatura_vrijednost);
        Typeface typete = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Bold.ttf");
        tempe.setTypeface(typete);

        TextView danp = (TextView)view.findViewById(R.id.pr);
        Typeface typeap = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Regular.ttf");
        danp.setTypeface(typeap);

        TextView opisfont = (TextView)view.findViewById(R.id.opis);
        Typeface typeop = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Regular.ttf");
        opisfont.setTypeface(typeop);


        mtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getActivity(), NoteActivity.class);
                startActivity(newIntent);

            }
        });


        String tekstic = sharedPref.getString("mojtext", "Trenutno nema bilješki");
        if(!tekstic.isEmpty()){
            mtasktext.setText(tekstic);
        }else{
            mtasktext.setText("Trenutno nema bilješki");
        }
        mtasktext.setMovementMethod(new ScrollingMovementMethod());

        mtasktext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getActivity(), NoteActivity.class);
                startActivity(newIntent);
            }
        });


        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        showList();
        updateWeatherWhenOffline();
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
                alertUserAboutError();
                Log.e(TAG, "Exception caught", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
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


        // make call to server
        // get your own API KEY from developer.forecast.io and fill it in.
        final String forecastUrl = "https://api.forecast.io/forecast/" + myApiKey + "/" + mLatitude + "," + mLongitude;

        if(isNetworkAvailable()) {
            getForecast(forecastUrl);
        } else {
           showSnacOffline();
            updateWeatherWhenOffline();
        }

    }


    private void updateDisplay() {

        mProgressCircle.setVisibility(View.GONE);
        mCardHome.setVisibility(View.VISIBLE);

        Current current = mForecast.getCurrent();

        String pTemperatura = (((current.getTemperature() - 32) * 5) / 9) + "°";
        String pHumidity = current.getHumidity() + "";
        String pWind = (float) Math.round((current.getWind() * 1.609344)*10)/10 + " km/h";
        String pPrecip = current.getPrecipChance() + "%";
        String pSummary = current.getSummary();


        mTemperatureLabel.setText(pTemperatura);
        mHumidityValue.setText(pHumidity);
        mPrecipValue.setText(pPrecip);
        mWindLabel.setText(pWind);
        mSummaryLabel.setText(pSummary);

        Drawable drawable = getResources().getDrawable(current.getIconId());
        mIconImageView.setImageDrawable(drawable);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("zadnja_temp", pTemperatura);
        editor.putString("zadnja_vlaznost", pHumidity);
        editor.putString("zadnja_percip", pPrecip);
        editor.putString("zadnji_vjetar", pWind);
        editor.putString("zadnji_opis", pSummary);
        editor.putInt("imageId", current.getIconId());
        editor.commit();
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));

        return forecast;
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

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
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    public void updateWeatherWhenOffline(){
        mProgressCircle.setVisibility(View.GONE);
        mCardHome.setVisibility(View.VISIBLE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mTemperatureLabel.setText(sharedPref.getString("zadnja_temp", "-"));
        mHumidityValue.setText(sharedPref.getString("zadnja_vlaznost", "-"));
        mPrecipValue.setText(sharedPref.getString("zadnja_percip", "-"));
        mWindLabel.setText(sharedPref.getString("zadnji_vjetar", "-"));
        mSummaryLabel.setText(sharedPref.getString("zadnji_opis", "-"));
        mIconImageView.setImageResource((sharedPref.getInt("imageId", R.drawable.clouds)));


    }

    public void showList() {

        Realm.init(getActivity().getBaseContext());
        Realm mrealm = Realm.getDefaultInstance();

        mrealm.beginTransaction();
        RealmResults<Predavanja> rezultati = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", date).findAll();
        mrealm.commitTransaction();

        if (rezultati.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            np.setVisibility(View.VISIBLE);
        } else {

            EmployeeRVAdapter adapter = new EmployeeRVAdapter(rezultati);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            pbar1.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void showSnacOffline(){
        Snackbar snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Niste povezani", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

    public void alertUserAboutError(){
        Snackbar snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Došlo je do pogreške pri dohvaćanju prognoze", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

}
