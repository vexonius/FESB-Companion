package com.tstudioz.fax.fme.feature.attendance.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.feature.attendance.compose.AttendanceCompose
import com.tstudioz.fax.fme.random.NetworkUtils
import io.realm.kotlin.Realm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

@OptIn(InternalCoroutinesApi::class)
class AttendanceFragment : Fragment(), KoinComponent {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val attendanceViewModel: AttendanceViewModel by viewModel() // TODO: Bad code, fix this later

    private var snack: Snackbar? = null
    private var binding: PrisutnostTabBinding? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ConstraintLayout? {

        setHasOptionsMenu(true)
        binding = PrisutnostTabBinding.inflate(inflater, container, false)

        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            attendanceViewModel.fetchAttendance()
        } else {
            showSnack("Offline")
        }

        attendanceViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error) {
                showSnack("Error")
            }
        }

        attendanceViewModel.attendanceList.observe(viewLifecycleOwner) { attendanceList ->
            if (!attendanceList.isNullOrEmpty() && attendanceViewModel.shouldShow.value == true) {
                binding!!.composeView.setContent {
                    AttendanceCompose(attendanceItems = attendanceViewModel.attendanceList.value ?: emptyList())
                }
            } else {
                binding!!.composeView.setContent {
                    AppTheme {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(64.dp)
                            )
                        }
                    }
                }
            }
        }
        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun showSnack(error: String) {
        val text = when (error) {
            "Error" -> "Došlo je do pogreške"
            "Offline" -> "Niste povezani"
            else -> "Došlo je do pogreške"
        }
        val snack = Snackbar.make(requireActivity().findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
        snack.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
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
    }
}
