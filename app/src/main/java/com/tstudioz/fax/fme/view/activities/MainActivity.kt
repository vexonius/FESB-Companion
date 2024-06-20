package com.tstudioz.fax.fme.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.BuildConfig
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import com.tstudioz.fax.fme.databinding.ActivityMainBinding
import com.tstudioz.fax.fme.feature.login.view.LoginActivity
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.models.util.PreferenceHelper.set
import com.tstudioz.fax.fme.models.util.SPKey
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.fragments.HomeFragment
import com.tstudioz.fax.fme.view.fragments.IksicaFragment
import com.tstudioz.fax.fme.view.fragments.PrisutnostFragment
import com.tstudioz.fax.fme.feature.timetable.view.TimeTableFragment
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import nl.joery.animatedbottombar.AnimatedBottomBar
import nl.joery.animatedbottombar.AnimatedBottomBar.Tab
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate


@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private val dbManager: DatabaseManagerInterface by inject()
    private val shPref: SharedPreferences by inject()


    var date: String? = null

    private var realmLog: Realm? = null
    private var client: OkHttpClient? = null
    private var snack: Snackbar? = null
    private var bottomSheet: BottomSheetDialog? = null
    private val homeFragment = HomeFragment()
    private val timeTableFragment = TimeTableFragment()
    private val prisutnostFragment = PrisutnostFragment()
    private var editor: SharedPreferences.Editor? = null
    private var binding: ActivityMainBinding? = null
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        onBack()
        setUpToolbar()
        setFragmentTabListener()
        testBottomBar()
        checkUser()

        isThereAction()

        checkVersion()
        shouldShowGDPRDialog()
    }

    private fun isThereAction() {
        if (intent.action != null) {
            showShortcutView()
        } else {
            setDefaultScreen()
        }
    }

    private fun onBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setDefaultScreen() {
        beginFragTransaction(R.id.tab_home)
    }

    private fun checkUser() {
        var korisnik: Korisnik? = null
        realmLog = Realm.open(dbManager.getDefaultConfiguration())

        try {
            korisnik = realmLog?.query<Korisnik>()?.find()?.first()
        } catch (ex: Exception) {
            ex.printStackTrace()
            invalidCreds()
        } finally {
            if (korisnik != null) {
                editor = shPref.edit()
                editor?.putString("username", korisnik.username)
                editor?.putString("password", korisnik.password)
                editor?.commit()
                mojRaspored()
            } else {
                invalidCreds()
            }
            realmLog?.close()
        }
    }

    @SuppressLint("RestrictedApi")
    fun setUpToolbar() {
        val actionbar = supportActionBar
        actionbar?.setShowHideAnimationEnabled(false)
        actionbar?.setDisplayShowHomeEnabled(false)
        actionbar?.elevation = 1.0f
    }

    private fun testBottomBar() {
        val bar = binding?.bottomBar

        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.attend),
                "Prisutnost",
                R.id.tab_prisutnost
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.command_line),
                "Home",
                R.id.tab_home
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.cal),
                "Raspored",
                R.id.tab_raspored
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.cal),
                "Iksica",
                R.id.tab_iksica
            )
        )
        bar?.selectTabById(R.id.tab_home, true)
    }

    private fun setFragmentTabListener() {
        binding?.bottomBar?.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(lastIndex: Int, lastTab: Tab?, newIndex: Int, newTab: Tab) {
                beginFragTransaction(newTab.id)
            }

            override fun onTabReselected(index: Int, tab: Tab) {
                if (tab.id == R.id.tab_raspored) {
                    mainViewModel.showWeekChooseMenu()
                }
            }
        })
    }

    fun beginFragTransaction(pos: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
        when (pos) {
            R.id.tab_prisutnost -> {
                supportActionBar?.title = "Prisutnost"
                ft.replace(R.id.frame, prisutnostFragment)
            }

            R.id.tab_home -> {
                supportActionBar?.title = "FESB Companion"
                ft.replace(R.id.frame, homeFragment)
            }

            R.id.tab_raspored -> {
                supportActionBar?.title = "Raspored"
                ft.replace(R.id.frame, timeTableFragment)
            }

            R.id.tab_iksica -> {
                supportActionBar?.title = "Iksica"
                ft.replace(R.id.frame, IksicaFragment())
            }
        }
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.chooseSchedule -> {
                mainViewModel.showWeekChooseMenu()
            }

            R.id.refreshTimetable -> if (NetworkUtils.isNetworkAvailable(this)) {
                mojRaspored()
            } else {
                showSnacOffline()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun mojRaspored() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            val user = shPref.getString("username", "")?.let { User(it, "") }

            val now = LocalDate.now().plusDays(1)
            val start = now.dayOfWeek.value
            val startDate = now.plusDays((1 - start).toLong())
            val endDate = now.plusDays(7 - start.toLong())

            if (user != null) {
                mainViewModel.fetchUserTimetableCurrentWeekAndSave(user, startDate, endDate, startDate)
            }
        } else {
            showSnacOffline()
        }

    }

    private fun invalidCreds() {
        shPref[SPKey.LOGGED_IN] = false
        Toast.makeText(this, "Potrebna je prijava!", Toast.LENGTH_SHORT).show()

        realmLog = Realm.open(dbManager.getDefaultConfiguration())
        try {
            realmLog?.writeBlocking { this.deleteAll() }
        } catch (ex: RealmException) {
            Log.e("MainActivity", ex.toString())
        } finally {
            realmLog?.close()
        }
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    private fun showSnacOffline() {
        snack = Snackbar.make(
            findViewById(R.id.coordinatorLayout), "Niste povezani",
            Snackbar.LENGTH_LONG
        )
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red_nice))
        snack?.show()
    }

    private fun showShortcutView() {
        var shortPosition = 0

        if (intent.action == "podsjetnik") {
            val newIntent = Intent(this@MainActivity, NoteActivity::class.java)
            newIntent.putExtra("mode", 2)
            newIntent.putExtra("note_key", "")
            startActivity(newIntent)
        } else {
            when (intent.action) {
                "raspored" -> shortPosition = 1
                "prisutnost" -> shortPosition = 2
            }
            beginFragTransaction(shortPosition)
        }
    }

    private fun checkVersion() {
        val staraVerzija = shPref.getInt("version_number", 14)
        val trenutnaVerzija: Int = BuildConfig.VERSION_CODE

        if ((staraVerzija != null) && (staraVerzija < trenutnaVerzija)) {
            showChangelog()
            editor = shPref.edit()
            editor?.putInt("version_number", trenutnaVerzija)
            editor?.commit()
        }
    }

    private fun showChangelog() {
        val view =
            LayoutInflater.from(this).inflate(R.layout.licence_view, null) as NestedScrollView
        val wv = view.findViewById<View>(R.id.webvju) as WebView

        wv.loadUrl("file:///android_asset/changelog.html")

        bottomSheet = BottomSheetDialog(this)
        bottomSheet?.setCancelable(true)
        bottomSheet?.setContentView(view)
        bottomSheet?.setCanceledOnTouchOutside(true)
        bottomSheet?.show()
    }

    private fun shouldShowGDPRDialog() {
        val bool = shPref.getBoolean("GDPR_agreed", false)
        if (bool == false) {
            showGDPRCompliance()
            editor = shPref.edit()
            editor?.putBoolean("GDPR_agreed", true)
            editor?.commit()
        }
    }

    private fun showGDPRCompliance() {
        val view = LayoutInflater.from(this).inflate(R.layout.gdpr_layout, null) as ConstraintLayout
        val heading = view.findViewById<View>(R.id.terms_heading) as TextView
        val desc = view.findViewById<View>(R.id.terms_text) as TextView
        val typeBold = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold.ttf")
        val typeRegular = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular.ttf")
        val more = view.findViewById<View>(R.id.button_more) as TextView
        val ok = view.findViewById<View>(R.id.button_ok) as TextView

        heading.typeface = typeBold
        desc.typeface = typeRegular
        more.typeface = typeBold
        ok.typeface = typeBold

        more.setOnClickListener { view ->
            try {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent =
                    builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                        .build()
                customTabsIntent.launchUrl(
                    view.context, Uri.parse(
                        "http://tstud" + ".io/privacy"
                    )
                )
            } catch (ex: Exception) {
                Toast.makeText(
                    view.context,
                    "Ažurirajte Chrome preglednik za pregled " + "web stranice",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        ok.setOnClickListener { bottomSheet?.dismiss() }
        bottomSheet = BottomSheetDialog(this)
        bottomSheet?.setCancelable(false)
        bottomSheet?.setContentView(view)
        bottomSheet?.setCanceledOnTouchOutside(false)
        bottomSheet?.show()
    }

    public override fun onStop() {
        super.onStop()
        if (client != null) {
            client?.dispatcher?.cancelAll()
        }
    }

    public override fun onResume() {
        mojRaspored()
        super.onResume()
    }
}
