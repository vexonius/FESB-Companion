package com.tstudioz.fax.fme.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
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
import com.tstudioz.fax.fme.adapters.DolasciAdapter;
import com.tstudioz.fax.fme.database.Dolazak;
import com.tstudioz.fax.fme.database.Korisnik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.recyclerZimski) RecyclerView zRecyclerview;
    @BindView(R.id.recyclerLItnji) RecyclerView lRecyclerview;
    @BindView(R.id.progress_attend) ProgressBar mProgress;
    @BindView(R.id.nested_attend) NestedScrollView mNested;

    private Snackbar snack;
    DolasciAdapter winterAdapter, summerAdapter;

    Realm nRealm, cRealm, sRealm, wRealm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.prisutnost_tab, container, false);

        ButterKnife.bind(this, view);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mNested.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);

        startFetching();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

  public void fetchPrisutnost(){

            final CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getActivity()));

            final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(cookieJar)
                    .build();

                    nRealm = Realm.getInstance(realmConfig);
                    final RealmResults<Dolazak> svaPrisutnost = nRealm.where(Dolazak.class).findAll();

                    nRealm.beginTransaction();
                    svaPrisutnost.deleteAllFromRealm();
                    nRealm.commitTransaction();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showRecyclerviewWinterSem();
                            showRecyclerviewSummerSem();
                        }
                    });

                    cRealm = Realm.getInstance(CredRealmCf);
                    Korisnik korisnik = cRealm.where(Korisnik.class).findFirst();

                    final RequestBody formData = new FormBody.Builder()
                            .add("Username", korisnik.getUsername())
                            .add("Password", korisnik.getLozinka())
                            .add("IsRememberMeChecked", "true")
                            .build();

                    final Request rq = new Request.Builder()
                            .url("https://korisnik.fesb.unist.hr/prijava?returnUrl=https://raspored.fesb.unist.hr")
                            .post(formData)
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
                                    .get()
                                    .build();

                            Call call1 = okHttpClient.newCall(request);
                            call1.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("pogreska", "failure");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                    if (response.code() != 500) {

                                        Document doc = Jsoup.parse(response.body().string());

                                        Element zimski = doc.select("div.semster.winter").first();
                                        Element litnji = doc.select("div.semster.summer").first();
                                        Element zimskaPredavanja = zimski.select("div.body.clearfix").first();
                                        Element litnjaPredavanja = litnji.select("div.body.clearfix").first();

                                        final Realm mRealm = Realm.getInstance(realmConfig);

                                 try {
                                     mRealm.beginTransaction();

                                     if (doc.select("#studentGeneralReportTable > div.semster.winter > div.emptyList") == null) {
                                         Elements zimskiKolegiji = zimskaPredavanja.select("a");


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

                                     }

                                     if (doc.select("#studentGeneralReportTable > div.semster.summer > div.emptyList") == null) {
                                         Elements litnjiKolegiji = litnjaPredavanja.select("a");


                                         for (final Element element : litnjiKolegiji) {


                                             Request request = new Request.Builder()
                                                     .url("https://raspored.fesb.unist.hr" + element.attr("href").toString())
                                                     .get()
                                                     .build();

                                             Call callonme1 = okHttpClient.newCall(request);
                                             callonme1.enqueue(new Callback() {
                                                 @Override
                                                 public void onFailure(Call call, IOException e) {
                                                     showSnackError();
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
                                     }

                                     mRealm.commitTransaction();
                                 } finally {
                                     mRealm.close();
                                 }

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateAdapters();
                                                mProgress.setVisibility(View.INVISIBLE);
                                                mNested.setVisibility(View.VISIBLE);
                                            }
                                        });


                                    }else {
                                        showSnackError();
                                    }
                                }

                            });
                        }
                    });

    }

    public void showRecyclerviewWinterSem() {

        wRealm = Realm.getInstance(realmConfig);
        RealmResults<Dolazak> dolasciWinter = wRealm.where(Dolazak.class).equalTo("semestar", 1).findAll();

        zRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        winterAdapter = new DolasciAdapter(dolasciWinter);
        zRecyclerview.setAdapter(winterAdapter);


    }

    public void showRecyclerviewSummerSem() {

        sRealm = Realm.getInstance(realmConfig);
        RealmResults<Dolazak> dolasciSummer = sRealm.where(Dolazak.class).equalTo("semestar", 2).findAll();

        lRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        summerAdapter= new DolasciAdapter(dolasciSummer);
        lRecyclerview.setAdapter(summerAdapter);

    }

    public void updateAdapters(){
        winterAdapter.notifyDataSetChanged();
        summerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void startFetching(){
        if(isNetworkAvailable()) {
            fetchPrisutnost();
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

    public void showSnackError(){
        snack=Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Došlo je do pogreške", Snackbar.LENGTH_LONG);
        View okvir = snack.getView();
        okvir.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red_nice));
        snack.setAction(("PONOVI"), new View.OnClickListener() {
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

    @Override
    public void onDestroy(){
        super.onDestroy();

        if(nRealm!=null)
            nRealm.close();

        if(cRealm!=null)
            cRealm.close();

        if(sRealm!=null)
            sRealm.close();

        if(wRealm!=null)
            wRealm.close();
    }

}

