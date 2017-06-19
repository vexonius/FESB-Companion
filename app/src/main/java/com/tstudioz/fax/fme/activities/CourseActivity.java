package com.tstudioz.fax.fme.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.fragments.CourseWeek;

public class CourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0.0f);
        setContentView(R.layout.activity_course);

        Intent intent = getIntent();
        String imeKolegija = intent.getStringExtra("kolegij");
        imeKolegija = imeKolegija.substring(0, imeKolegija.length()-5);
        getSupportActionBar().setTitle(imeKolegija);

        Bundle bundle = new Bundle();
        bundle.putString("link_kolegija", intent.getStringExtra("link_na_kolegij"));

        final CourseWeek cw = new CourseWeek();
        cw.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.course_content, cw);
                ft.addToBackStack(null);
                ft.commit();
    }
}
