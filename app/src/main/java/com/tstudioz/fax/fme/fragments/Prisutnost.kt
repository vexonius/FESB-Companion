package com.tstudioz.fax.fme.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.adapters.DolasciAdapter
import com.tstudioz.fax.fme.database.Dolazak
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.networking.NetworkUtils
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.coroutines.InternalCoroutinesApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.CookieJar
import okhttp3.FormBody.Builder
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.util.StringTokenizer
import java.util.UUID

class Prisutnost : Fragment() {
    var realmConfig: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("prisutnost.realm")
        .schemaVersion(10)
        .deleteRealmIfMigrationNeeded()
        .build()
    private var snack: Snackbar? = null
    private var winterAdapter: DolasciAdapter? = null
    private var summerAdapter: DolasciAdapter? = null
    private var nRealm: Realm? = null
    private var cRealm: Realm? = null
    private var sRealm: Realm? = null
    private var wRealm: Realm? = null
    private var okHttpClient: OkHttpClient? = null
    private var binding: PrisutnostTabBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = PrisutnostTabBinding.inflate(inflater, container, false)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        hideRecyc()
        startFetching()
        return binding!!.root
    }

    @OptIn(InternalCoroutinesApi::class)
    fun fetchPrisutnost() {
        val cookieJar: CookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(activity)
        )
        okHttpClient = instance!!.okHttpInstance
        deletePreviousResults()
        cRealm = Realm.getDefaultInstance()
        val korisnik = cRealm?.where(Korisnik::class.java)?.findFirst()
        val formData: RequestBody = Builder()
            .add("Username", korisnik!!.getUsername())
            .add("Password", korisnik.getLozinka())
            .add("IsRememberMeChecked", "true")
            .build()
        val rq: Request = Request.Builder()
            .url(
                "https://korisnik.fesb.unist.hr/prijava?returnUrl=https://raspored.fesb" +
                        ".unist.hr"
            )
            .post(formData)
            .build()
        val call0 = okHttpClient!!.newCall(rq)
        call0.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("pogreska", "failure")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val request: Request = Request.Builder()
                    .url("https://raspored.fesb.unist.hr/part/prisutnost/opcenito/tablica")
                    .get()
                    .build()
                val call1 = okHttpClient!!.newCall(request)
                call1.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("pogreska", "failure")
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.code != 500) {
                            val doc = Jsoup.parse(response.body!!.string())
                            try {
                                val zimski = doc.select("div.semster.winter").first()
                                val litnji = doc.select("div.semster.summer").first()
                                val zimskaPredavanja = zimski.select("div.body.clearfix").first()
                                val litnjaPredavanja = litnji.select("div.body.clearfix").first()
                                if (zimski.getElementsByClass("emptyList").first() == null) {
                                    val zimskiKolegiji = zimskaPredavanja.select("a")
                                    for (element in zimskiKolegiji) {
                                        val request: Request = Request.Builder()
                                            .url(
                                                "https://raspored.fesb.unist.hr" + element.attr("href")
                                                    .toString()
                                            )
                                            .get()
                                            .build()
                                        val callonme = okHttpClient!!.newCall(request)
                                        callonme.enqueue(object : Callback {
                                            override fun onFailure(call: Call, e: IOException) {
                                                Log.d("pogreska", "failure")
                                            }

                                            @Throws(IOException::class)
                                            override fun onResponse(
                                                call: Call,
                                                response: Response
                                            ) {
                                                val document = Jsoup.parse(
                                                    response.body!!.string()
                                                )
                                                val mRealm1 = Realm.getInstance(realmConfig)
                                                try {
                                                    val content = document.getElementsByClass(
                                                        "courseCategories"
                                                    ).first()
                                                    val kategorije = content.select(
                                                        "div.courseCategory"
                                                    )
                                                    mRealm1.executeTransaction { realm ->
                                                        for (kat in kategorije) {
                                                            val mDolazak = realm.createObject(
                                                                Dolazak::class.java,
                                                                UUID.randomUUID().toString()
                                                            )
                                                            mDolazak.setSemestar(1)
                                                            mDolazak.setPredmet(
                                                                element.select("div.cellContent")
                                                                    .first().text()
                                                            )
                                                            mDolazak.setVrsta(
                                                                kat.getElementsByClass(
                                                                    "name"
                                                                ).first().text()
                                                            )
                                                            mDolazak.setAttended(
                                                                kat.select("div.attended > span.num")
                                                                    .first().text().toInt()
                                                            )
                                                            mDolazak.setAbsent(
                                                                kat.select("div.absent > span.num")
                                                                    .first().text().toInt()
                                                            )
                                                            mDolazak.setRequired(
                                                                kat.select(
                                                                    "div.required-attendance " +
                                                                            "> span"
                                                                ).first().text()
                                                            )
                                                            val string = kat.select(
                                                                "div" +
                                                                        ".required-attendance > " +
                                                                        "span"
                                                            ).first().text()
                                                            val st = StringTokenizer(string, " ")
                                                            val ric1 = st.nextToken()
                                                            val ric2 = st.nextToken()
                                                            val max = st.nextToken()
                                                            mDolazak.setTotal(max.toInt())
                                                        }
                                                    }
                                                } catch (exception: Exception) {
                                                    Log.d(
                                                        "Exception prisutnost",
                                                        exception.message!!
                                                    )
                                                    exception.printStackTrace()
                                                } finally {
                                                    mRealm1.close()
                                                }
                                            }
                                        })
                                    }
                                }
                                if (litnji.getElementsByClass("emptyList").first() == null) {
                                    val litnjiKolegiji = litnjaPredavanja.select("a")
                                    for (element in litnjiKolegiji) {
                                        val request: Request = Request.Builder()
                                            .url(
                                                "https://raspored.fesb.unist.hr" + element.attr("href")
                                                    .toString()
                                            )
                                            .get()
                                            .build()
                                        val callonme1 = okHttpClient!!.newCall(request)
                                        callonme1.enqueue(object : Callback {
                                            override fun onFailure(call: Call, e: IOException) {
                                                showSnackError()
                                            }

                                            @Throws(IOException::class)
                                            override fun onResponse(
                                                call: Call,
                                                response: Response
                                            ) {
                                                val document = Jsoup.parse(
                                                    response.body!!.string()
                                                )
                                                val mRealm2 = Realm.getInstance(realmConfig)
                                                try {
                                                    val content = document.getElementsByClass(
                                                        "courseCategories"
                                                    ).first()
                                                    val kategorije = content.select(
                                                        "div.courseCategory"
                                                    )
                                                    mRealm2.executeTransaction { realm ->
                                                        for (kat in kategorije) {
                                                            val mDolazak = realm.createObject(
                                                                Dolazak::class.java,
                                                                UUID.randomUUID().toString()
                                                            )
                                                            mDolazak.setSemestar(2)
                                                            mDolazak.setPredmet(
                                                                element.select("div.cellContent")
                                                                    .first().text()
                                                            )
                                                            mDolazak.setVrsta(
                                                                kat.getElementsByClass(
                                                                    "name"
                                                                ).first().text()
                                                            )
                                                            mDolazak.setAttended(
                                                                kat.select("div.attended > span.num")
                                                                    .first().text().toInt()
                                                            )
                                                            mDolazak.setAbsent(
                                                                kat.select("div.absent > span.num")
                                                                    .first().text().toInt()
                                                            )
                                                            mDolazak.setRequired(
                                                                kat.select(
                                                                    "div.required-attendance " +
                                                                            "> span"
                                                                ).first().text()
                                                            )
                                                            val string = kat.select(
                                                                "div" +
                                                                        ".required-attendance > " +
                                                                        "span"
                                                            ).first().text()
                                                            val st = StringTokenizer(string, " ")
                                                            val ric1 = st.nextToken()
                                                            val ric2 = st.nextToken()
                                                            val max = st.nextToken()
                                                            mDolazak.setTotal(max.toInt())
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Log.d("Exception prisutnost", e.message!!)
                                                    e.printStackTrace()
                                                } finally {
                                                    mRealm2.close()
                                                }
                                            }
                                        })
                                    }
                                }
                            } catch (ex: Exception) {
                                Log.d("Exception pris", ex.message!!)
                                ex.printStackTrace()
                            }
                            if (activity != null) {
                                activity!!.runOnUiThread {
                                    showRecyclerviewWinterSem()
                                    showRecyclerviewSummerSem()
                                    binding!!.progressAttend.visibility = View.INVISIBLE
                                    binding!!.nestedAttend.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            showSnackError()
                        }
                    }
                })
            }
        })
    }

    fun showRecyclerviewWinterSem() {
        wRealm = Realm.getInstance(realmConfig)
        val dolasciWinter: RealmResults<Dolazak>? =
            wRealm?.where(Dolazak::class.java)?.equalTo("semestar", 1.toInt())?.findAll()
        try {
            binding!!.recyclerZimski.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false
            )
            if (dolasciWinter!=null)
                winterAdapter = context?.let { DolasciAdapter(it, dolasciWinter) }
            binding!!.recyclerZimski.adapter = winterAdapter
        } finally {
            wRealm?.close()
        }
    }

    fun showRecyclerviewSummerSem() {
        sRealm = Realm.getInstance(realmConfig)
        val dolasciSummer: RealmResults<Dolazak>? =
            sRealm?.where(Dolazak::class.java)?.equalTo("semestar", 2.toInt())?.findAll()
        try {
            binding!!.recyclerLItnji.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false
            )
            if(dolasciSummer!=null)
                summerAdapter = context?.let { DolasciAdapter(it, dolasciSummer) }
            binding!!.recyclerLItnji.adapter = summerAdapter
        } finally {
            sRealm?.close()
        }
    }

    private fun deletePreviousResults() {
        nRealm = Realm.getInstance(realmConfig)
        val svaPrisutnost = nRealm?.where(Dolazak::class.java)?.findAll()
        nRealm?.executeTransaction { svaPrisutnost?.deleteAllFromRealm() }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun startFetching() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            fetchPrisutnost()
        } else {
            showSnacOffline()
        }
    }

    private fun showSnacOffline() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), "Niste " +
                    "povezani", Snackbar.LENGTH_INDEFINITE
        )
        val vjuz = snack!!.view
        vjuz.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack!!.setAction("PONOVI") {
            snack!!.dismiss()
            startFetching()
        }
        snack!!.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        snack!!.show()
    }

    fun showSnackError() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), "Došlo je do " +
                    "pogreške", Snackbar.LENGTH_LONG
        )
        val okvir = snack!!.view
        okvir.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack!!.setAction("PONOVI") {
            snack!!.dismiss()
            startFetching()
        }
        snack!!.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snack!!.show()
    }

    private fun hideRecyc() {
        binding!!.nestedAttend.visibility = View.INVISIBLE
        binding!!.progressAttend.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        if (snack != null) {
            snack!!.dismiss()
        }
        if (okHttpClient != null) okHttpClient!!.dispatcher.cancelAll()
        if (cRealm != null) cRealm!!.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (nRealm != null) nRealm!!.close()
    }
}