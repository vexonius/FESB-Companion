package com.tstudioz.fax.fme.view.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro2
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.view.fragments.SampleSlideFragment

class Welcome : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSlide(SampleSlideFragment.newInstance(R.layout.welcome_intro))
        addSlide(SampleSlideFragment.newInstance(R.layout.welcome_timetable))
        //addSlide(SampleSlide.newInstance(R.layout.welcome_files))
        addSlide(SampleSlideFragment.newInstance(R.layout.welcome_attendance))
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        onWelcomeFinish()
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        onWelcomeFinish()
        finish()
    }

    private fun onWelcomeFinish() {
        val sharedPref = getSharedPreferences("PRIVATE_PREFS", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("first_open", false)
        editor.apply()
    }
}