package com.tstudioz.fax.fme.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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
import com.tstudioz.fax.fme.database.CoursesAdapter;
import com.tstudioz.fax.fme.database.Kolegij;
import com.tstudioz.fax.fme.database.KolegijTjedan;
import com.tstudioz.fax.fme.database.Korisnik;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


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

    @InjectView(R.id.kolegiji_rv) RecyclerView recyclerView;
    @InjectView(R.id.kolegij_progress) ProgressBar progress;


    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.kolegiji_tab,
                container, false);

        setHasOptionsMenu(true);
        ButterKnife.inject(this, view);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new FetchCourses().execute();

        return  view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    public class FetchCourses extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getActivity()));


             final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .cookieJar(cookieJar)
                    .build();

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


                        }
                    });
                }
            });

            return "Done";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String result) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showList();

                }
            });



        }
    }

    public void showList(){

        Realm realm = Realm.getInstance(realmConfig);
        RealmResults<Kolegij> rezultati = realm.where(Kolegij.class).findAll();


        if (rezultati.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
        } else{

            CoursesAdapter adapter = new CoursesAdapter(rezultati);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

            progress.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);

        }


    }

    @Override
    public void onStop(){
        super.onStop();
        Realm rlm = Realm.getInstance(realmConfig);
        rlm.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            activity = (Activity) context;

    }



    public void fetchCourseContent(final String url, Context context){

        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));


        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cookieJar(cookieJar)
                .build();

        final RequestBody formData = new FormBody.Builder()
                .add("Username", "temer00")
                .add("Password", "Jc72028N")
                .add("IsRememberMeChecked", "true")
                .build();

        final Request rq = new Request.Builder()
                .url("https://korisnik.fesb.unist.hr/prijava")
                .post(formData)
                .get()
                .build();

        Call call = okHttpClient.newCall(rq);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("pogreska", "failure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {



                Request request = new Request.Builder()
                        .url(url)
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
                      //  Log.d("uradi mi to", doc.body().text());

                        Element content = doc.select("div.course-content").first();

                        Elements welements = content.getElementsByClass("weekdates");

                        Elements selements = content.select("li.section");

                        Elements delements = content.getElementsByClass("no-overflow");

                          final Realm mRealm = Realm.getInstance(realmConfig);
                          mRealm.beginTransaction();



                          for(Element element : selements){

                              KolegijTjedan kolegijTjedan = mRealm.createObject(KolegijTjedan.class);

                              if(content.hasClass("weekdates")) {
                                  kolegijTjedan.setTjedan(element.getElementsByClass("weekdates").first().text());
                              }

                              if(content.hasClass("no-overflow")) {
                                  kolegijTjedan.setOpis(element.getElementsByClass("no-overflow").first().text());
                              }


                          }

                        mRealm.commitTransaction();



                        Log.d("prvi element", welements.first().text());
                        Log.d("drugi element", delements.first().text());
                        Log.d("glavni element", selements.first().toString());





                    }

                  });
            }
        });



    }

    public void showCourseWeeksToMe(){



   //    final Realm mRealm = Realm.getInstance(realmConfig);

   //    final RealmResults<KolegijTjedan> tjedni = mRealm.where(KolegijTjedan.class).findAll();

   //    if(!tjedni.isEmpty()) {
   //        CourseWeeksAdapter adapter = new CourseWeeksAdapter(tjedni);
   //        recyclerView.setAdapter(adapter);
   //    }



      // progress.setVisibility(View.INVISIBLE);
      // recyclerView.setVisibility(View.VISIBLE);

    }


/**
    public static class FetchCourseContent extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String...params){

            String courseUrl = params[0];


            final Request request = new Request.Builder()
                    .url(courseUrl)
                    .get()
                    .build();

            Call newCall = okHttpClient.newCall(request);
            newCall.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("odgovor", "neuspjesno");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    Document document = Jsoup.parse(response.body().string());
                    Log.d("helo", document.html());

                }
            });




            return "done";
        }

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected void onPostExecute(String result){


        }

    }

*/

}
