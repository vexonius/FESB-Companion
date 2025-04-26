package com.tstudioz.fax.fme.routing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import com.tstudioz.fax.fme.feature.settings.SettingsActivity
import com.tstudioz.fax.fme.feature.settings.model.EmailModalModel
import com.tstudioz.fax.fme.view.activities.MainActivity
import java.lang.ref.WeakReference

class Router : AppRouter, LoginRouter, HomeRouter, SettingsRouter {

    private var activity: WeakReference<Activity>? = null

    override fun register(activity: Activity) {
        this.activity = WeakReference(activity)
    }

    override fun routeToHome() {
        val activity = activity?.get() ?: return

        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun routeToLogin() {
        val activity = activity?.get() ?: return

        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
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

    override fun sendEmail(model: EmailModalModel) {
        val activity = activity?.get() ?: return

        val intent = Intent(Intent.ACTION_SEND)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(model.recipient))
            .putExtra(Intent.EXTRA_SUBJECT, model.subject)
            .putExtra(Intent.EXTRA_TEXT, model.body)
            .setType("message/rfc822")

        activity.startActivity(Intent.createChooser(intent, model.title))
    }

    override fun openCustomTab(url: String) {
        val activity = activity?.get() ?: return

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder
            .setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
            .build()

        customTabsIntent.launchUrl(activity, Uri.parse(url))
    }

}

interface AppRouter {

    fun routeToLogin()

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

    fun sendEmail(model: EmailModalModel)

    fun openCustomTab(url: String)

}