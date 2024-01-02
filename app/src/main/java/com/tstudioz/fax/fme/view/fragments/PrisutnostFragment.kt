package com.tstudioz.fax.fme.view.fragments

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.Dolazak
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.adapters.DolasciAdapter
import com.tstudioz.fax.fme.viewmodel.PrisutnostViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
class PrisutnostFragment : Fragment() {
    private var realmConfig: RealmConfiguration = RealmConfiguration.Builder()
        .allowWritesOnUiThread(true)
        .name("prisutnost.realm")
        .schemaVersion(10)
        .deleteRealmIfMigrationNeeded()
        .build()
    private var snack: Snackbar? = null
    private var winterAdapter: DolasciAdapter? = null
    private var summerAdapter: DolasciAdapter? = null
    private var cRealm: Realm? = null
    private var sRealm: Realm? = null
    private var wRealm: Realm? = null
    private var binding: PrisutnostTabBinding? = null
    private var shPref: SharedPreferences? =  FESBCompanion.instance?.sP
    private lateinit var prisutnostviewmodel : PrisutnostViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ConstraintLayout? {

        setHasOptionsMenu(true)
        binding = PrisutnostTabBinding.inflate(inflater, container, false)
        prisutnostviewmodel = PrisutnostViewModel()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        hideRecyc()
        startFetching()
        return binding?.root
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun fetchPrisutnost() {
        cRealm = Realm.getInstance(realmConfig)
        //ako ovo uklonim nece da radi, kaze da je zatvoren realm...
        // FATAL EXCEPTION: main
        // Process: com.tstudioz.fax.fme, PID: 10798
        // java.lang.IllegalStateException: This Realm instance has already been closed, making it unusable.
        // val korisnik = cRealm?.where(Korisnik::class.java)?.findFirst()
        val username = shPref?.getString("username", "")
        val password = shPref?.getString("password","")
        val user =User("","","")
        if (username != null && password != null){
            user.username = username
            user.password = password
            user.fmail = "$username@fesb.hr"
        }
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            lifecycleScope.launch { prisutnostviewmodel.fetchPrisutnost(user) }
        }

        prisutnostviewmodel.gotPri.observe(viewLifecycleOwner){ gotPri ->
            if (gotPri){
                requireActivity().runOnUiThread {
                    showRecyclerviewWinterSem()
                    showRecyclerviewSummerSem()
                    binding?.progressAttend?.visibility = View.INVISIBLE
                    binding?.nestedAttend?.visibility = View.VISIBLE
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
            wRealm?.where(Dolazak::class.java)?.equalTo("semestar", "1".toInt())?.findAll()
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
            sRealm?.where(Dolazak::class.java)?.equalTo("semestar", "2".toInt())?.findAll()
        try {
            binding?.recyclerLItnji?.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false
            )
            if(dolasciSummer!=null)
                summerAdapter = context?.let { DolasciAdapter(it, dolasciSummer) }
            binding?.recyclerLItnji?.adapter = summerAdapter
        } finally {
            sRealm?.close()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun startFetching() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            fetchPrisutnost()
        } else {
            fetchPrisutnost()
            showSnacOffline()
        }
    }

    private fun showSnacOffline() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), "Niste " +
                    "povezani", Snackbar.LENGTH_INDEFINITE
        )
        val vjuz = snack?.view
        vjuz?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.setAction("PONOVI") {
            snack?.dismiss()
            startFetching()
        }
        snack?.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        snack?.show()
    }

    private fun showSnackError() {
        snack = Snackbar.make(
            requireActivity().findViewById(R.id.coordinatorLayout), "Došlo je do " +
                    "pogreške", Snackbar.LENGTH_LONG
        )
        val okvir = snack?.view
        okvir?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.setAction("PONOVI") {
            snack?.dismiss()
            startFetching()
        }
        snack?.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snack?.show()
    }

    private fun hideRecyc() {
        binding?.nestedAttend?.visibility = View.INVISIBLE
        binding?.progressAttend?.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        if (snack != null) {
            snack?.dismiss()
        }
        if (cRealm != null) cRealm?.close()
    }
}