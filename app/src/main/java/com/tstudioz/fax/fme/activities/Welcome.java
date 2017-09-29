package com.tstudioz.fax.fme.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.fragments.SampleSlide;


public class Welcome extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.welcome_intro));
        addSlide(SampleSlide.newInstance(R.layout.welcome_timetable));
        addSlide(SampleSlide.newInstance(R.layout.welcome_files));
        addSlide(SampleSlide.newInstance(R.layout.welcome_attendance));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        onWelcomeFinish();
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        onWelcomeFinish();
        finish();
    }

    public void onWelcomeFinish(){
        SharedPreferences sharedPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor =  sharedPref.edit();
        editor.putBoolean("first_open", false);
        editor.commit();
    }
}