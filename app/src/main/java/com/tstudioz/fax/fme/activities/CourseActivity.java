package com.tstudioz.fax.fme.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.fragments.CourseWeek;

public class CourseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getSupportActionBar().setElevation(0.0f);
        setContentView(R.layout.activity_course);

        Intent intent = getIntent();
        String imeKolegija = intent.getStringExtra("kolegij");
        imeKolegija = imeKolegija.substring(0, imeKolegija.length()-5);
        getSupportActionBar().setTitle(imeKolegija);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        Bundle bundle = new Bundle();
        bundle.putString("link_kolegija", intent.getStringExtra("link_na_kolegij"));

        final CourseWeek cw = new CourseWeek();
        cw.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.course_content, cw);
                ft.addToBackStack(null);
                ft.commit();
    }

    public void onBackPressed(){
        finish();
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
}
