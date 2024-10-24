package com.tstudioz.fax.fme.routing

import android.app.Activity
import android.content.Intent
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import com.tstudioz.fax.fme.feature.settings.SettingsActivity
import com.tstudioz.fax.fme.view.activities.MainActivity
import java.lang.ref.WeakReference

class Router: LoginRouter, HomeRouter, SettingsRouter {

    private var activity: WeakReference<Activity>? = null

    override fun register(activity: Activity) {
        this.activity = WeakReference(activity)
    }

    override fun routeToHome() {
        val activity = activity?.get() ?: return

        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent)
        activity.finish()
    }

    override fun routeToLogin() {
        val activity = activity?.get() ?: return

        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent)
        activity.finish()
    }

    override fun routeToSettings() {
        val activity = activity?.get() ?: return

        activity.startActivity(Intent(activity, SettingsActivity::class.java))
    }

    override fun popToHome() {
        val activity = activity?.get() ?: return

        activity.finish()
    }


}

interface LoginRouter {

    fun register(activity: Activity)

    fun routeToHome()

}

interface HomeRouter {

    fun register(activity: Activity)

    fun routeToLogin()

    fun routeToSettings()

}

interface SettingsRouter {

    fun register(activity: Activity)

    fun popToHome()

    fun routeToLogin()
}