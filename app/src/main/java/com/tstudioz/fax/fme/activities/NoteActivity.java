package com.tstudioz.fax.fme.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.LeanTask;

import java.util.UUID;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class NoteActivity  extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    private Realm tRealm;
    private String mTaskId;
    private EditText et;

    public RealmConfiguration realmTaskConfiguration = new RealmConfiguration.Builder()
            .name("tasks.realm")
            .deleteRealmIfMigrationNeeded()
            .schemaVersion(1)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tRealm=Realm.getInstance(realmTaskConfiguration);
        et = (EditText)findViewById(R.id.textEditor);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);


        if(getIntent().getExtras().getInt("mode")==1) {
            mTaskId = getIntent().getExtras().getString("task_key");
        }

        if(mTaskId!=null){
            LeanTask leanTask = tRealm.where(LeanTask.class).equalTo("id", mTaskId).findFirst();
            et.setText(leanTask.getTaskTekst());
        }

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5944203368510130/2813576206");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                finish();
            }
        });

        requestNewInterstitial();
    }

    @Override
    protected void onPause(){
        super.onPause();

        final String stringBiljeska = et.getText().toString();

        if(mTaskId!=null && !stringBiljeska.trim().equals("")){
            tRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LeanTask leanTask = tRealm.where(LeanTask.class).equalTo("id", mTaskId).findFirst();
                    leanTask.setTaskTekst(stringBiljeska);
                    leanTask.setChecked(false);
                }
            });
        } else if(mTaskId!=null && stringBiljeska.trim().equals("")) {
            tRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LeanTask leanTask = tRealm.where(LeanTask.class).equalTo("id", mTaskId).findFirst();
                    leanTask.deleteFromRealm();
                }
            });
        } else if(!stringBiljeska.trim().equals("")){
            tRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    LeanTask leanTask = tRealm.createObject(LeanTask.class, UUID.randomUUID().toString());
                    leanTask.setTaskTekst(stringBiljeska);
                    leanTask.setChecked(false);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete) {
            et.setText("");
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        super.onBackPressed();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }
}

