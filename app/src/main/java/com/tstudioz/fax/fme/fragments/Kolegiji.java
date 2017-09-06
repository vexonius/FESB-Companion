package com.tstudioz.fax.fme.fragments;


import android.content.pm.ActivityInfo;
import android.os.Bundle;


import android.support.v4.app.Fragment;
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
import com.tstudioz.fax.fme.adapters.CoursesAdapter;
import com.tstudioz.fax.fme.database.Kolegij;
import com.tstudioz.fax.fme.database.Korisnik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


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



public class Kolegiji extends Fragment {

    final RealmConfiguration realmConfig = new RealmConfiguration.Builder()
            .name("kolegiji.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(9)
            .build();

    public final RealmConfiguration CredRealmCf = new RealmConfiguration.Builder()
            .name("encrypted.realm")
            .schemaVersion(5)
            .deleteRealmIfMigrationNeeded()
            .build();

    @BindView(R.id.kolegiji_rv) RecyclerView recyclerView;
    @BindView(R.id.kolegij_progress) ProgressBar progress;

    CoursesAdapter kolegijiAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.kolegiji_tab,
                container, false);

        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        showList();
        fetchCourses();

        return  view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void fetchCourses(){


             CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getActivity()));

             final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(cookieJar)
                    .build();

                    Realm credRealm = Realm.getInstance(CredRealmCf);
                    Korisnik kor = credRealm.where(Korisnik.class).findFirst();

                    final RequestBody formData = new FormBody.Builder()
                            .add("Username", kor.getUsername())
                            .add("Password", kor.getLozinka())
                            .add("IsRememberMeChecked", "true")
                            .build();

                    Request request = new Request.Builder()
                            .url("https://korisnik.fesb.unist.hr/prijava?returnURL=https://elearning.fesb.unist.hr/login/index.php")
                            .post(formData)
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
                            Log.d("Uspjeh", doc.html());

                            Element content = doc.getElementById("inst17149");
                            Elements elements = content.select("div.column.c1");

                            final Realm mRealm = Realm.getInstance(realmConfig);
                            mRealm.beginTransaction();

                            mRealm.deleteAll();

                            for(Element el : elements){

                                Kolegij kg = mRealm.createObject(Kolegij.class);
                                kg.setName(el.text());
                                kg.setLink(el.select("a").first().attr("href"));

                            }

                            mRealm.commitTransaction();

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateList();
                                    progress.setVisibility(View.INVISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    });
    }

    public void showList(){

        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Kolegij> rezultati = realm.where(Kolegij.class).findAll();

        kolegijiAdapter = new CoursesAdapter(rezultati);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(kolegijiAdapter);

    }

    public void updateList(){
        kolegijiAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop(){
        super.onStop();
        Realm rlm = Realm.getInstance(realmConfig);
        rlm.close();
    }

}
