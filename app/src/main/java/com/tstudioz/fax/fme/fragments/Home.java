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


import com.appnext.appnextsdk.API.AppnextAPI;
import com.appnext.appnextsdk.API.AppnextAd;
import com.appnext.appnextsdk.API.AppnextAdRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
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
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import static android.content.ContentValues.TAG;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class Home extends Fragment{

    public String date = null;

    String myApiKey = "e39d50a0b9c65d5c7f2739eff093e6f5";

    double mLatitude = 43.511287;
    double mLongitude = 16.469252;

    private Forecast mForecast;
    Snackbar snack;
    Realm mrealm;

    public AppnextAPI bannerAppnextAPI;
    public AppnextAd banner_ad;



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
    @BindView(R.id.taskText) TextView mtasktext;
    @BindView(R.id.list_progressbar) ProgressBar pbar1;
    @BindView(R.id.rv) RecyclerView recyclerView;
    @BindView(R.id.nema_predavanja) RelativeLayout np;
    @BindView(R.id.relative_parent_home) RelativeLayout parentRelative;
    @BindView(R.id.banner_view_home) RelativeLayout bannerView;
    @BindView(R.id.banner_title_home) TextView bannerTitle;
    @BindView(R.id.banner_rating_home) TextView bannerRating;
    @BindView(R.id.banner_install_home) TextView bannerInstall;
    @BindView(R.id.banner_privacy_home) ImageView bannerPrivacy;
    @BindView(R.id.banner_icon_home) ImageView bannerIcon;
    @BindView(R.id.banner_click_home) View bannerClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View view = inflater.inflate(R.layout.home_tab, container, false);

        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);

        DateFormat df = new SimpleDateFormat("d.M.yyyy.");
        date = df.format(Calendar.getInstance().getTime());

        try {
            start();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        setFancyFonts();
        loadNotes();
        loadNativeAd();

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
        mrealm = Realm.getDefaultInstance();
        RealmResults<Predavanja> rezultati = mrealm.where(Predavanja.class).contains("detaljnoVrijeme", date).findAll();

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

    public void setFancyFonts(){
        Typeface typeLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");
        lokacija.setTypeface(typeLight);
        mWindLabel.setTypeface(typeLight);
        mHumidityValue.setTypeface(typeLight);
        mPrecipValue.setTypeface(typeLight);

        Typeface typeRegular = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Regular.ttf");
        mtasktext.setTypeface(typeRegular);
        danp.setTypeface(typeRegular);
        mSummaryLabel.setTypeface(typeRegular);

        Typeface typeBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/OpenSans-Bold.ttf");
        mTemperatureLabel.setTypeface(typeBold);
    }

    public void loadNotes(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String tekstic = sharedPref.getString("mojtext", "Trenutno nema bilješki");

        if(!tekstic.isEmpty()){
            mtasktext.setText(tekstic);
        } else {
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

        mtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(getActivity(), NoteActivity.class);
                startActivity(newIntent);
            }
        });
    }

    public void showSnacOffline(){
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Niste povezani", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

    public void alertUserAboutError(){
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Došlo je do pogreške pri dohvaćanju prognoze", Snackbar.LENGTH_LONG);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.show();
    }

    public void loadNativeAd(){
        // Make sure to use your own Placement ID as created in www.appnext.com
        bannerAppnextAPI = new AppnextAPI(getActivity(), "344bf528-b041-44cc-aa7b-a58ec4157d73");
        bannerAppnextAPI.setAdListener(new AppnextAPI.AppnextAdListener() {
            @Override
            public void onAdsLoaded(ArrayList<AppnextAd> arrayList) {
                banner_ad = arrayList.get(0);

                Glide.with(getActivity()).load(banner_ad.getImageURL()).transition(withCrossFade()).into(bannerIcon);
                bannerTitle.setText(banner_ad.getAdTitle());
                bannerRating.setText(banner_ad.getStoreRating());
                bannerInstall.setText(banner_ad.getButtonText());
                bannerView.setVisibility(View.VISIBLE);
                bannerClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerAppnextAPI.adClicked(banner_ad);
                    }
                });
                bannerPrivacy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bannerAppnextAPI.privacyClicked(banner_ad);
                    }
                });

                bannerAppnextAPI.adImpression(banner_ad);
            }

            @Override
            public void onError(String s) {
                bannerView.setVisibility(View.GONE);
                Log.d("Ad loading error", s);
            }
        });
        // In this example we're loading only one ad for the banner using the setCount(1) function in the ad request
        // This is an optional usage. To load more ads either don't use the fucntion or call it with a different value: setCount(x)
        bannerAppnextAPI.loadAds(new AppnextAdRequest());

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mrealm!=null){
        mrealm.close();
        }

        if(bannerAppnextAPI!=null)
            bannerAppnextAPI.finish();
    }

}
