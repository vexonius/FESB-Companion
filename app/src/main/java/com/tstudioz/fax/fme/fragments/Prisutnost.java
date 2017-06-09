package com.tstudioz.fax.fme.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.DolasciAdapter;
import com.tstudioz.fax.fme.database.Dolazak;
import com.tstudioz.fax.fme.database.Korisnik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Prisutnost extends Fragment {


    final RealmConfiguration realmConfig = new RealmConfiguration.Builder()
            .name("prisutnost.realm")
            .schemaVersion(10)
            .deleteRealmIfMigrationNeeded()
            .build();

    public final RealmConfiguration CredRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(5)
            .deleteRealmIfMigrationNeeded()
            .build();


    @InjectView(R.id.recyclerZimski) RecyclerView zRecyclerview;
    @InjectView(R.id.recyclerLItnji) RecyclerView lRecyclerview;
    @InjectView(R.id.progress_attend) ProgressBar mProgress;
    @InjectView(R.id.nested_attend) NestedScrollView mNested;

    public static String TAG = "Prisutnost.class";
    private Snackbar snack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.prisutnost_tab, container, false);


        ButterKnife.inject(this, view);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("logasync", "ovo je onCreateView");
            }
        });

        startFetching();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }



    public class FetchPrisutnost extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            final CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getActivity()));

            final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(cookieJar)
                    .build();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("logasync", "ovo je pocetak asynca");
                }
            });

            Request request = new Request.Builder()
                    .url("https://korisnik.fesb.unist.hr/prijava?returnURL=https://elearning.fesb.unist.hr/login/index.php")
                    .get()
                    .build();

            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("pogreska", "failure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Realm cRealm = Realm.getInstance(CredRealmCf);
                    Korisnik korisnik = cRealm.where(Korisnik.class).findFirst();

                    final RequestBody formData = new FormBody.Builder()
                            .add("Username", korisnik.getUsername())
                            .add("Password", korisnik.getLozinka())
                            .add("IsRememberMeChecked", "true")
                            .build();

                    final Request rq = new Request.Builder()
                            .url("https://korisnik.fesb.unist.hr/prijava?returnURL=https://elearning.fesb.unist.hr/login/index.php")
                            .post(formData)
                            .get()
                            .build();

                    Call call0 = okHttpClient.newCall(rq);
                    call0.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("pogreska", "failure");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            Request request = new Request.Builder()
                                    .url("https://raspored.fesb.unist.hr/part/prisutnost/opcenito/tablica")
                                    .build();

                            Call call1 = okHttpClient.newCall(request);
                            call1.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("pogreska", "failure");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {



                                    Document doc = Jsoup.parse(response.body().string());

                                    Element zimski = doc.select("div.semster.winter").first();
                                    Element litnji = doc.select("div.semster.summer").first();
                                    Element zimskaPredavanja = zimski.select("div.body.clearfix").first();
                                    Element litnjaPredavanja = litnji.select("div.body.clearfix").first();

                                    Elements zimskiKolegiji = zimskaPredavanja.select("a");

                                    final Realm mRealm = Realm.getInstance(realmConfig);
                                    final RealmResults<Dolazak> svaPrisutnost = mRealm.where(Dolazak.class).findAll();

                                    mRealm.beginTransaction();
                                    svaPrisutnost.deleteAllFromRealm();
                                    mRealm.commitTransaction();

                                    mRealm.beginTransaction();

                                    for (final Element element : zimskiKolegiji) {

                                        Request request = new Request.Builder()
                                                .url("https://raspored.fesb.unist.hr" + element.attr("href").toString())
                                                .get()
                                                .build();

                                        Call callonme = okHttpClient.newCall(request);
                                        callonme.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.d("pogreska", "failure");
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {

                                                final Realm nekiRealm = Realm.getInstance(realmConfig);

                                                Document document = Jsoup.parse(response.body().string());
                                                Element content = document.getElementsByClass("courseCategories").first();
                                                final Elements kategorije = content.select("div.courseCategory");

                                                nekiRealm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        for (Element kat : kategorije) {
                                                            final Dolazak mDolazak = nekiRealm.createObject(Dolazak.class, UUID.randomUUID().toString());

                                                            mDolazak.setSemestar(1);
                                                            mDolazak.setPredmet(element.select("div.cellContent").first().text());
                                                            mDolazak.setVrsta(kat.getElementsByClass("name").first().text());
                                                            mDolazak.setAttended(Integer.parseInt(kat.select("div.attended > span.num").first().text()));
                                                            mDolazak.setAbsent(Integer.parseInt(kat.select("div.absent > span.num").first().text()));
                                                            mDolazak.setRequired(kat.select("div.required-attendance > span").first().text());

                                                            String string = kat.select("div.required-attendance > span").first().text();
                                                            StringTokenizer st = new StringTokenizer(string, " ");
                                                            String ric1 = st.nextToken();
                                                            String ric2 = st.nextToken();
                                                            String max = st.nextToken();

                                                            mDolazak.setTotal(Integer.parseInt(max));


                                                        }
                                                    }
                                                });


                                            }
                                        });


                                    }

                                    Elements litnjiKolegiji = litnjaPredavanja.select("a");

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("logasync", "ovo je sredina asynca");
                                        }
                                    });

                                    for (final Element element : litnjiKolegiji) {


                                        Request request = new Request.Builder()
                                                .url("https://raspored.fesb.unist.hr" + element.attr("href").toString())
                                                .get()
                                                .build();

                                        Call callonme1 = okHttpClient.newCall(request);
                                        callonme1.enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                Log.d("pogreska", "failure");
                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {

                                                final Realm nekiRealm = Realm.getInstance(realmConfig);

                                                Document document = Jsoup.parse(response.body().string());
                                                Element content = document.getElementsByClass("courseCategories").first();
                                                final Elements kategorije = content.select("div.courseCategory");

                                                nekiRealm.executeTransaction(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        for (Element kat : kategorije) {
                                                            final Dolazak mDolazak = nekiRealm.createObject(Dolazak.class, UUID.randomUUID().toString());

                                                            mDolazak.setSemestar(2);
                                                            mDolazak.setPredmet(element.select("div.cellContent").first().text());
                                                            mDolazak.setVrsta(kat.getElementsByClass("name").first().text());
                                                            mDolazak.setAttended(Integer.parseInt(kat.select("div.attended > span.num").first().text()));
                                                            mDolazak.setAbsent(Integer.parseInt(kat.select("div.absent > span.num").first().text()));
                                                            mDolazak.setRequired(kat.select("div.required-attendance > span").first().text());

                                                            String string = kat.select("div.required-attendance > span").first().text();
                                                            StringTokenizer st = new StringTokenizer(string, " ");
                                                            String ric1 = st.nextToken();
                                                            String ric2 = st.nextToken();
                                                            String max = st.nextToken();

                                                            mDolazak.setTotal(Integer.parseInt(max));

                                                        }
                                                    }
                                                });


                                            }
                                        });

                                    }

                                    mRealm.commitTransaction();


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("logasync", "ovo je kraj asynca");
                                        }
                                    });


                                }

                            });
                        }
                    });


                }
            });

            return "Done";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mNested.setVisibility(View.INVISIBLE);
            mProgress.setVisibility(View.VISIBLE);
           getActivity().runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Log.d("logasync", "ovo je onpreexecute");
               }
           });

        }

        @Override
        protected void onPostExecute(String result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showRecyclerviewWinterSem();
                    showRecyclerviewSummerSem();
                    mProgress.setVisibility(View.INVISIBLE);
                    mNested.setVisibility(View.VISIBLE);
                }
            });

        }

    }

    public void showRecyclerviewWinterSem() {

        Realm mRealm = Realm.getInstance(realmConfig);
        RealmResults<Dolazak> dolasciWinter = mRealm.where(Dolazak.class).equalTo("semestar", 1).findAll();

        if (dolasciWinter.isEmpty()) {
            Log.d("message", "doslaci su prazni");
        } else {
            zRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            DolasciAdapter adapter = new DolasciAdapter(dolasciWinter);
            zRecyclerview.setAdapter(adapter);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("logasync", "ovo je recycler winter");
            }
        });

    }

    public void showRecyclerviewSummerSem() {

        Realm mRealm = Realm.getInstance(realmConfig);
        RealmResults<Dolazak> dolasciSummer = mRealm.where(Dolazak.class).equalTo("semestar", 2).findAll();

        if (dolasciSummer.isEmpty()) {
            Log.d("message", "doslaci su prazni");
        } else {
            lRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            DolasciAdapter ladapter = new DolasciAdapter(dolasciSummer);
            lRecyclerview.setAdapter(ladapter);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("logasync", "ovo je recycler summer");
            }
        });

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void startFetching(){
        if(isNetworkAvailable()) {
            new FetchPrisutnost().execute();
        }else {
            showSnacOffline();
        }
    }

    public void showSnacOffline(){
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Niste povezani", Snackbar.LENGTH_INDEFINITE);
        View vjuz = snack.getView();
        vjuz.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.setAction("PONOVI", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snack.dismiss();
                        startFetching();
                    }
                });
        snack.setActionTextColor(getResources().getColor(R.color.white));
        snack.show();
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

    public void onStop(){
        super.onStop();

        if(snack!=null){
            snack.dismiss();
        }
    }

}

