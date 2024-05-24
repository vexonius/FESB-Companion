package com.tstudioz.fax.fme.feature.attendance.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.DatabaseManagerInterface
import com.tstudioz.fax.fme.database.models.Dolazak
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.feature.attendance.compose.AttendanceCompose
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.random.NetworkUtils
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

@OptIn(InternalCoroutinesApi::class)
class AttendanceFragment : Fragment(), KoinComponent {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val attendanceViewModel: AttendanceViewModel by viewModel() // TODO: Bad code, fix this later

    private var snack: Snackbar? = null
    private var realm: Realm? = null
    private var binding: PrisutnostTabBinding? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): ConstraintLayout? {

        setHasOptionsMenu(true)
        binding = PrisutnostTabBinding.inflate(inflater, container, false)

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            attendanceViewModel.fetchAttendance()
        } else {
            showSnack("Offline")
        }

        attendanceViewModel.shouldShow.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                binding!!.composeView.setContent {
                    AttendanceCompose(attendanceItems = attendanceViewModel.attendanceList.value ?: emptyMap())
                }
            }
            else {
                binding!!.composeView.setContent {
                    ProgressBar(requireContext())
                }
                showSnack("Error")
            }
        }
        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
            attendanceViewModel.fetchAttendance()
        }
        snack.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        snack.show()
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
