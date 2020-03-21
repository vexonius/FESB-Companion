package com.tstudioz.fax.fme.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.tstudioz.fax.fme.Application.FESBCompanion;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.adapters.CoursesAdapter;
import com.tstudioz.fax.fme.database.Kolegij;
import com.tstudioz.fax.fme.database.Korisnik;
import com.tstudioz.fax.fme.databinding.KolegijiTabBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
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

    private CoursesAdapter kolegijiAdapter;
    private Realm credRealm, realm, mRealm;
    private OkHttpClient okHttpClient;
    private Snackbar snack;

    private KolegijiTabBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        binding = KolegijiTabBinding.inflate(inflater,
                container, false);

        setHasOptionsMenu(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        startFetching();
        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public void startFetching() {
        if (isNetworkAvailable()) {
            fetchCourses();
        } else {
            showSnacOffline();
        }
    }

    public void fetchCourses() {

        okHttpClient = FESBCompanion.getInstance().getOkHttpInstance();

        credRealm = Realm.getDefaultInstance();
        Korisnik kor = credRealm.where(Korisnik.class).findFirst();

        final RequestBody formData = new FormBody.Builder()
                .add("Username", kor.getUsername())
                .add("Password", kor.getLozinka())
                .add("IsRememberMeChecked", "true")
                .build();

        Request request = new Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava?returnURL=https://elearning.fesb" +
                        ".unist.hr/login/index.php")
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
                mRealm = Realm.getInstance(realmConfig);

                try {
                    Element content = doc.getElementById("inst17149");

                    if (content.select("div.column.c1") != null) {
                        Elements elements = content.select("div.column.c1");

                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                mRealm.deleteAll();
                            }
                        });


                        for (final Element el : elements) {
                            if (el != null) {
                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Kolegij kg = mRealm.createObject(Kolegij.class);
                                        kg.setName(el.text());
                                        kg.setLink(el.select("a").first().attr("href"));
                                    }
                                });


                            }

                        }
                    }

                } catch (Exception ex) {
                    Log.d("Exception kolegiji", ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    mRealm.close();
                }


                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showList();
                            binding.kolegijProgress.setVisibility(View.INVISIBLE);
                            binding.kolegijiRv.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    public void showList() {

        realm = Realm.getInstance(realmConfig);
        RealmResults<Kolegij> rezultati = realm.where(Kolegij.class).findAll();

        kolegijiAdapter = new CoursesAdapter(rezultati);
        binding.kolegijiRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.kolegijiRv.setHasFixedSize(true);
        binding.kolegijiRv.setAdapter(kolegijiAdapter);

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public void showSnacOffline() {
        snack = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), "Niste " +
                "povezani", Snackbar.LENGTH_INDEFINITE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (credRealm != null)
            credRealm.close();

        if (realm != null)
            realm.close();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }

        if (snack != null) {
            snack.dismiss();
        }
    }

}
