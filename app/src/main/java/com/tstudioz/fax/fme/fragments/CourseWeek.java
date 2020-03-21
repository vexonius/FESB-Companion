package com.tstudioz.fax.fme.fragments;


import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.adapters.CourseWeeksAdapter;
import com.tstudioz.fax.fme.database.KolegijTjedan;
import com.tstudioz.fax.fme.database.Korisnik;
import com.tstudioz.fax.fme.database.Materijal;
import com.tstudioz.fax.fme.databinding.CourseWeeksTabBinding;

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
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CourseWeek extends Fragment {

    final RealmConfiguration realmConfig = new RealmConfiguration.Builder()
            .name("kolegiji.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(9)
            .build();

    private Realm tRealm;
    private Realm wRealm;
    private CourseWeeksAdapter adapter;

    private CourseWeeksTabBinding binding;

    private OkHttpClient okHttpClient;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        String link = getArguments().getString("link_kolegija");
        String ime = getArguments().getString("kolegij");

        binding = CourseWeeksTabBinding.inflate(inflater, container, false);

        setHasOptionsMenu(true);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding.bigName.setText(ime);

        fetchCourseContent(link);

        return binding.getRoot();
    }

    public void fetchCourseContent(final String url) {

        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(),
                new SharedPrefsCookiePersistor(getActivity()));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.courseWeekRv.setVisibility(View.INVISIBLE);
                binding.courseWeekProgress.setVisibility(View.VISIBLE);
                showWeeks();
            }
        });


        okHttpClient = new OkHttpClient().newBuilder()
                .followRedirects(true)
                .followSslRedirects(true)
                .cookieJar(cookieJar)
                .build();

        tRealm = Realm.getDefaultInstance();
        Korisnik korisnik = tRealm.where(Korisnik.class).findFirst();

        try {

            final RequestBody formData = new FormBody.Builder()
                    .add("Username", korisnik.getUsername())
                    .add("Password", korisnik.getLozinka())
                    .add("IsRememberMeChecked", "true")
                    .build();

            Request rq = new Request.Builder()
                    .url("https://korisnik.fesb.unist.hr/prijava?returnURL=https://elearning.fesb" +
                            ".unist.hr/login/index.php")
                    .post(formData)
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

                            Document doc = Jsoup.parse(response.body().string());

                            Realm mRealm = Realm.getInstance(realmConfig);
                            final RealmResults<KolegijTjedan> tjedni =
                                    mRealm.where(KolegijTjedan.class).findAll();
                            mRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    tjedni.deleteAllFromRealm();
                                }
                            });

                            Element content = doc.select("div.course-content").first();
                            Elements selements = content.getElementsByClass("section main " +
                                    "clearfix");

                            Element currentElement = content.getElementsByClass("section main " +
                                    "clearfix current").first();

                            if (currentElement != null)
                                selements.add(0, currentElement);

                            try {
                                mRealm.beginTransaction();

                                int i = 0;
                                for (Element element : selements) {

                                    KolegijTjedan kolegijTjedan =
                                            mRealm.createObject(KolegijTjedan.class);
                                    kolegijTjedan.setIndex(i++);

                                    kolegijTjedan.setTjedan(element.select("div.content > h3").text());
                                    kolegijTjedan.setOpis(element.select("div.summary").first().text());

                                    if (element.getElementsByClass("section img-text").first() != null) {
                                        Element modz = element.getElementsByClass("section " +
                                                "img-text").first();
                                        Elements sections = modz.select("div.mod-indent");

                                        for (Element sekcija : sections) {

                                            if ((!sekcija.select("span.instancename").text().equals("News forum"))) {
                                                if (!sekcija.select("span.instancename").text().isEmpty()) {
                                                    Materijal materijal =
                                                            mRealm.createObject(Materijal.class);
                                                    materijal.setImeMtarijala(sekcija.select(
                                                            "span.instancename").text());

                                                    if (sekcija.getElementsByClass("activityicon").first() != null) {
                                                        Element ikona =
                                                                sekcija.getElementsByClass(
                                                                        "activityicon").first();

                                                        switch (ikona.attr("src")) {
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fpdf&rev=305":
                                                                materijal.setIcon(R.drawable.pdf);
                                                                materijal.setVrsta("pdf");
                                                                String imeMat = sekcija.select(
                                                                        "span.instancename").text();
                                                                if (imeMat.length() > 5) {
                                                                    materijal.setImeMtarijala(sekcija.select("span.instancename").text().substring(0, sekcija.select("span.instancename").text().length() - 5));
                                                                }
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fword&rev=305":
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fdocm&rev=305":
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fdocx&rev=305":
                                                                materijal.setIcon(R.drawable.word);
                                                                materijal.setVrsta("docx");
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fpptx&rev=305":
                                                                materijal.setIcon(R.drawable.ppt);
                                                                materijal.setVrsta("pptx");
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fxlsx&rev=305":
                                                                materijal.setIcon(R.drawable.excel);
                                                                materijal.setVrsta("xlsx");
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=icon&rev=305&component" +
                                                                    "=folder":
                                                                materijal.setIcon(R.drawable.folder);
                                                                materijal.setVrsta("folder");
                                                                materijal.setDownloadable(0);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Ftext&rev=305":
                                                                materijal.setIcon(R.drawable.txt);
                                                                materijal.setVrsta("txt");
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=icon&rev=305&component" +
                                                                    "=choice":
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=icon&rev=305&component=quiz":
                                                                materijal.setIcon(R.drawable.quiz);
                                                                materijal.setVrsta("quiz");
                                                                materijal.setDownloadable(0);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=icon&rev=305&component" +
                                                                    "=assignment":
                                                                materijal.setIcon(R.drawable.assign);
                                                                materijal.setVrsta("assign");
                                                                materijal.setDownloadable(0);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fhtml&rev=305":
                                                                materijal.setIcon(R.drawable.link);
                                                                materijal.setVrsta("link");
                                                                materijal.setDownloadable(0);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=icon&rev=305&component=page":
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fweb&rev=305":
                                                                materijal.setIcon(R.drawable.link);
                                                                materijal.setVrsta("link");
                                                                materijal.setDownloadable(0);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fzip&rev=305":
                                                                materijal.setIcon(R.drawable.archive);
                                                                materijal.setVrsta("zip");
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            case "https://elearning.fesb.unist" +
                                                                    ".hr/theme/image" +
                                                                    ".php?theme=fesb_metro&image" +
                                                                    "=f%2Fimage&rev=305":
                                                                materijal.setIcon(R.drawable.imagelink);
                                                                materijal.setVrsta("jpg");
                                                                materijal.setDownloadable(1);
                                                                break;
                                                            default:
                                                                materijal.setIcon(R.drawable.unknown);
                                                                materijal.setVrsta("unknown");
                                                                materijal.setDownloadable(0);
                                                                break;
                                                        }
                                                    }

                                                    materijal.setUrl(sekcija.select("a").attr(
                                                            "href"));
                                                    kolegijTjedan.materijali.add(materijal);
                                                }
                                            }
                                        }
                                    }
                                }

                                mRealm.commitTransaction();

                            } catch (Exception ex) {
                                Log.e("Exception found", ex.toString());

                            } finally {
                                mRealm.close();
                            }

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                        binding.courseWeekProgress.setVisibility(View.INVISIBLE);
                                        binding.courseWeekRv.setVisibility(View.VISIBLE);
                                    }
                                });

                            }

                        }

                    });
                }
            });

        } finally {
            tRealm.close();
        }

    }

    public void showWeeks() {
        wRealm = Realm.getInstance(realmConfig);
        final RealmResults<KolegijTjedan> tjedni = wRealm.where(KolegijTjedan.class).findAll();

        try {
            adapter = new CourseWeeksAdapter(tjedni);
            binding.courseWeekRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.courseWeekRv.setHasFixedSize(true);
            binding.courseWeekRv.setAdapter(adapter);
        } finally {
            wRealm.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void onStop() {
        super.onStop();
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
    }
}