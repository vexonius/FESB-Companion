package com.tstudioz.fax.fme.view.activities

import android.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tstudioz.fax.fme.view.fragments.SettingsFragment

/**
 * Created by etino7 on 12/01/2018.
 */
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sf = SettingsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.content, sf)
        ft.addToBackStack(null)
        ft.commit()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                // Handle the "Up" button press
                finish() // Or navigate to the parent activity
                true
            }
            // Add other cases if you have additional menu items
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        finish()
    }
}