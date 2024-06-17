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
import com.tstudioz.fax.fme.random.NetworkUtils
import com.tstudioz.fax.fme.view.activities.MainActivity
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.json.JSONException
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException

@OptIn(InternalCoroutinesApi::class)
class HomeFragment : Fragment() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mainViewModel: MainViewModel by activityViewModel()
    private val homeViewModel: HomeViewModel by viewModel()

    private var binding: TabHomeBinding? = null
    private var snack: Snackbar? = null
    private val forecastUrl = "https://api.met.no/weatherapi/locationforecast/2.0/compact?" +
            "lat=$mLatitude&" +
            "lon=$mLongitude"

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CoordinatorLayout? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = TabHomeBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        fetchForcast()

        homeViewModel.getNotes()
        binding!!.composeView.setContent {
            HomeTabCompose(
                weather = homeViewModel.weatherDisplay,
                notes = homeViewModel.notes,
                events = mainViewModel.lessonsPerm,
                lastFetched = mainViewModel.lastFetched,
                insertNote = homeViewModel::insert,
                deleteNote = homeViewModel::delete
            )
        }
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        setCyanStatusBarColor()
        (activity as MainActivity?)?.mojRaspored()
    }

    @Throws(IOException::class, JSONException::class)
    private fun fetchForcast() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            homeViewModel.getForecast(forecastUrl)
        } else {
            showSnac("Niste povezani")
        }
    }

    private fun setCyanStatusBarColor() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(requireContext(), R.color.dark_cyan))
            )
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.darker_cyan)
    }

    private fun showSnac(text: String) {
        snack = Snackbar.make(requireActivity().findViewById(R.id.coordinatorLayout), text, Snackbar.LENGTH_LONG)
        snack?.view?.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red_nice))
        snack?.show()
    }

    override fun onStop() {
        (activity as AppCompatActivity?)?.supportActionBar
            ?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), R.color.colorPrimary)))
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
        super.onStop()
    }

    companion object {
        private const val mLatitude = 43.511287
        private const val mLongitude = 16.469252
    }
}