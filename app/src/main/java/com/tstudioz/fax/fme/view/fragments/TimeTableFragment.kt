package com.tstudioz.fax.fme.view.fragments

import android.content.ContentValues
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.databinding.TimetableTabBinding
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.adapters.PredavanjaRaspAdapterTable
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.OkHttpClient
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class TimeTableFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    var tempRealm = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("temporary.realm")
        .schemaVersion(12)
        .deleteRealmIfMigrationNeeded()
        .build()
    val mainRealmConfig = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("glavni.realm")
        .schemaVersion(3)
        .deleteRealmIfMigrationNeeded()
        .build()
    var rlm: Realm? = null
    var realm: Realm? = null
    var trealm: Realm? = null
    private var snack: Snackbar? = null
    private var adapteriTemp:MutableList<PredavanjaRaspAdapterTable?> = mutableListOf()
    private val numberOfPredavanjaPerDay :MutableList<Int> = mutableListOf()
    private var client: OkHttpClient? = null
    private var bold: Typeface? = null
    private var binding: TimetableTabBinding? = null
    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private lateinit var mainViewModel: MainViewModel

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        //set the layout you want to display in First Fragment
        binding = TimetableTabBinding.inflate(inflater, container, false)
        mainViewModel = MainViewModel()

        requireActivity().runOnUiThread {
            showDay("Ponedjeljak")
            showDay("Utorak")
            showDay("Srijeda")
            showDay("četvrtak")
            showDay("Petak")
            showDay("Subota")
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

        val sday: DateFormat = SimpleDateFormat("dd")
        val smonth: DateFormat = SimpleDateFormat("MM")
        val syear: DateFormat = SimpleDateFormat("yyyy")

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
        binding?.odaberiDan?.text = "Raspored za $mDay.$mMonth - $sDay.$sMonth"
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun mojRaspored(
        cal: Calendar, mMonth: String, mDay: String,
        mYear: String, sMonth: String, sDay: String, sYear: String) {
        requireActivity().runOnUiThread {
            binding?.linearParent?.visibility = View.INVISIBLE
            binding?.rasporedProgress?.visibility = View.VISIBLE
            adapteriTemp.clear()
            showDayTemp("Ponedjeljak")
            showDayTemp("Utorak")
            showDayTemp("Srijeda")
            showDayTemp("četvrtak")
            showDayTemp("Petak")
            showDayTemp("Subota")
        }
        rlm = Realm.getDefaultInstance()
        val kor = rlm?.where(Korisnik::class.java)?.findFirst()
        client = instance?.okHttpInstance
        val mindate = "$mMonth%2F$mDay%2F$mYear"
        val maxdate = "$sMonth%2F$sDay%2F$sYear"
        mainViewModel.deleteTempTimeTable()

        mainViewModel.fetchUserTimetableTemp(User(kor?.getUsername().toString(),"",""), mindate, maxdate)

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
        val format: DateFormat = SimpleDateFormat("d")

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

    private fun showDay(day: String) {
        realm = Realm.getInstance(mainRealmConfig)
        val rezulatiDay = realm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", day, Case.INSENSITIVE)?.findAll()
        val adapter = rezulatiDay?.let { PredavanjaRaspAdapterTable(it) }
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
            bindDay?.setHasFixedSize(true)
            bindDay?.adapter = adapter
        }
        if (day != "Subota" ){
            bindDay?.layoutManager = LinearLayoutManager(activity)
            bindDay?.setHasFixedSize(true)
            bindDay?.adapter = adapter
        }
    }

    private fun showDayTemp(day: String) {
        trealm = Realm.getInstance(tempRealm)
        val rezulatiDay = trealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", day, Case.INSENSITIVE)?.findAll()
        val adapter = rezulatiDay?.let { PredavanjaRaspAdapterTable(it) }
        adapteriTemp.add(adapter)
        if (adapter != null) {
            numberOfPredavanjaPerDay.add(adapter.itemCount)
        }
        val bindDay = when (day){
            "Ponedjeljak" -> binding?.recyclerPon
            "Utorak" -> binding?.recyclerUto
            "Srijeda" -> binding?.recyclerSri
            "četvrtak" -> binding?.recyclerCet
            "Petak" -> binding?.recyclerPet
            "Subota" -> binding?.recyclerSub
            else -> binding?.recyclerPet
        }
        if (rezulatiDay?.isEmpty() == true && day == "Subota") {
            binding?.linearSub?.visibility = View.GONE
            binding?.linearParent?.weightSum = 5f
        } else if (day == "Subota"){
            binding?.linearSub?.visibility = View.VISIBLE
            binding?.linearParent?.weightSum = 6f
            bindDay?.layoutManager = LinearLayoutManager(activity)
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
        if (client != null) {
            client?.dispatcher?.cancelAll()
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
        if (trealm != null) {
            trealm?.close()
        }
    }
}