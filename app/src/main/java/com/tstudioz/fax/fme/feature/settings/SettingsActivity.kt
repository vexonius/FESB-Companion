package com.tstudioz.fax.fme.feature.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*val sf = SettingsFragment()
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(android.R.id.content, sf)
        ft.addToBackStack(null)
        ft.commit()*/
        setContent {
            SettingsCompose()
        }

        onBack()
    }

    private fun onBack(){
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle the "Up" button press
                finish() // Or navigate to the parent activity
                true
            }
            // Add other cases if you have additional menu items
            else -> super.onOptionsItemSelected(item)
        }
    }
}