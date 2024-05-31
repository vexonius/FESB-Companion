package com.tstudioz.fax.fme.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.HomeCompose
import com.tstudioz.fax.fme.databinding.TimetableTabBinding
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class TimeTableFragment : Fragment() {

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val mainViewModel: MainViewModel by activityViewModel()
    private var binding: TimetableTabBinding? = null

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TimetableTabBinding.inflate(inflater, container, false)
        val composeView = binding?.composeView!!

        mainViewModel.showThisWeeksEvents()

        composeView.setContent {
            AppTheme { HomeCompose(mainViewModel) }
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