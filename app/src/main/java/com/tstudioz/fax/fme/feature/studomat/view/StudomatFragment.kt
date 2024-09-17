package com.tstudioz.fax.fme.feature.studomat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.feature.studomat.compose.HomeCompose
import org.koin.androidx.viewmodel.ext.android.viewModel


class StudomatFragment : Fragment() {

    private val studomatViewModel: StudomatViewModel by viewModel<StudomatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        studomatViewModel.initStudomat()

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.refreshTimetable).isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        })

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme { HomeCompose(studomatViewModel) }
            }
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
        }
    }
}