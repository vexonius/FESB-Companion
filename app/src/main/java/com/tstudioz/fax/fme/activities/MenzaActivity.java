package com.tstudioz.fax.fme.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.adapters.MeniesAdapter;
import com.tstudioz.fax.fme.database.Meni;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

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

public class MenzaActivity extends AppCompatActivity {

    @BindView(R.id.menza_title) TextView mMenzaTitle;
    @BindView(R.id.menza_recyclerview) RecyclerView mRecyclerView;

    RealmConfiguration menzaRealmConf = new RealmConfiguration.Builder()
            .name("menza.realm")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build();

     private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menza);

        ButterKnife.bind(this);

        Typeface typeBold = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");
        mMenzaTitle.setTypeface(typeBold);


         mRealm = Realm.getInstance(menzaRealmConf);
         mRealm.executeTransaction(new Realm.Transaction() {
             @Override
             public void execute(Realm realm) {
                 mRealm.deleteAll();
             }
         });

        startParsing();
    }


    public void startParsing(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .followSslRedirects(true)
                .build();

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

                String json = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(json);

                    JSONArray array = jsonResponse.getJSONArray("values");

                    for (int j=7; j<=9; j++) {
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mRealm.copyToRealm(meni);
                                    }
                                });

                            }
                        });

                    }

                    for (int k=13; k<=15; k++) {
                        JSONArray itemsArray = array.getJSONArray(k);

                        final Meni izborniMeni = new Meni();
                        izborniMeni.setId(itemsArray.getString(0));
                        izborniMeni.setJelo1(itemsArray.getString(1).substring(0, itemsArray.getString(1).length()-6));
                        izborniMeni.setCijena(itemsArray.getString(1).substring(itemsArray.getString(1).length()-6, itemsArray.getString(1).length()));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        mRealm.copyToRealm(izborniMeni);
                                    }
                                });

                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMenies();
                        }
                    });

                } catch (Exception ex){

                }
            }
        });

    }

    public void showMenies(){

        RealmResults<Meni> results = mRealm.where(Meni.class).findAll();

        MeniesAdapter adapter = new MeniesAdapter(results);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        if (mRealm!=null)
            mRealm.close();
    }
}
