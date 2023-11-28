package com.tstudioz.fax.fme.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R/*
import com.tstudioz.fax.fme.database.AmoledSetting*/
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.databinding.ActivityMainBinding
import com.tstudioz.fax.fme.fragments.Home
import com.tstudioz.fax.fme.fragments.Prisutnost
import com.tstudioz.fax.fme.fragments.TimeTable
import com.tstudioz.fax.fme.networking.NetworkUtils
import com.tstudioz.fax.fme.ui.mainscreen.MainViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.exceptions.RealmException
import kotlinx.coroutines.InternalCoroutinesApi
import nl.joery.animatedbottombar.AnimatedBottomBar
import nl.joery.animatedbottombar.AnimatedBottomBar.Tab
import nl.joery.animatedbottombar.BottomBarStyle
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.koin.java.KoinJavaComponent.get
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

class MainActivity : AppCompatActivity() {
    var date: String? = null
    var back_pressed: Long = 0
    private var realmLog: Realm? = null
    private var client: OkHttpClient? = null
    private var hf: Home? = null
    private var snack: Snackbar? = null
    private var bottomSheet: BottomSheetDialog? = null
    private var shPref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    @OptIn(InternalCoroutinesApi::class)
    private val viewModel = get(MainViewModel::class.java)
    private var binding: ActivityMainBinding? = null
    val mainRealmConfig = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("glavni.realm")
        .schemaVersion(3)
        .deleteRealmIfMigrationNeeded()
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        //checkTheme()
        //setTheme(R.style.AppTheme_Custom)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        setUpToolbar()
        getDate()
        testBottomBar()

        isThereAction

        setFragmentTab()
        checkUser()
        checkVersion()
        shouldShowGDPRDialog()
    }

    private fun checkTheme() {
        shPref = getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE)
        val themeMode = shPref?.getString("Theme_mode", "1")?.toInt()
        when (themeMode) {
            1 -> {
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //setTheme(R.style.AppTheme)
            }

            2 -> {
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                //setTheme(R.style.AppTheme_Custom)
            }

            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }
    val isThereAction: Unit
        get() {
            if (intent.action != null) {
                showShortcutView()
            } else {
                setDefaultScreen()
            }
        }

    /*private fun setTheme(){

        try {
            realmLog = Realm.getDefaultInstance()
            if (realmLog?.where(AmoledSetting::class.java)?.findFirst()!!.themeSetting){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        } catch (e: Exception) {
            Log.e("settings exp", e.message!!)
        } finally {
            realmLog?.close()
        }
    }*/
    private fun setDefaultScreen() {
        //getSupportActionBar().hide();
        val ft = supportFragmentManager.beginTransaction()
        hf = Home()
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
        ft.replace(R.id.frame, hf!!)
        ft.addToBackStack(null)
        ft.commit()
    }

    @OptIn(InternalCoroutinesApi::class)
    fun checkUser() {
        realmLog = Realm.getDefaultInstance()
        if (realmLog != null) {
            var korisnik: Korisnik? = null
            try {
                korisnik = realmLog!!.where(Korisnik::class.java).findFirst()
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                realmLog!!.close()
            }
            if (korisnik != null) {
                mojRaspored
            } else {
                invalidCreds()
            }
        } else {
            shPref = instance!!.sP
            assert(shPref != null)
            editor = shPref!!.edit()
            editor?.putBoolean("loged_in", false)
            editor?.commit()
            Toast.makeText(this, "Potrebna je prijava!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    @SuppressLint("RestrictedApi")
    fun setUpToolbar() {
        val actionbar = supportActionBar
        //actionbar.hide();
        actionbar!!.setShowHideAnimationEnabled(false)
        actionbar.elevation = 1.0f
        actionbar.setDisplayShowHomeEnabled(false)
    }

    fun testBottomBar() {
        val bar = binding!!.bottomBar

        /*bar.addTab(new AnimatedBottomBar.Tab(getDrawable(R.drawable.attend), "Prisutnost", 1));
        bar.addTab(new AnimatedBottomBar.Tab(getDrawable(R.drawable.cal), "Raspored", 2));
        bar.addTab(new AnimatedBottomBar.Tab(getDrawable(R.drawable.command_line), "Home", 3));
        bar.addTab(new AnimatedBottomBar.Tab(getDrawable(R.drawable.courses), "Kolegiji", 4));
        bar.addTab(new AnimatedBottomBar.Tab(getDrawable(R.drawable.mail), "Outlook", 5));*/
        bar.addTab(bar.createTab(getDrawable(R.drawable.attend), "Prisutnost", R.id.tab_prisutnost))
        bar.addTab(bar.createTab(getDrawable(R.drawable.command_line), "Home", R.id.tab_home))
        bar.addTab(bar.createTab(getDrawable(R.drawable.cal), "Raspored", R.id.tab_raspored))
        bar.selectTabById(R.id.tab_home, true)
    }

    fun setFragmentTab() {
        binding!!.bottomBar.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(i: Int, tab: Tab?, i1: Int, tab1: Tab) {
                beginFragTransaction(tab1.id)
            }

            override fun onTabReselected(i: Int, tab: Tab) {}
        })
    }

    fun beginFragTransaction(pos: Int) {
        val ft = supportFragmentManager.beginTransaction()
        when (pos) {
            R.id.tab_prisutnost-> {
                val ik = Prisutnost()
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                ft.replace(R.id.frame, ik)
                ft.addToBackStack(null)
                ft.commit()
                supportActionBar!!.title = "Prisutnost"
                supportActionBar!!.show()
            }

            R.id.tab_home -> {
                supportActionBar!!.title = "FESB Companion"
                val hf0 = Home()
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                ft.replace(R.id.frame, hf0)
                ft.addToBackStack(null)
                ft.commit()
            }

            R.id.tab_raspored -> {
                val lf = TimeTable()
                ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                ft.replace(R.id.frame, lf)
                ft.addToBackStack(null)
                ft.commit()
                supportActionBar!!.title = "Raspored"
                supportActionBar!!.show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.refresMe -> if (NetworkUtils.isNetworkAvailable(this)) {
                mojRaspored
            } else {
                showSnacOffline()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        exitApp()
    }

    @OptIn(InternalCoroutinesApi::class)
    val mojRaspored: Unit
        get() {
            realmLog = Realm.getDefaultInstance()
            val kor = realmLog?.where(Korisnik::class.java)?.findFirst()


            // Get calendar set to current date and time
            val c = Calendar.getInstance()
            val s = Calendar.getInstance()

            // Set the calendar to monday of the current week
            c[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
            val dfday: DateFormat = SimpleDateFormat("dd")
            val dfmonth: DateFormat = SimpleDateFormat("MM")
            val dfyear: DateFormat = SimpleDateFormat("yyyy")

            // Set the calendar to Saturday of the current week
            s[Calendar.DAY_OF_WEEK] = Calendar.SATURDAY
            val sday: DateFormat = SimpleDateFormat("dd")
            val smonth: DateFormat = SimpleDateFormat("MM")
            val syear: DateFormat = SimpleDateFormat("yyyy")
            client = instance!!.okHttpInstance
            val request: Request = Request.Builder()
                .url(
                    "https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId" +
                            "=" + kor!!.getUsername()
                        .toString() + "&MinDate=" + dfmonth.format(c.time) +
                            "%2F" + dfday.format(c.time) + "%2F" + dfyear.format(c.time) + "%2022%3A44%3A48&MaxDate=" +
                            smonth.format(s.time) + "%2F" + sday.format(s.time) + "%2F" + syear.format(
                        s.time
                    ) + "%2022%3A44%3A48"
                )
                .get()
                .build()
            val call = client!!.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(ContentValues.TAG, "Exception caught", e)
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val realm = Realm.getInstance(mainRealmConfig)
                    try {
                        if (response.code == 500) {
                            client!!.dispatcher.cancelAll()
                            invalidCreds()
                        }
                        val doc = Jsoup.parse(response.body!!.string())
                        realm.executeTransaction { realm ->
                            val svaPredavanja = realm.where(
                                Predavanja::class.java
                            ).findAll()
                            svaPredavanja.deleteAllFromRealm()
                        }
                        if (response.isSuccessful) {
                            val elements = doc.select("div.event")
                            try {
                                realm.executeTransaction { realm ->
                                    for (e in elements) {
                                        val predavanja = realm.createObject(
                                            Predavanja::class.java,
                                            UUID.randomUUID().toString()
                                        )
                                        if (e.hasAttr("data-id")) {
                                            val attr = e.attr("data-id")
                                            predavanja.objectId = attr.toInt()
                                        }
                                        predavanja.predavanjeIme =
                                            e.select("span.groupCategory").text()
                                        predavanja.predmetPredavanja = e.select(
                                            "span.name" +
                                                    ".normal"
                                        ).text()
                                        predavanja.rasponVremena = e.select("div.timespan").text()
                                        predavanja.grupa = e.select("span.group.normal").text()
                                        predavanja.grupaShort = e.select("span.group.short").text()
                                        predavanja.dvorana = e.select("span.resource").text()
                                        predavanja.detaljnoVrijeme = e.select(
                                            "div.detailItem" +
                                                    ".datetime"
                                        ).text()
                                        predavanja.profesor = e.select("div.detailItem.user").text()
                                    }
                                }
                            } finally {
                                realm.close()
                            }
                        }
                        runOnUiThread {
                            if (hf != null) {
                                hf!!.showList()
                            }
                        }
                    } catch (e: IOException) {
                        Log.e(ContentValues.TAG, "Exception caught: ", e)
                    }
                }
            })
            realmLog?.close()
        }

    @OptIn(InternalCoroutinesApi::class)
    fun invalidCreds() {
        shPref = instance!!.sP
        editor = shPref!!.edit()
        editor?.putBoolean("loged_in", false)
        editor?.apply()
        realmLog = Realm.getDefaultInstance()
        try {
            realmLog?.executeTransaction(Realm.Transaction { realmLog?.deleteAll() })
        } catch (ex: RealmException) {
            Log.e("MainActivity", ex.toString())
        } finally {
            realmLog?.close()
        }
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }

    private fun getDate() {
        val df: DateFormat = SimpleDateFormat("d.M.yyyy.")
        date = df.format(Calendar.getInstance().time)
    }

    fun showSnacOffline() {
        snack = Snackbar.make(
            findViewById(R.id.coordinatorLayout), "Niste povezani",
            Snackbar.LENGTH_LONG
        )
        val vjuz = snack!!.view
        vjuz.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red_nice))
        snack!!.show()
    }

    fun exitApp() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            finish()
        } else {
            snack = Snackbar.make(
                findViewById(R.id.coordinatorLayout), "Pritisnite nazad za " +
                        "izlazak iz aplikacije", Snackbar.LENGTH_SHORT
            )
            val viewto = snack!!.view
            viewto.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.grey_nice))
            snack!!.show()
        }
        back_pressed = System.currentTimeMillis()
    }

    fun showShortcutView() {
        var shortPosition = 0
        if (intent.action == "podsjetnik") {
            val newIntent = Intent(this@MainActivity, NoteActivity::class.java)
            newIntent.putExtra("mode", 2)
            newIntent.putExtra("task_key", "")
            startActivity(newIntent)
        } else {
            when (intent.action) {
                "raspored" -> shortPosition = 1
                "prisutnost" -> shortPosition = 2
            }
            beginFragTransaction(shortPosition)
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    fun checkVersion() {
        shPref = instance!!.sP
        val staraVerzija = shPref!!.getInt("version_number", 14)
        val trenutnaVerzija = versionCode
        if (staraVerzija < trenutnaVerzija) {
            showChangelog()
            editor = shPref!!.edit()
            editor?.putInt("version_number", trenutnaVerzija)
            editor?.commit()
        } else {
            return
        }
    }

    val versionCode: Int
        get() {
            var versionCode = 0
            try {
                val pInfo = this.packageManager.getPackageInfo(packageName, 0)
                versionCode = pInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return versionCode
        }

    private fun showChangelog() {
        val view =
            LayoutInflater.from(this).inflate(R.layout.licence_view, null) as NestedScrollView
        val wv = view.findViewById<View>(R.id.webvju) as WebView
        wv.loadUrl("file:///android_asset/changelog.html")
        bottomSheet = BottomSheetDialog(this)
        bottomSheet!!.setCancelable(true)
        bottomSheet!!.setContentView(view)
        bottomSheet!!.setCanceledOnTouchOutside(true)
        bottomSheet!!.show()
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun shouldShowGDPRDialog() {
        shPref = instance!!.sP
        val bool = shPref!!.getBoolean("GDPR_agreed", false)
        if (!bool) {
            showGDPRCompliance()
            editor = shPref!!.edit()
            editor?.putBoolean("GDPR_agreed", true)
            editor?.commit()
        }
    }

    private fun showGDPRCompliance() {
        val view = LayoutInflater.from(this).inflate(R.layout.gdpr_layout, null) as ConstraintLayout
        val heading = view.findViewById<View>(R.id.terms_heading) as TextView
        val desc = view.findViewById<View>(R.id.terms_text) as TextView
        val typeBold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold.ttf")
        heading.typeface = typeBold
        val typeRegular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular.ttf")
        desc.typeface = typeRegular
        val more = view.findViewById<View>(R.id.button_more) as TextView
        more.typeface = typeBold
        more.setOnClickListener { view ->
            try {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent =
                    builder.setToolbarColor(resources.getColor(R.color.colorPrimaryDark)).build()
                customTabsIntent.launchUrl(
                    view.context, Uri.parse(
                        "http://tstud" +
                                ".io/privacy"
                    )
                )
            } catch (ex: Exception) {
                Toast.makeText(
                    view.context, "Ažurirajte Chrome preglednik za pregled " +
                            "web stranice", Toast.LENGTH_SHORT
                ).show()
            }
        }
        val ok = view.findViewById<View>(R.id.button_ok) as TextView
        ok.typeface = typeBold
        ok.setOnClickListener { bottomSheet!!.dismiss() }
        bottomSheet = BottomSheetDialog(this)
        bottomSheet!!.setCancelable(false)
        bottomSheet!!.setContentView(view)
        bottomSheet!!.setCanceledOnTouchOutside(false)
        bottomSheet!!.show()
    }

    public override fun onStop() {
        super.onStop()
        if (client != null) {
            client!!.dispatcher.cancelAll()
        }
    }

    public override fun onResume() {
        super.onResume()
    }
}