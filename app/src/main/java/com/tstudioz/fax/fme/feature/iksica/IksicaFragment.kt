package com.tstudioz.fax.fme.feature.iksica

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.databinding.FragmentIksicaBinding
import com.tstudioz.fax.fme.feature.iksica.compose.IksicaCompose
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class IksicaFragment : Fragment() {

    @OptIn(InternalCoroutinesApi::class)
    private val iksicaViewModel: IksicaViewModel by viewModel()
    private var binding: FragmentIksicaBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIksicaBinding.inflate(inflater, container, false)
        val composeView = binding?.composeView!!

        composeView.setContent {
            AppTheme {
                IksicaCompose(iksicaViewModel)
            }
        }

        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refreshTimetable).setVisible(false)
        menu.findItem(R.id.chooseSchedule).setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }
}