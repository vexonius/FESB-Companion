package com.tstudioz.fax.fme.view.fragments

import android.content.ContentValues
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
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
import com.tstudioz.fax.fme.view.adapters.EmployeeRVAdapterTable
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.database.Predavanja
import com.tstudioz.fax.fme.databinding.TimetableTabBinding
import com.tstudioz.fax.fme.networking.NetworkUtils
import io.realm.Case
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.UUID

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
    var prealm: Realm? = null
    var ptrealm: Realm? = null
    var urealm: Realm? = null
    var utrealm: Realm? = null
    var srealm: Realm? = null
    var strealm: Realm? = null
    var crealm: Realm? = null
    var ctrealm: Realm? = null
    var petrealm: Realm? = null
    var pettrealm: Realm? = null
    var subrealm: Realm? = null
    var subtrealm: Realm? = null
    private var snack: Snackbar? = null
    private var adapterPonTemp: EmployeeRVAdapterTable? = null
    private var adapterUtoTemp: EmployeeRVAdapterTable? = null
    private var adapterSriTemp: EmployeeRVAdapterTable? = null
    private var adapterCetTemp: EmployeeRVAdapterTable? = null
    private var adapterPetTemp: EmployeeRVAdapterTable? = null
    private var adapterSubTemp: EmployeeRVAdapterTable? = null
    private var client: OkHttpClient? = null
    private var bold: Typeface? = null
    private var binding: TimetableTabBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        //set the layout you want to display in First Fragment
        binding = TimetableTabBinding.inflate(inflater, container, false)
        requireActivity().runOnUiThread {
            showPon()
            showUto()
            showSri()
            showCet()
            showPet()
            showSub()
        }
        val min = Calendar.getInstance()
        val now = Calendar.getInstance()
        val max = Calendar.getInstance()
        max.add(Calendar.YEAR, 10)
        min.add(Calendar.YEAR, -1)
        val builder: BottomSheetPickerDialog.Builder = DatePickerDialog.Builder(
            this@TimeTableFragment,
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )
        val dateDialogBuilder = builder as DatePickerDialog.Builder
        dateDialogBuilder.setMaxDate(max)
            .setMinDate(min)
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setThemeDark(true)
            .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            .setHeaderColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        checkNetwork()
        binding!!.odaberiDan.setOnClickListener {
            builder.build().show(requireFragmentManager(), ContentValues.TAG)
        }
        // Get the root frame layout
        /* View rootFrameLayout = binding.getRoot();

        // Get the layout parameters of the root frame layout
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) rootFrameLayout.getLayoutParams();

        // Set bottom margin to 64 pixels
        params.bottomMargin = 64;

        // Apply the modified layout parameters
        rootFrameLayout.setLayoutParams(params);*/
        setSetDates(now)
        boldOut()
        return binding!!.root
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
        binding!!.odaberiDan.text = "Raspored za $mDay.$mMonth - $sDay.$sMonth"
    }

    @OptIn(InternalCoroutinesApi::class)
    fun mojRaspored(
        cal: Calendar, mMonth: String, mDay: String, mYear: String,
        sMonth: String, sDay: String, sYear: String
    ) {
        requireActivity().runOnUiThread {
            binding!!.linearParent.visibility = View.INVISIBLE
            binding!!.rasporedProgress.visibility = View.VISIBLE
            showPonTemp()
            showUtoTemp()
            showSriTemp()
            showCetTemp()
            showPetTemp()
            showSubTemp()
        }
        rlm = Realm.getDefaultInstance()
        val kor = rlm?.where(Korisnik::class.java)?.findFirst()
        client = instance!!.okHttpInstance
        val request: Request = Request.Builder()
            .url(
                "https://raspored.fesb.unist.hr/part/raspored/kalendar?DataType=User&DataId" +
                        "=" + kor!!.getUsername()
                    .toString() + "&MinDate=" + mMonth + "%2F" + mDay + "%2F" + mYear + "%2022%3A44%3A48&MaxDate=" + sMonth + "%2F" + sDay + "%2F" + sYear + "%2022%3A44%3A48"
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
                try {
                    if (response.code == 500) {
                        activity!!.runOnUiThread { showSnackError() }
                    } else {
                        val doc = Jsoup.parse(response.body!!.string())
                        val trealm = Realm.getInstance(tempRealm)
                        trealm.beginTransaction()
                        val svaPredavanja = trealm.where(
                            Predavanja::class.java
                        ).findAll()
                        svaPredavanja.deleteAllFromRealm()
                        trealm.commitTransaction()
                        if (response.isSuccessful) {
                            val elements = doc.select("div.event")
                            try {
                                trealm.beginTransaction()
                                for (e in elements) {
                                    val predavanja = trealm.createObject(
                                        Predavanja::class.java,
                                        UUID.randomUUID().toString()
                                    )
                                    if (e.hasAttr("data-id")) {
                                        val attr = e.attr("data-id")
                                        predavanja.objectId = attr.toInt()
                                    }
                                    predavanja.predavanjeIme = e.select("span.groupCategory").text()
                                    predavanja.predmetPredavanja =
                                        e.select("span.name.normal").text()
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
                                trealm.commitTransaction()
                            } finally {
                                trealm.close()
                            }
                            activity!!.runOnUiThread {
                                updateTemporaryWeek()
                                setSetDates(cal)
                                if (adapterSubTemp!!.itemCount > 0) {
                                    binding!!.linearParent.weightSum = 6f
                                    binding!!.linearSub.visibility = View.VISIBLE
                                    binding!!.linearParent.invalidate()
                                } else {
                                    binding!!.linearSub.visibility = View.INVISIBLE
                                    binding!!.linearParent.weightSum = 5f
                                    binding!!.linearParent.invalidate()
                                }
                                binding!!.rasporedProgress.visibility = View.INVISIBLE
                                binding!!.linearParent.visibility = View.VISIBLE
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Exception caught: ", e)
                }
            }
        })
    }

    fun setSetDates(calendar: Calendar) {
        val format: DateFormat = SimpleDateFormat("d")
        calendar[Calendar.DAY_OF_WEEK]
        calendar.add(
            Calendar.DAY_OF_MONTH,
            -(calendar[Calendar.DAY_OF_WEEK] - Calendar.MONDAY)
        )
        val pon = format.format(calendar.time)
        binding!!.ponDate.text = pon
        calendar.add(
            Calendar.DAY_OF_MONTH,
            -(calendar[Calendar.DAY_OF_WEEK] - Calendar.TUESDAY)
        )
        val uto = format.format(calendar.time)
        binding!!.utoDate.text = uto
        calendar.add(
            Calendar.DAY_OF_MONTH,
            -(calendar[Calendar.DAY_OF_WEEK] - Calendar.WEDNESDAY)
        )
        val sri = format.format(calendar.time)
        binding!!.sriDate.text = sri
        calendar.add(
            Calendar.DAY_OF_MONTH,
            -(calendar[Calendar.DAY_OF_WEEK] - Calendar.THURSDAY)
        )
        val cet = format.format(calendar.time)
        binding!!.cetDate.text = cet
        calendar.add(
            Calendar.DAY_OF_MONTH,
            -(calendar[Calendar.DAY_OF_WEEK] - Calendar.FRIDAY)
        )
        val pet = format.format(calendar.time)
        binding!!.petDate.text = pet
        calendar.add(
            Calendar.DAY_OF_MONTH,
            -(calendar[Calendar.DAY_OF_WEEK] - Calendar.SATURDAY)
        )
        val sub = format.format(calendar.time)
        binding!!.subDate.text = sub
    }

    fun boldOut() {
        bold = Typeface.createFromAsset(requireContext().assets, "fonts/OpenSans-Bold.ttf")
        binding!!.mPon.typeface = bold
        binding!!.mUto.typeface = bold
        binding!!.mSri.typeface = bold
        binding!!.mCet.typeface = bold
        binding!!.mPet.typeface = bold
        binding!!.mSub.typeface = bold
    }

    fun showPon() {
        prealm = Realm.getInstance(mainRealmConfig)
        val rezulatiPon = prealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Ponedjeljak", Case.INSENSITIVE
        )?.findAll()
        val adapter = rezulatiPon?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerPon.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerPon.setHasFixedSize(true)
        binding!!.recyclerPon.adapter = adapter
    }

    fun showPonTemp() {
        ptrealm = Realm.getInstance(tempRealm)
        val rezulatiPon1 = ptrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Ponedjeljak", Case.INSENSITIVE
        )?.findAll()
        adapterPonTemp = rezulatiPon1?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerPon.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerPon.setHasFixedSize(true)
        binding!!.recyclerPon.adapter = adapterPonTemp
    }

    fun showUto() {
        urealm = Realm.getInstance(mainRealmConfig)
        val rezulatiUto = urealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Utorak", Case.INSENSITIVE
        )?.findAll()
        val adapter2 = rezulatiUto?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerUto.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerUto.setHasFixedSize(true)
        binding!!.recyclerUto.adapter = adapter2
    }

    fun showUtoTemp() {
        utrealm = Realm.getInstance(tempRealm)
        val rezulatiUto1 = utrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Utorak", Case.INSENSITIVE
        )?.findAll()
        adapterUtoTemp = rezulatiUto1?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerUto.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerUto.setHasFixedSize(true)
        binding!!.recyclerUto.adapter = adapterUtoTemp
    }

    fun showSri() {
        srealm = Realm.getInstance(mainRealmConfig)
        val rezulatiSri = srealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Srijeda", Case.INSENSITIVE
        )?.findAll()
        val adapter3 = rezulatiSri?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerSri.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerSri.setHasFixedSize(true)
        binding!!.recyclerSri.adapter = adapter3
    }

    fun showSriTemp() {
        strealm = Realm.getInstance(tempRealm)
        val rezulatiSri1 = strealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Srijeda", Case.INSENSITIVE
        )?.findAll()
        adapterSriTemp = rezulatiSri1?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerSri.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerSri.setHasFixedSize(true)
        binding!!.recyclerSri.adapter = adapterSriTemp
    }

    fun showCet() {
        crealm = Realm.getInstance(mainRealmConfig)
        val rezulatiCet = crealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "četvrtak", Case.INSENSITIVE
        )?.findAll()
        val adapter4 = rezulatiCet?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerCet.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerCet.setHasFixedSize(true)
        binding!!.recyclerCet.adapter = adapter4
    }

    fun showCetTemp() {
        ctrealm = Realm.getInstance(tempRealm)
        val rezulatiCet1 = ctrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "četvrtak", Case.INSENSITIVE
        )?.findAll()
        adapterCetTemp = rezulatiCet1?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerCet.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerCet.setHasFixedSize(true)
        binding!!.recyclerCet.adapter = adapterCetTemp
    }

    fun showPet() {
        petrealm = Realm.getInstance(mainRealmConfig)
        val rezulatiPet = petrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Petak", Case.INSENSITIVE
        )?.findAll()
        val adapter5 = rezulatiPet?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerPet.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerPet.setHasFixedSize(true)
        binding!!.recyclerPet.adapter = adapter5
    }

    fun showPetTemp() {
        pettrealm = Realm.getInstance(tempRealm)
        val rezulatiPet1 = pettrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Petak", Case.INSENSITIVE
        )?.findAll()
        adapterPetTemp = rezulatiPet1?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerPet.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerPet.setHasFixedSize(true)
        binding!!.recyclerPet.adapter = adapterPetTemp
    }

    fun showSub() {
        subrealm = Realm.getInstance(mainRealmConfig)
        val rezulatiSub = subrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Subota", Case.INSENSITIVE
        )?.findAll()
        if (rezulatiSub?.isEmpty() == true) {
            binding!!.linearSub.visibility = View.GONE
            binding!!.linearParent.weightSum = 5f
        } else {
            binding!!.linearSub.visibility = View.VISIBLE
            binding!!.linearParent.weightSum = 6f
            val adapter6 = rezulatiSub?.let { EmployeeRVAdapterTable(it) }
            binding!!.recyclerSub.layoutManager = LinearLayoutManager(activity)
            binding!!.recyclerSub.setHasFixedSize(true)
            binding!!.recyclerSub.adapter = adapter6
        }
    }

    fun showSubTemp() {
        subtrealm = Realm.getInstance(tempRealm)
        val rezulatiSub1 = subtrealm?.where(Predavanja::class.java)?.contains(
            "detaljnoVrijeme", "Subota", Case.INSENSITIVE
        )?.findAll()
        adapterSubTemp = rezulatiSub1?.let { EmployeeRVAdapterTable(it) }
        binding!!.recyclerSub.layoutManager = LinearLayoutManager(activity)
        binding!!.recyclerSub.adapter = adapterSubTemp
    }

    fun updateTemporaryWeek() {
        adapterPonTemp!!.notifyDataSetChanged()
        adapterUtoTemp!!.notifyDataSetChanged()
        adapterSriTemp!!.notifyDataSetChanged()
        adapterCetTemp!!.notifyDataSetChanged()
        adapterPetTemp!!.notifyDataSetChanged()
        adapterSubTemp!!.notifyDataSetChanged()
    }

    fun checkNetwork() {
        if (context?.let { NetworkUtils.isNetworkAvailable(it) } == true) {
            binding!!.odaberiDan.visibility = View.VISIBLE
        } else {
            binding!!.odaberiDan.visibility = View.INVISIBLE
            showSnacOffline()
        }
    }

    fun showSnacOffline() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), """
     Niste povezani.
     Prikazuje se raspored ovog tjedna.
     """.trimIndent(), Snackbar.LENGTH_INDEFINITE
        )
        val vjuz = snack!!.view
        vjuz.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack!!.setAction("OSVJEŽI") {
            snack!!.dismiss()
            checkNetwork()
        }
        snack!!.setActionTextColor(resources.getColor(R.color.white))
        snack!!.show()
    }

    fun showSnackError() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), "Došlo je do " +
                    "pogreške pri dohvaćanju rasporeda", Snackbar.LENGTH_SHORT
        )
        val vjuzs = snack!!.view
        vjuzs.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack!!.show()
    }

    override fun onStop() {
        super.onStop()
        if (snack != null) {
            snack!!.dismiss()
        }
        if (client != null) {
            client!!.dispatcher.cancelAll()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (rlm != null) {
            rlm!!.close()
        }
        if (prealm != null) {
            prealm!!.close()
        }
        if (ptrealm != null) {
            ptrealm!!.close()
        }
        if (urealm != null) {
            urealm!!.close()
        }
        if (utrealm != null) {
            utrealm!!.close()
        }
        if (srealm != null) {
            srealm!!.close()
        }
        if (strealm != null) {
            strealm!!.close()
        }
        if (crealm != null) {
            crealm!!.close()
        }
        if (ctrealm != null) {
            ctrealm!!.close()
        }
        if (petrealm != null) {
            petrealm!!.close()
        }
        if (pettrealm != null) {
            pettrealm!!.close()
        }
        if (subrealm != null) {
            subrealm!!.close()
        }
        if (subtrealm != null) {
            subtrealm!!.close()
        }
    }
}