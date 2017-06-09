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
import com.tstudioz.fax.fme.database.CourseWeeksAdapter;
import com.tstudioz.fax.fme.database.CoursesAdapter;
import com.tstudioz.fax.fme.database.Kolegij;
import com.tstudioz.fax.fme.database.KolegijTjedan;

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


public class CourseWeek extends Fragment {

    final RealmConfiguration realmConfig = new RealmConfiguration.Builder()
            .name("kolegiji.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(9)
            .build();


    @InjectView(R.id.course_week_rv) RecyclerView mRecyclerView;
    @InjectView(R.id.course_week_progress) ProgressBar mProgress;


    public Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.course_weeks_tab,
                container, false);

        setHasOptionsMenu(true);
        ButterKnife.inject(this, view);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Realm mRealm = Realm.getInstance(realmConfig);

        final RealmResults<KolegijTjedan> tjedni = mRealm.where(KolegijTjedan.class).findAll();



        if(!tjedni.isEmpty()) {
            CourseWeeksAdapter adapter = new CourseWeeksAdapter(tjedni);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setAdapter(adapter);


             mProgress.setVisibility(View.INVISIBLE);
             mRecyclerView.setVisibility(View.VISIBLE);
        }

        return  view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresMe).setVisible(false);
        super.onPrepareOptionsMenu(menu);
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



}
