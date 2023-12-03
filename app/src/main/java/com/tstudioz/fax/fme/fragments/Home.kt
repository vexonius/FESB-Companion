package com.tstudioz.fax.fme.fragments

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.activities.IndexActivity
import com.tstudioz.fax.fme.activities.MenzaActivity
import com.tstudioz.fax.fme.adapters.EmployeeRVAdapter
import com.tstudioz.fax.fme.adapters.LeanTaskAdapter
import com.tstudioz.fax.fme.database.LeanTask
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.databinding.HomeTabBinding
import com.tstudioz.fax.fme.networking.NetworkUtils
import com.tstudioz.fax.fme.weather.Current
import com.tstudioz.fax.fme.weather.Forecast
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class Home : Fragment() {
    private var date: String? = null
    private var units: String? = null
    private var mForecast: Forecast? = null
    private var snack: Snackbar? = null
    private var mrealm: Realm? = null
    private var taskRealm: Realm? = null
    private var binding: HomeTabBinding? = null
    private val forecastUrl = "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=$mLatitude&lon=$mLongitude"

    var realmTaskConfiguration: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("tasks.realm")
        .deleteRealmIfMigrationNeeded()
        .schemaVersion(1)
        .build()
    val mainRealmConfig: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("glavni.realm")
        .schemaVersion(3)
        .deleteRealmIfMigrationNeeded()
        .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CoordinatorLayout? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding = HomeTabBinding.inflate(inflater, container, false)

        //getActivity().setActionBar(binding.customToolbar);
        setHasOptionsMenu(true)
        setCyanStatusBarColor()
        getDate()
        try {
            start()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        taskRealm = Realm.getInstance(realmTaskConfiguration)
        loadNotes()
        loadIksicaAd()
        loadMenzaView()
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

    @OptIn(InternalCoroutinesApi::class)
    private fun getForecast(url: String) {
        // OkHttp stuff
        val client = instance?.okHttpInstance
        val request: Request = Builder()
            .url(url).header("Accept", "application/xml").header("User-Agent", "FesbCompanion/1.0")
            .build()
        val call = client?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(ContentValues.TAG, "Exception caught", e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    val jsonData = response.body?.string()
                    if (response.isSuccessful) {
                        mForecast = jsonData?.let { parseForecastDetails(it) }
                        activity?.runOnUiThread { updateDisplay() }
                    } else {
                        alertUserAboutError()
                    }
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Exception caught: ", e)
                } catch (e: JSONException) {
                    Log.e(ContentValues.TAG, "Exception caught: ", e)
                }
            }
        })
    }

    @Throws(IOException::class, JSONException::class)
    private fun start() {
        val shared = requireActivity().getSharedPreferences("PRIVATE_PREFS", Context.MODE_PRIVATE)
        units = shared.getString("weather_units", "&units=ca")

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            getForecast(forecastUrl)
        } else {
            showSnacOffline()
        }
    }

    private fun updateDisplay() {
        val current = mForecast?.current
        val pTemperatura = current?.temperature.toString() + "°"
        val pHumidity = current?.humidity.toString() + " %"
        val pWind = current?.wind.toString() + " km/h"
        val pPrecip = current?.precipChance.toString() + " mm"
        val pSummary = current?.summary
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

    @Throws(JSONException::class)
    private fun parseForecastDetails(jsonData: String): Forecast {
        /*jsonData = jsonData.replaceAll("\\\\\"", "\"");
        jsonData = jsonData.substring(1, jsonData.length()-1);
        Log.d("REGEX OUTPUT", jsonData);*/
        val forecast = Forecast()
        forecast.current = getCurrentDetails(jsonData)
        return forecast
    }

    @Throws(JSONException::class)
    private fun getCurrentDetails(jsonData: String): Current {
        val forecast = JSONObject(jsonData)
        // String timezone = forecast.getString("timezone");
        val currently0 = forecast.getJSONObject("properties")
        val currentlyArray = currently0.getJSONArray("timeseries")
        val currently = currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("instant").getJSONObject("details")
        val currentlyNextOneHours = currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("next_1_hours")
        val currentlyNextOneHoursSummary = currentlyNextOneHours.getJSONObject("summary")
        val currentlyNextOneHoursDetails = currentlyNextOneHours.getJSONObject("details")
        val unparsedsummary = currentlyNextOneHoursSummary.getString("symbol_code")
        val summary: String? = if (unparsedsummary.contains("_"))
        {
            unparsedsummary.substring(0, unparsedsummary.indexOf('_'))
        } else {
            unparsedsummary
        }
        val current = Current()
        current.humidity = currently.getDouble("relative_humidity")
        current.icon = currentlyNextOneHoursSummary.getString("symbol_code")
        current.precipChance = currentlyNextOneHoursDetails.getDouble("precipitation_amount")
        current.summary = getString(resources.getIdentifier(summary, "string", requireContext().packageName))
        current.wind = currently.getDouble("wind_speed")
        current.setTemperature(currently.getDouble("air_temperature"))
        return current
    }

    fun showList() {
        mrealm = Realm.getInstance(mainRealmConfig)
        val rezultati =
            date?.let {mrealm?.where(Predavanja::class.java)?.contains("detaljnoVrijeme", it)?.findAll()}
        if (rezultati !=null && rezultati.isEmpty()) {
            binding?.rv?.visibility = View.INVISIBLE
            binding?.nemaPredavanja?.visibility = View.VISIBLE
        } else {
            binding?.nemaPredavanja?.visibility = View.INVISIBLE
            val adapter = EmployeeRVAdapter(rezultati)
            binding?.rv?.layoutManager = LinearLayoutManager(activity)
            binding?.rv?.let { ViewCompat.setNestedScrollingEnabled(it, false) }
            binding?.rv?.adapter = adapter
            binding?.nemaPredavanja?.visibility = View.GONE
            binding?.rv?.visibility = View.VISIBLE
        }
    }

    private fun setCyanStatusBarColor() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dark_cyan
                    )
                )
            )
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.darker_cyan)
    }

    private fun loadNotes() {
        val tasks = taskRealm?.where(LeanTask::class.java)?.findAll()
        val dodajNovi = LeanTask()
        dodajNovi.setId("ACTION_ADD")
        dodajNovi.setTaskTekst("Dodaj novi podsjetnik")
        taskRealm?.executeTransaction { realm -> realm.insertOrUpdate(dodajNovi) }
        val leanTaskAdapter = LeanTaskAdapter(tasks)
        binding?.recyclerTask?.layoutManager = LinearLayoutManager(activity)
        binding?.recyclerTask?.let { ViewCompat.setNestedScrollingEnabled(it, false) }
        binding?.recyclerTask?.adapter = leanTaskAdapter
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

    fun alertUserAboutError() {
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
        super.onStop()
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            )
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        if (mrealm != null) {
            mrealm?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (taskRealm != null) {
            taskRealm?.close()
        }
    }

    companion object {
        private const val mLatitude = 43.511287
        private const val mLongitude = 16.469252
    }
}