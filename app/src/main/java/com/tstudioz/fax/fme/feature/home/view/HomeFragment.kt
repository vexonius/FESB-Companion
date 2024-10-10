package com.tstudioz.fax.fme.feature.home.view

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.TabHomeBinding
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import com.tstudioz.fax.fme.random.NetworkUtils
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class)
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val menzaViewModel: MenzaViewModel by viewModel()
    private val networkUtils: NetworkUtils by inject()

    private var binding: TabHomeBinding? = null
    private var snack: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CoordinatorLayout? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = TabHomeBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        fetchForecast()

        binding?.composeView?.setContent {
            HomeTabCompose(
                weather = homeViewModel.weatherDisplay,
                notes = homeViewModel.notes,
                events = homeViewModel.events,
                menza = menzaViewModel.menza,
                insertNote = homeViewModel::insert,
                deleteNote = homeViewModel::delete
            )
        }
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        menzaViewModel.getMenza()

        setCyanStatusBarColor()
        homeViewModel.fetchDailyTimetable()
    }

    private fun fetchForecast() {
        if (networkUtils.isNetworkAvailable()) {
            homeViewModel.getForecast()
        } else {
            showSnack("Niste povezani")
        }
    }

    private fun setCyanStatusBarColor() {
        (activity as AppCompatActivity?)
            ?.supportActionBar
            ?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_cyan)))
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.darker_cyan)
    }

    private fun showSnack(text: String) {
        snack = Snackbar.make(requireActivity().findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
        snack?.view?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.show()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)
            ?.supportActionBar
            ?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary)))
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
    }
}