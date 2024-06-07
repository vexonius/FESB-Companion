package com.tstudioz.fax.fme.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.databinding.HomeTabBinding
import com.tstudioz.fax.fme.models.util.PreferenceHelper.get
import com.tstudioz.fax.fme.models.util.SPKey
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.activities.MainActivity
import com.tstudioz.fax.fme.feature.menza.view.MenzaActivity
import com.tstudioz.fax.fme.view.adapters.HomePredavanjaAdapter
import com.tstudioz.fax.fme.view.adapters.NoteAdapter
import com.tstudioz.fax.fme.viewmodel.HomeViewModel
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.json.JSONException
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.time.LocalDate
import java.util.Locale

@OptIn(InternalCoroutinesApi::class)
class HomeFragment : Fragment() {

    private val dbManager: DatabaseManagerInterface by inject()
    private val shPref: SharedPreferences by inject()

    private var binding: HomeTabBinding? = null
    private val forecastUrl =
        "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=$mLatitude&lon=$mLongitude"
    private val homeViewModel: HomeViewModel by viewModel()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mainViewModel: MainViewModel by activityViewModel()
    private var snack: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CoordinatorLayout? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = HomeTabBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        setCyanStatusBarColor()
        setLastFetched()
        fetchForcastAndRegisterListener()
        loadNotes()
        loadIksicaAd()
        loadMenzaView()
        showEventList()
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        setCyanStatusBarColor()
        (activity as MainActivity?)?.mojRaspored()
    }

    private fun setLastFetched() {
        binding?.TimeRaspGot?.text = shPref[SPKey.LAST_FETCHED, ""]
        binding?.TimeRaspGot?.visibility = View.VISIBLE
    }

    @Throws(IOException::class, JSONException::class)
    private fun fetchForcastAndRegisterListener() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            try {
                lifecycleScope.launch { homeViewModel.getForecast(forecastUrl) }
                homeViewModel.forecastGot.observe(viewLifecycleOwner) { forecastGot ->
                    if (forecastGot) {
                        activity?.runOnUiThread { updateWeatherDisplay() }
                    } else {
                        showSnac("Došlo je do pogreške pri dohvaćanju prognoze")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            showSnac("Niste povezani")
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun updateWeatherDisplay() {
        try {
            binding?.temperaturaVrijednost?.text = String.format(Locale.US, getString(R.string.weatherTemp), homeViewModel.temperature.value)
            binding?.vlaznostVrijednost?.text = String.format(Locale.US, getString(R.string.weatherHumidity), homeViewModel.humidity.value)
            binding?.oborineVrijednost?.text = String.format(Locale.US, getString(R.string.weatherPrecipChance), homeViewModel.precipChance.value)
            binding?.trenutniVjetar?.text = String.format(Locale.US, getString(R.string.weatherWind), homeViewModel.wind.value)
            binding?.opis?.text = homeViewModel.summary.value
            binding?.shimmerWeather?.visibility = View.GONE
            binding?.cardHome?.visibility = View.VISIBLE
            binding?.vrijemeImage?.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    resources.getIdentifier(homeViewModel.icon.value, "drawable", requireContext().packageName),
                    null
                )
            )
        } catch (e: Exception) {
            showSnac("Došlo je do pogreške pri dohvaćanju prognoze")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun showEventList() {
        mainViewModel.lessonsPerm.observe(viewLifecycleOwner) { lessons ->
            val filteredLessons = lessons.filter { it.start.toLocalDate() == LocalDate.now() }
            binding?.TimeRaspGot?.text = shPref[SPKey.LAST_FETCHED, ""]
            if (filteredLessons.isEmpty()) {
                binding?.rv?.visibility = View.INVISIBLE
                binding?.nemaPredavanja?.visibility = View.VISIBLE
            } else {
                binding?.nemaPredavanja?.visibility = View.GONE
                binding?.rv?.visibility = View.VISIBLE
                val adapter = HomePredavanjaAdapter(filteredLessons)
                binding?.rv?.adapter = adapter
                binding?.rv?.layoutManager = LinearLayoutManager(activity)
                binding?.rv?.let { ViewCompat.setNestedScrollingEnabled(it, false) }
            }
        }
    }

    private fun setCyanStatusBarColor() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(requireContext(), R.color.dark_cyan)
                )
            )
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.darker_cyan)
    }

    private fun loadNotes() {
        val realm = Realm.open(dbManager.getDefaultConfiguration())
        val notes = realm.query<Note>().find()
        val addNew = Note()
        addNew.id = "ACTION_ADD"
        addNew.noteTekst = "Dodaj novi podsjetnik"
        realm.writeBlocking { this.copyToRealm(addNew, updatePolicy = UpdatePolicy.ALL) }
        binding?.recyclerNote?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerNote?.let { ViewCompat.setNestedScrollingEnabled(it, false) }

        CoroutineScope(Dispatchers.Default).launch {
            notes.asFlow().collect { changes ->
                activity?.runOnUiThread {
                    val noteAdapter = NoteAdapter(changes.list)
                    binding?.recyclerNote?.adapter = noteAdapter
                }
            }
        }
    }

    private fun loadIksicaAd() {
        /*binding?.iksicaAd?.setOnClickListener {
            val appPackageName = "com.tstud.iksica"
            try {
                val intent =
                    requireActivity().packageManager.getLaunchIntentForPackage(appPackageName)
                if (intent != null) {
                    startActivity(intent)
                }

            } catch (anfe: Exception) {
                try {
                    startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
                    )
                } catch (ex: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }
        }*/
    }

    private fun loadMenzaView() {
        binding?.menzaRelative?.setOnClickListener {
            startActivity(Intent(activity, MenzaActivity::class.java))
        }
    }

    private fun showSnac(text: String) {
        snack = Snackbar.make(requireActivity().findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
        snack?.view?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.show()
    }

    override fun onStop() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary)))
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        super.onStop()
    }

    companion object {
        private const val mLatitude = 43.511287
        private const val mLongitude = 16.469252
    }
}