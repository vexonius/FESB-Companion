package com.tstudioz.fax.fme.view.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.tstudioz.fax.fme.navigation.Attendance
import com.tstudioz.fax.fme.navigation.Home
import com.tstudioz.fax.fme.navigation.MainCompose
import com.tstudioz.fax.fme.navigation.TimeTable
import com.tstudioz.fax.fme.routing.HomeRouter
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val router: HomeRouter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        router.register(this)

        setContent {
            val startDestination = when (intent.action.toString()) {
                "raspored" -> TimeTable
                "prisutnost" -> Attendance
                "reminder" -> Home
                else -> Home
            }

            MainCompose(startDestination, router)
        }
    }

    override fun onResume() {
        super.onResume()

        router.register(this)
    }

}