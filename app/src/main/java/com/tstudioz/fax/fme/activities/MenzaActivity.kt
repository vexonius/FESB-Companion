package com.tstudioz.fax.fme.activities;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.tstudioz.fax.fme.Application.FESBCompanion;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.adapters.MeniesAdapter;
import com.tstudioz.fax.fme.database.Meni;
import com.tstudioz.fax.fme.databinding.ActivityMenzaBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MenzaActivity extends AppCompatActivity {


    RealmConfiguration menzaRealmConf = new RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("menza.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();

    private Realm mRealm, nRealm;
    private Snackbar snack;
    private OkHttpClient okHttpClient;

    private ActivityMenzaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenzaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTextTypeface();

        checkConditions();
        //  loadAds();

    }

    public void checkConditions() {
        if (isNetworkAvailable()) {
            startParsing();
        } else {
            showSnacOffline();
            binding.menzaProgress.setVisibility(View.VISIBLE);
        }
    }


    public void startParsing() {

        okHttpClient = FESBCompanion.getInstance().getOkHttpInstance();

        Request request = new Request.Builder()
                .url("http://sc.dbtouch.com/menu/api.php/?place=fesb_vrh")
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                mRealm = Realm.getInstance(menzaRealmConf);
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mRealm.deleteAll();
                    }
                });

                String json = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(json);

                    JSONArray array = jsonResponse.getJSONArray("values");

                    for (int j = 7; j <= 9; j++) {

                        try {
                            JSONArray itemsArray = array.getJSONArray(j);

                            final Meni meni = new Meni();
                            meni.setId(itemsArray.getString(0));
                            meni.setType(itemsArray.getString(1));
                            meni.setJelo1(itemsArray.getString(2));
                            meni.setJelo2(itemsArray.getString(3));
                            meni.setJelo3(itemsArray.getString(4));
                            meni.setJelo4(itemsArray.getString(5));
                            meni.setDesert(itemsArray.getString(6));
                            meni.setCijena(itemsArray.getString(7));


                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    mRealm.copyToRealm(meni);
                                }
                            });


                        } catch (Exception ex) {
                            Log.d("Menza activity", ex.toString());
                        }

                    }

                    for (int k = 13; k <= 15; k++) {

                        try {
                            JSONArray itemsArray = array.getJSONArray(k);

                            final Meni izborniMeni = new Meni();
                            izborniMeni.setId(itemsArray.getString(0));
                            izborniMeni.setJelo1(itemsArray.getString(1).substring(0,
                                    itemsArray.getString(1).length() - 6));
                            izborniMeni.setCijena(itemsArray.getString(1).substring(itemsArray.getString(1).length() - 6, itemsArray.getString(1).length()));


                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    mRealm.copyToRealm(izborniMeni);
                                }
                            });


                        } catch (Exception exc) {
                            Log.d("Menza activity", exc.toString());
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.menzaProgress.setVisibility(View.INVISIBLE);
                            showMenies();
                        }
                    });

                } catch (Exception ex) {
                    Log.d("MenzaActivity", ex.getMessage());
                } finally {
                    mRealm.close();
                }
            }
        });

    }

    public void showMenies() {

        nRealm = Realm.getInstance(menzaRealmConf);

        RealmResults<Meni> results = nRealm.where(Meni.class).findAll();

        if (!results.isEmpty()) {

            MeniesAdapter adapter = new MeniesAdapter(results);
            binding.menzaRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            binding.menzaRecyclerview.setHasFixedSize(true);
            binding.menzaRecyclerview.setAdapter(adapter);

        } else {
            binding.menzaRecyclerview.setVisibility(View.INVISIBLE);
            binding.cookieHeaderRoot.setVisibility(View.VISIBLE);
        }

    }

    public void setTextTypeface() {
        Typeface typeBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        Typeface regular = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        binding.menzaTitle.setTypeface(typeBold);
        binding.cookieHeaderText.setTypeface(regular);
    }

    public void onBackPressed() {
        //  if (mInterstitialAd.isLoaded()) {
        //      mInterstitialAd.show();
        //  } else {
        finish();
        //  }
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
        snack = Snackbar.make(findViewById(R.id.menza_root), "Niste povezani",
                Snackbar.LENGTH_INDEFINITE);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.red_nice));
        snack.setAction("PONOVI", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snack.dismiss();
                checkConditions();
            }
        });
        snack.setActionTextColor(getResources().getColor(R.color.white));
        snack.show();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (okHttpClient != null)
            okHttpClient.dispatcher().cancelAll();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (nRealm != null)
            nRealm.close();

    }
}
