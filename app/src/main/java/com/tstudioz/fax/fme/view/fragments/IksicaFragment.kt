package com.tstudioz.fax.fme.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.IksicaCompose
import com.tstudioz.fax.fme.databinding.FragmentIksicaBinding
import com.tstudioz.fax.fme.viewmodel.IksicaViewModel
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class IksicaFragment : Fragment() {

    @OptIn(InternalCoroutinesApi::class)
    private val iksicaViewModel: IksicaViewModel by inject()
    private var binding: FragmentIksicaBinding? = null
    private val mainViewModel: MainViewModel by activityViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIksicaBinding.inflate(inflater, container, false)
        val composeView = binding?.composeView!!

        iksicaViewModel.getReceipts()
        composeView.setContent {
            AppTheme { IksicaCompose(mainViewModel) }
        }

        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refreshTimetable).isVisible = false
        menu.findItem(R.id.chooseSchedule).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }
}