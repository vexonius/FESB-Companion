package com.tstudioz.fax.fme.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.Application.FESBCompanion
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManager
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.adapters.DolasciAdapter
import com.tstudioz.fax.fme.viewmodel.AttendanceViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject

@OptIn(InternalCoroutinesApi::class)
class PrisutnostFragment : Fragment() {

    private val dbManager: DatabaseManager by inject()

    private var snack: Snackbar? = null
    private var semAdapter: DolasciAdapter? = null
    private var realm: Realm? = null
    private var binding: PrisutnostTabBinding? = null
    private var shPref: SharedPreferences? =  FESBCompanion.instance?.sP
    @OptIn(ExperimentalCoroutinesApi::class)
    private lateinit var prisutnostviewmodel : AttendanceViewModel
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ConstraintLayout? {

        setHasOptionsMenu(true)
        binding = PrisutnostTabBinding.inflate(inflater, container, false)
        prisutnostviewmodel = AttendanceViewModel()
        hideRecyc()
        fetchPrisutnost()
        return binding?.root
    }

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    fun fetchPrisutnost() {
        val username = shPref?.getString("username", "")
        val password = shPref?.getString("password","")
        val user =User("","","")
        if (username != null && password != null){
            user.username = username
            user.password = password
            user.fmail = "$username@fesb.hr"
        }
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            prisutnostviewmodel.fetchAttendance(user)
        } else {
            showSnack("Offline")
        }

        prisutnostviewmodel.shouldShow.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                requireActivity().runOnUiThread {
                    showRecyclerview(1)
                    showRecyclerview(2)
                    binding?.progressAttend?.visibility = View.INVISIBLE
                    binding?.nestedAttend?.visibility = View.VISIBLE
                }
            }
            else {
                showSnack("Error")
            }
        }
    }

    private fun showRecyclerview(sem: Int) {
        if (realm == null || realm?.isClosed() == true){
            realm = Realm.open(dbManager.getDefaultConfiguration()) }
        val dolasciSem: RealmResults<Dolazak>? =
            realm?.query<Dolazak>("semestar = $0", sem)?.find()
        try {
            if (!dolasciSem.isNullOrEmpty()){
                semAdapter = context?.let { DolasciAdapter(it, dolasciSem) }}
            if (!dolasciSem.isNullOrEmpty() && semAdapter != null)
                if (sem == 1){
                    binding?.recyclerZimski?.layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    binding?.recyclerZimski?.adapter = semAdapter
                } else if (sem == 2){
                    binding?.recyclerLItnji?.layoutManager = LinearLayoutManager(activity,
                        LinearLayoutManager.HORIZONTAL, false)
                    binding?.recyclerLItnji?.adapter = semAdapter
                }
                semAdapter = null
        } catch (_: Exception){}
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    private fun showSnack(error: String ) {
        val snack = when (error) {
            "Error" -> Snackbar.make(
                requireActivity().findViewById(R.id.coordinatorLayout), "Došlo je do " +
                        "pogreške", Snackbar.LENGTH_LONG)
            "Offline" -> Snackbar.make(
                requireActivity().findViewById(R.id.coordinatorLayout), "Niste " +
                        "povezani", Snackbar.LENGTH_INDEFINITE)
            else -> Snackbar.make(
                requireActivity().findViewById(R.id.coordinatorLayout), "Došlo je do " +
                        "pogreške", Snackbar.LENGTH_LONG)
        }
        val okvir = snack.view
        okvir.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack.setAction("Ponovi") {
            snack.dismiss()
            fetchPrisutnost()
        }
        snack.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snack.show()
    }

    private fun hideRecyc() {
        binding?.nestedAttend?.visibility = View.INVISIBLE
        binding?.progressAttend?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (snack != null) {
            snack?.dismiss()
        }
        if (realm != null) {
            realm?.close()
        }
    }
}