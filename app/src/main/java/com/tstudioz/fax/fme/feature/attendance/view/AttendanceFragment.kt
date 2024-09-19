package com.tstudioz.fax.fme.feature.attendance.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.PrisutnostTabBinding
import com.tstudioz.fax.fme.feature.attendance.compose.AttendanceCompose
import com.tstudioz.fax.fme.random.NetworkUtils
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
            showSnack(getString(R.string.attendance_offline))
        }

        attendanceViewModel.error.observe(viewLifecycleOwner) { error ->
            if (error) {
                showSnack(getString(R.string.attendance_error))
            }
        }

        binding!!.composeView.setContent {
            AttendanceCompose(attendanceItems = attendanceViewModel.attendanceList)
        }
        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refreshTimetable).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun showSnack(text: String = getString(R.string.attendance_error)) {
        val snack = Snackbar.make(requireActivity().findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
        snack.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack.setAction(getString(R.string.attendance_retry)) {
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
