package com.tstudioz.fax.fme.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.fragments.CourseWeek;

public class CourseActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        Intent intent = getIntent();

        String imeKolegija = intent.getStringExtra("kolegij");
        getSupportActionBar().setTitle(imeKolegija);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

       styledNavigation();

        if (shouldAskPermissions()) {
            askPermissions();
        }

        Bundle bundle = new Bundle();
        bundle.putString("link_kolegija", intent.getStringExtra("link_na_kolegij"));

        final CourseWeek cw = new CourseWeek();
        cw.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.course_content, cw);
        ft.addToBackStack(null);
        ft.commit();

         loadAdsCourse();


    }

    public void styledNavigation(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    public void informUser() {
        int permissionCheck = ContextCompat.checkSelfPermission(CourseActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == -1) {
            Snackbar snack = Snackbar.make(findViewById(R.id.relative_course_ac), "FESB Companion treba dopuštenje za preuzimanje dokumenata!", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("DOPUSTI", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    askPermissions();
                }
            });
            snack.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.blue_nice));
            snack.setActionTextColor(ContextCompat.getColor(this, R.color.white));
            snack.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    informUser();
                }
                return;
            }

        }
    }

    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void loadAdsCourse() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5944203368510130/8958513574");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                finish();
            }
        });

        requestNewInterstitial();
    }

}
