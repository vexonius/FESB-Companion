package com.tstudioz.fax.fme.feature.timetable.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.databinding.TabTimetableBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class TimeTableFragment : Fragment() {

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val timetableViewModel: TimetableViewModel by activityViewModel()
    private var binding: TabTimetableBinding? = null

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TabTimetableBinding.inflate(inflater, container, false)
        val composeView = binding?.composeView

        composeView?.setContent {
            AppTheme {
                TimetableCompose(timetableViewModel)
            }
        }
        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refreshTimetable).isVisible = true
        menu.findItem(R.id.chooseSchedule).setVisible(true)
        super.onPrepareOptionsMenu(menu)
    }
}