package com.tstudioz.fax.fme.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.ActivityMainBinding
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceFragment
import com.tstudioz.fax.fme.feature.home.view.HomeFragment
import com.tstudioz.fax.fme.feature.timetable.view.TimeTableFragment
import com.tstudioz.fax.fme.feature.studomat.view.StudomatFragment
import com.tstudioz.fax.fme.feature.iksica.view.IksicaFragment
import com.tstudioz.fax.fme.feature.settings.SettingsActivity
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import nl.joery.animatedbottombar.AnimatedBottomBar
import nl.joery.animatedbottombar.AnimatedBottomBar.Tab
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class MainActivity : AppCompatActivity() {

    private val timetableViewModel: TimetableViewModel by viewModel()

    private val iksicaFragment = IksicaFragment()
    private val homeFragment = HomeFragment()
    private val timeTableFragment = TimeTableFragment()
    private val attendanceFragment = AttendanceFragment()
    private val studomatFragment = StudomatFragment()

    private var binding: ActivityMainBinding? = null
    private var snack: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        onBack()
        setUpToolbar()
        createBottomBar()
        setTabListener()
        setDefaultScreen()
        isThereAction()
    }

    private fun setDefaultScreen() {
        selectTab(R.id.tab_home)
    }

    @SuppressLint("RestrictedApi")
    fun setUpToolbar() {
        val actionbar = supportActionBar
        actionbar?.setShowHideAnimationEnabled(false)
        actionbar?.setDisplayShowHomeEnabled(false)
        actionbar?.elevation = 1.0f
    }

    private fun createBottomBar() {
        val bar = binding?.bottomBar

        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.iksica),
                getString(R.string.tab_iksica),
                R.id.tab_iksica
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.attend),
                getString(R.string.tab_attendance),
                R.id.tab_prisutnost
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.command_line),
                getString(R.string.tab_home),
                R.id.tab_home
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.cal),
                getString(R.string.tab_timetable),
                R.id.tab_raspored
            )
        )
        bar?.addTab(
            bar.createTab(
                AppCompatResources.getDrawable(this, R.drawable.studomat_icon),
                getString(R.string.tab_studomat),
                R.id.tab_studomat
            )
        )
        bar?.selectTabById(R.id.tab_home, true)
    }

    private fun setTabListener() {
        binding?.bottomBar?.setOnTabSelectListener(object : AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(lastIndex: Int, lastTab: Tab?, newIndex: Int, newTab: Tab) {
                selectTab(newTab.id)
            }

            override fun onTabReselected(index: Int, tab: Tab) {
                if (tab.id == R.id.tab_raspored) {
                    timetableViewModel.showWeekChooseMenu()
                }
            }
        })
    }

    fun selectTab(index: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
        when (index) {
            R.id.tab_iksica -> {
                supportActionBar?.title = getString(R.string.tab_iksica)
                ft.replace(R.id.frame, iksicaFragment)
            }

            R.id.tab_prisutnost -> {
                supportActionBar?.title = getString(R.string.tab_attendance)
                ft.replace(R.id.frame, attendanceFragment)
            }

            R.id.tab_home -> {
                supportActionBar?.title = getString(R.string.app_name)
                ft.replace(R.id.frame, homeFragment)
            }

            R.id.tab_raspored -> {
                supportActionBar?.title = getString(R.string.tab_timetable)
                ft.replace(R.id.frame, timeTableFragment)
            }

            R.id.tab_studomat -> {
                supportActionBar?.title = getString(R.string.tab_studomat)
                ft.replace(R.id.frame, studomatFragment)
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
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.chooseSchedule -> timetableViewModel.showWeekChooseMenu()
            R.id.refreshTimetable -> timetableViewModel.fetchUserTimetable()
        }

        return true
    }

    private fun isThereAction() {
        intent.action?.let {
            showShortcutView()
        }
    }

    private fun onBack() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun showSnackOffline() {
        snack = Snackbar.make(
            findViewById(R.id.coordinatorLayout), "Niste povezani",
            Snackbar.LENGTH_LONG
        )
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.red_nice))
        snack?.show()
    }

    private fun showShortcutView() {
        when (intent.action) {
            LaunchAction.TIMETABLE.name -> selectTab(1)
            LaunchAction.ATTENDANCE.name -> selectTab(2)
        }
    }

}
