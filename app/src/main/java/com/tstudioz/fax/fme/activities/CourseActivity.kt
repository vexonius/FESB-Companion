package com.tstudioz.fax.fme.activities

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.fragments.CourseWeek

class CourseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        val intent = intent
        val imeKolegija = intent.getStringExtra("kolegij")
        //     getSupportActionBar().setTitle(imeKolegija);
        //    styledNavigation();

        if (shouldAskPermissions()) {
            askPermissions()
        }

        val bundle = Bundle()
        bundle.putString("link_kolegija", intent.getStringExtra("link_na_kolegij"))
        bundle.putString("kolegij", intent.getStringExtra("kolegij"))
        val cw = CourseWeek()
        cw.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.course_content, cw)
        ft.addToBackStack(null)
        ft.commit()

        //   loadAdsCourse();
    }

    fun styledNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(
                this,
                R.color.colorPrimaryDark
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun shouldAskPermissions(): Boolean = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1

    @TargetApi(23)
    protected fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }

    fun informUser() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            this@CourseActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permissionCheck == -1) {
            val snack = Snackbar.make(
                findViewById(R.id.relative_course_ac), "FESB Companion" +
                        " treba dopuštenje za preuzimanje dokumenata!", Snackbar.LENGTH_INDEFINITE
            )
            snack.setAction("DOPUSTI") { askPermissions() }
            snack.view.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_nice))
            snack.setActionTextColor(ContextCompat.getColor(this, R.color.white))
            snack.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            200 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                } else {
                    informUser()
                }
                return
            }
        }
    }

    override fun onBackPressed() {
        //   if (mInterstitialAd.isLoaded()) {
        //       mInterstitialAd.show();
        //   } else {
        finish()
        //   }
    }
}