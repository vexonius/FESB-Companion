package com.tstudioz.fax.fme.view.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.tstudioz.fax.fme.navigation.MainCompose


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainCompose()
        }
    }

    /*private fun isThereAction() {
        intent.action?.let {
            showShortcutView()
        }
    }

    private fun showShortcutView() {

    }*/

}