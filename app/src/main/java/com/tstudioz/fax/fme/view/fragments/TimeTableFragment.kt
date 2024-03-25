package com.tstudioz.fax.fme.view.fragments

import android.content.ContentValues
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog
import com.tstudioz.fax.fme.Application.FESBCompanion
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Korisnik
import com.tstudioz.fax.fme.database.models.Predavanja
import com.tstudioz.fax.fme.databinding.TimetableTabBinding
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.adapters.PredavanjaRaspAdapterTable
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeTableFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val dbManager: DatabaseManagerInterface by inject()
    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val mainViewModel: MainViewModel by inject()

    private var rlm: Realm? = null
    var realm: Realm? = null
    private var snack: Snackbar? = null
    private var adapteriTemp:MutableList<PredavanjaRaspAdapterTable?> = mutableListOf()
    private val numberOfPredavanjaPerDay :MutableList<Int> = mutableListOf()
    private var bold: Typeface? = null
    private var binding: TimetableTabBinding? = null
    @OptIn(InternalCoroutinesApi::class)
    private var shPref: SharedPreferences? =  FESBCompanion.instance?.sP

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        binding = TimetableTabBinding.inflate(inflater, container, false)

        requireActivity().runOnUiThread {
            showDay("Ponedjeljak",false)
            showDay("Utorak", false)
            showDay("Srijeda",false)
            showDay("četvrtak", false)
            showDay("Petak", false)
            showDay("Subota", false)
        }
        val min = Calendar.getInstance()
        val now = Calendar.getInstance()
        val max = Calendar.getInstance()
        max.add(Calendar.YEAR, 10)
        min.add(Calendar.YEAR, -1)
        val builder: BottomSheetPickerDialog.Builder = DatePickerDialog.Builder(
            this@TimeTableFragment,
            now[Calendar.YEAR], now[Calendar.MONTH], now[Calendar.DAY_OF_MONTH])
        val dateDialogBuilder = builder as DatePickerDialog.Builder
        dateDialogBuilder.setMaxDate(max)
            .setMinDate(min)
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setThemeDark(true)
            .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            .setHeaderColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        checkNetwork()
        binding?.odaberiDan?.setOnClickListener {
            builder.build().show(requireFragmentManager(), ContentValues.TAG)
        }
        setSetDates(now)
        boldOut()

        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onDateSet(dialog: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val kal = Calendar.getInstance()

        kal[Calendar.YEAR] = year
        kal[Calendar.MONTH] = monthOfYear
        kal[Calendar.DAY_OF_MONTH] = dayOfMonth
        kal.add(Calendar.DAY_OF_MONTH,-1)

        val sday: DateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val smonth: DateFormat = SimpleDateFormat("MM", Locale.getDefault())
        val syear: DateFormat = SimpleDateFormat("yyyy", Locale.getDefault())

        kal[Calendar.DAY_OF_WEEK]
        kal.add(Calendar.DAY_OF_MONTH, -(kal[Calendar.DAY_OF_WEEK] - Calendar.MONDAY))

        val mMonth = smonth.format(kal.time)
        val mDay = sday.format(kal.time)
        val mYear = syear.format(kal.time)

        kal.add(Calendar.DAY_OF_MONTH, 5)

        val sMonth = smonth.format(kal.time)
        val sDay = sday.format(kal.time)
        val sYear = syear.format(kal.time)

        mojRaspored(kal, mMonth, mDay, mYear, sMonth, sDay, sYear)
        val textdisp ="Raspored za $mDay.$mMonth - $sDay.$sMonth"
        binding?.odaberiDan?.text = textdisp
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun mojRaspored(
        cal: Calendar, mMonth: String, mDay: String,
        mYear: String, sMonth: String, sDay: String, sYear: String) {
        requireActivity().runOnUiThread {
            binding?.linearParent?.visibility = View.INVISIBLE
            binding?.rasporedProgress?.visibility = View.VISIBLE
            adapteriTemp.clear()
            showDay("Ponedjeljak", true)
            showDay("Utorak", true)
            showDay("Srijeda", true)
            showDay("četvrtak", true)
            showDay("Petak", true)
            showDay("Subota", true)
        }
        rlm = Realm.open(dbManager.getDefaultConfiguration())
        val user = shPref?.getString("username", "")?.let { User(it, "", "") }
        val mindate = "$mMonth%2F$mDay%2F$mYear"
        val maxdate = "$sMonth%2F$sDay%2F$sYear"

        mainViewModel.fetchUserTimetableTemp(User(user?.username.toString(),"",""), mindate, maxdate)

        mainViewModel.tableGot.observe(viewLifecycleOwner) { tableGot ->
            if (tableGot) {
                activity?.runOnUiThread {
                    updateTemporaryWeek(cal)
                }
            } else {
                activity?.runOnUiThread { showSnackError() }
            }
        }
    }
    private fun setSetDates(calendar: Calendar) {
        val format: DateFormat = SimpleDateFormat("d", Locale.getDefault())

        val daysOfWeek = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

        for (dayOfWeek in daysOfWeek) {
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            val date = format.format(calendar.time)

            when (dayOfWeek) {
                Calendar.MONDAY -> binding?.ponDate?.text = date
                Calendar.TUESDAY -> binding?.utoDate?.text = date
                Calendar.WEDNESDAY -> binding?.sriDate?.text = date
                Calendar.THURSDAY -> binding?.cetDate?.text = date
                Calendar.FRIDAY -> binding?.petDate?.text = date
                Calendar.SATURDAY -> binding?.subDate?.text = date
            }
        }
    }

    private fun boldOut() {
        bold = Typeface.createFromAsset(requireContext().assets, "fonts/OpenSans-Bold.ttf")
        binding?.mPon?.typeface = bold
        binding?.mUto?.typeface = bold
        binding?.mSri?.typeface = bold
        binding?.mCet?.typeface = bold
        binding?.mPet?.typeface = bold
        binding?.mSub?.typeface = bold
    }

    private fun showDay(day: String, isTemp: Boolean) {
        realm =
            if (!isTemp) {
            Realm.open(dbManager.getDefaultConfiguration())
        } else {
            Realm.open(dbManager.getDefaultConfiguration())
        }
        val rezulatiDay = realm?.query<Predavanja>("detaljnoVrijeme TEXT $0", "$day*")?.find()
        val adapter = rezulatiDay?.let { PredavanjaRaspAdapterTable(it) }
        if (adapter != null && isTemp){
            adapteriTemp.add(adapter)
            numberOfPredavanjaPerDay.add(adapter.itemCount)
        }
        val bindDay = when (day){
            "Ponedjeljak" -> binding?.recyclerPon
            "Utorak" -> binding?.recyclerUto
            "Srijeda" -> binding?.recyclerSri
            "četvrtak" -> binding?.recyclerCet
            "Petak" -> binding?.recyclerPet
            "Subota" -> binding?.recyclerSub //needs testing to show that sub properly shows
            else -> binding?.recyclerPet
        }
        if (rezulatiDay?.isEmpty() == true && day == "Subota") {
            binding?.linearSub?.visibility = View.GONE
            binding?.linearParent?.weightSum = 5f
        } else if (day == "Subota"){
            binding?.linearSub?.visibility = View.VISIBLE
            binding?.linearParent?.weightSum = 6f
            bindDay?.layoutManager = LinearLayoutManager(activity)
            if (!isTemp){
                bindDay?.setHasFixedSize(true)
            }
            bindDay?.adapter = adapter
        }
        if (day != "Subota" ){
            bindDay?.layoutManager = LinearLayoutManager(activity)
            bindDay?.setHasFixedSize(true)
            bindDay?.adapter = adapter
        }
    }

    private fun updateTemporaryWeek(cal: Calendar) {
        for (adapter in this.adapteriTemp){
            adapter?.notifyDataSetChanged()
            adapter?.notifyItemChanged(0)
        }
        setSetDates(cal)
        if (numberOfPredavanjaPerDay[5] != 0) {
            binding?.linearParent?.weightSum = 6f
            binding?.linearSub?.visibility = View.VISIBLE
            binding?.linearParent?.invalidate()
        } else {
            binding?.linearSub?.visibility = View.INVISIBLE
            binding?.linearParent?.weightSum = 5f
            binding?.linearParent?.invalidate()
        }
        binding?.rasporedProgress?.visibility = View.INVISIBLE
        binding?.linearParent?.visibility = View.VISIBLE
    }
    private fun checkNetwork() {
        if (context?.let { NetworkUtils.isNetworkAvailable(it) } == true) {
            binding?.odaberiDan?.visibility = View.VISIBLE
        } else {
            binding?.odaberiDan?.visibility = View.INVISIBLE
            showSnacOffline()
        }
    }

    private fun showSnacOffline() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), """
     Niste povezani.
     Prikazuje se raspored ovog tjedna.
     """.trimIndent(), Snackbar.LENGTH_INDEFINITE
        )
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.setAction("OSVJEŽI") {
            snack?.dismiss()
            checkNetwork()
        }
        snack?.setActionTextColor(resources.getColor(R.color.white))
        snack?.show()
    }

    private fun showSnackError() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), "Došlo je do " +
                    "pogreške pri dohvaćanju rasporeda", Snackbar.LENGTH_SHORT
        )
        val vjuzs = snack?.view
        vjuzs?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.show()
    }

    override fun onStop() {
        super.onStop()
        if (snack != null) {
            snack?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rlm != null) {
            rlm?.close()
        }
        if (realm != null) {
            realm?.close()
        }
    }
}