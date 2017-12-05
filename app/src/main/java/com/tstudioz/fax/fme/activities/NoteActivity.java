package com.tstudioz.fax.fme.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.tstudioz.fax.fme.R;

/**
 * Created by amarthus on 19-Mar-17.
 */

public class NoteActivity  extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText et = (EditText)findViewById(R.id.textEditor);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String tekstic = sharedPref.getString("mojtext", "");
        et.setText(tekstic);

        et.setSelection(et.getText().length());

     //   mInterstitialAd = new InterstitialAd(this);
     //   mInterstitialAd.setAdUnitId("ca-app-pub-5944203368510130/2813576206");
//
     //   mInterstitialAd.setAdListener(new AdListener() {
     //       @Override
     //       public void onAdClosed() {
     //           requestNewInterstitial();
     //           finish();
     //       }
     //   });
//
     //   requestNewInterstitial();
    }

    @Override
    protected void onPause(){
        super.onPause();

        EditText editText = (EditText)findViewById(R.id.textEditor);
        String stringBiljeska = editText.getText().toString();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor =  sharedPreferences.edit();
        editor.putString("mojtext", stringBiljeska);
        editor.commit();

       // mInterstitialAd.show();
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
            EditText edtme = (EditText)findViewById(R.id.textEditor);
            edtme.getText().clear();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        super.onBackPressed();
    }

    private void requestNewInterstitial() {
      //  AdRequest adRequest = new AdRequest.Builder()
      //          .build();
      //  mInterstitialAd.loadAd(adRequest);
    }
}

