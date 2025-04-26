package com.tstudioz.fax.fme.feature.settings

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.tstudioz.fax.fme.routing.SettingsRouter
import org.koin.android.ext.android.inject

class SettingsActivity : AppCompatActivity() {

    private val router: SettingsRouter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router.register(this)

        onBack()

        setContent {
            SettingsCompose(router = router)
        }
    }

    private fun onBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                router.popToHome()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                router.popToHome()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}