package com.tstudioz.fax.fme.view.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.view.activities.MenzaActivity
import com.tstudioz.fax.fme.view.adapters.HomePredavanjaAdapter
import com.tstudioz.fax.fme.view.adapters.NoteAdapter
import com.tstudioz.fax.fme.database.LeanTask
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.databinding.HomeTabBinding
import com.tstudioz.fax.fme.viewmodel.HomeViewModel
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.activities.IndexActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONException
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

@OptIn(InternalCoroutinesApi::class)
class HomeFragment : Fragment() {
    private var binding: HomeTabBinding? = null
    private val forecastUrl = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=$mLatitude&lon=$mLongitude"
    private lateinit var homeViewModel: HomeViewModel
    private var mrealm: Realm? = null
    private var taskRealm: Realm? = null
    private var date: String? = null
    private var snack: Snackbar? = null

    private var realmTaskConfiguration: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("tasks.realm")
        .deleteRealmIfMigrationNeeded()
        .schemaVersion(1)
        .build()
    private val mainRealmConfig: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("glavni.realm")
        .schemaVersion(3)
        .deleteRealmIfMigrationNeeded()
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): CoordinatorLayout?
    {
        super.onCreateView(inflater, container, savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding = HomeTabBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        taskRealm = Realm.getInstance(realmTaskConfiguration)
        setHasOptionsMenu(true)
        setCyanStatusBarColor()
        getDate()
        start()
        loadNotes()
        loadIksicaAd()
        loadMenzaView()
        showList()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        setCyanStatusBarColor()
        showList()
    }

    private fun getDate() {
        val df: DateFormat = SimpleDateFormat("d.M.yyyy.")
        date = df.format(Calendar.getInstance().time)
    }

    @Throws(IOException::class, JSONException::class)
    private fun start() {
        try {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
            lifecycleScope.launch { homeViewModel.getForecast(forecastUrl) }
            homeViewModel.forecastGot.observe(viewLifecycleOwner) { forecastGot ->
                if (forecastGot) {
                    activity?.runOnUiThread { updateDisplay() }
                } else {
                    alertUserAboutError()
                }
            }
        } else {
            showSnacOffline()
        }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updateDisplay() {
        val current = homeViewModel.mForecast?.current
        val pTemperatura = current?.temperature.toString() + "°"
        val pHumidity = current?.humidity.toString() + " %"
        val pWind = current?.wind.toString() + " km/h"
        val pPrecip = current?.precipChance.toString() + " mm"
        val pSummary = getString(resources.getIdentifier(current?.summary , "string", requireContext().packageName))
        binding?.temperaturaVrijednost?.text = pTemperatura
        binding?.vlaznostVrijednost?.text = pHumidity
        binding?.oborineVrijednost?.text = pPrecip
        binding?.trenutniVjetar?.text = pWind
        binding?.opis?.text = pSummary
        binding?.shimmerWeather?.visibility = View.GONE
        binding?.cardHome?.visibility = View.VISIBLE
        val drawable = ResourcesCompat.getDrawable(resources, resources.getIdentifier(current?.icon, "drawable", requireContext().packageName),null)

        binding?.vrijemeImage?.setImageDrawable(drawable)
    }

    fun showList() {
        mrealm = Realm.getInstance(mainRealmConfig)
        val rezultati =
            date?.let {mrealm?.where(Predavanja::class.java)?.contains("detaljnoVrijeme", it)?.findAll()}
        if (rezultati !=null && rezultati.isEmpty()) {
            binding?.rv?.visibility = View.INVISIBLE
            binding?.nemaPredavanja?.visibility = View.VISIBLE
        } else {
            binding?.nemaPredavanja?.visibility = View.GONE
            binding?.rv?.visibility = View.VISIBLE
            val adapter = rezultati?.let { HomePredavanjaAdapter(it) }
            binding?.rv?.adapter = adapter
            binding?.rv?.layoutManager = LinearLayoutManager(activity)
            binding?.rv?.let { ViewCompat.setNestedScrollingEnabled(it, false) }
        }
    }

    private fun setCyanStatusBarColor() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(requireContext(), R.color.dark_cyan)
                ))
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.darker_cyan)
    }

    private fun loadNotes() {
        val tasks = taskRealm?.where(LeanTask::class.java)?.findAll()
        val dodajNovi = LeanTask()
        dodajNovi.setId("ACTION_ADD")
        dodajNovi.setTaskTekst("Dodaj novi podsjetnik")
        taskRealm?.executeTransaction { realm -> realm.insertOrUpdate(dodajNovi) }
        val noteAdapter = tasks?.let { NoteAdapter(it) }
        binding?.recyclerTask?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerTask?.let { ViewCompat.setNestedScrollingEnabled(it, false) }
        binding?.recyclerTask?.adapter = noteAdapter
    }

    private fun loadIksicaAd() {
        binding?.iksicaAd?.setOnClickListener {
            val appPackageName = "com.tstud.iksica"
            try {
                val intent = requireActivity().packageManager.getLaunchIntentForPackage(appPackageName)
                startActivity(intent)
            } catch (anfe: Exception) {
                try {
                    startActivity(
                        Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=$appPackageName")))
                } catch (ex: ActivityNotFoundException) {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
                    )
                }
            }
        }
    }

    private fun loadMenzaView() {
        binding?.menzaRelative?.setOnClickListener {
            startActivity(Intent(activity, MenzaActivity::class.java))
        }
        binding?.eindexRelative?.setOnClickListener {
            startActivity(Intent(activity, IndexActivity::class.java))
        }
    }

    private fun showSnacOffline() {
        snack = Snackbar.make(requireActivity().findViewById(R.id.coordinatorLayout), "Niste povezani", Snackbar.LENGTH_LONG)
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.show()
    }

    private fun alertUserAboutError() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout),
            "Došlo je do pogreške pri dohvaćanju prognoze",
            Snackbar.LENGTH_LONG
        )
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.show()
    }

    override fun onStop() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            )
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        if (mrealm != null) {
            mrealm?.close()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (taskRealm != null) {
            taskRealm?.close()
        }
        super.onDestroy()
    }

    companion object {
        private const val mLatitude = 43.511287
        private const val mLongitude = 16.469252
    }
}