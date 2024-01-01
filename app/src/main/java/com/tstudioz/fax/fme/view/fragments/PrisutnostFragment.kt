package com.tstudioz.fax.fme.view.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion.Companion.instance
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.view.adapters.DolasciAdapter
import com.tstudioz.fax.fme.database.Dolazak
import com.tstudioz.fax.fme.database.Korisnik
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.viewmodel.PrisutnostViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
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

@OptIn(InternalCoroutinesApi::class)
class PrisutnostFragment : Fragment() {
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
    private lateinit var prisutnostviewmodel : PrisutnostViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = PrisutnostTabBinding.inflate(inflater, container, false)
        prisutnostviewmodel = PrisutnostViewModel()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        hideRecyc()
        startFetching()
        return binding!!.root
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun fetchPrisutnost() {
        val cookieJar: CookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(activity)
        )
        deletePreviousResults()
        lifecycleScope.launch { prisutnostviewmodel.fetchPrisutnost() }

        prisutnostviewmodel.gotPri.observe(viewLifecycleOwner){ gotPri ->
            if (gotPri){
                requireActivity().runOnUiThread {
                    showRecyclerviewWinterSem()
                    showRecyclerviewSummerSem()
                    binding!!.progressAttend.visibility = View.INVISIBLE
                    binding!!.nestedAttend.visibility = View.VISIBLE
                }
            }
            else {
                showSnackError()
            }
        }
    }

    private fun showRecyclerviewWinterSem() {
        wRealm = Realm.getInstance(realmConfig)
        val dolasciWinter: RealmResults<Dolazak>? =
            wRealm?.where(Dolazak::class.java)?.equalTo("semestar", 1.toInt())?.findAll()
        try {
            binding?.recyclerZimski?.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false
            )
            if (dolasciWinter!=null)
                winterAdapter = context?.let { DolasciAdapter(it, dolasciWinter) }
            binding?.recyclerZimski?.adapter = winterAdapter
        } finally {
            wRealm?.close()
        }
    }

    private fun showRecyclerviewSummerSem() {
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