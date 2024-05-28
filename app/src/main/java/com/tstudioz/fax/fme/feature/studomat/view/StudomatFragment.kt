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
import org.koin.java.KoinJavaComponent


class StudomatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val studomatViewModel: StudomatViewModel by KoinJavaComponent.inject(StudomatViewModel::class.java)

        studomatViewModel.getStudomatData()

        (requireActivity() as MenuHost).addMenuProvider(object :MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.refresMe).isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {return false }

        })

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            studomatViewModel.predmetList.observe(viewLifecycleOwner) {
                setContent {
                    AppTheme{ HomeCompose(studomatViewModel) }
                }
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                        viewLifecycleOwner
                    )
                )
            }
        }
    }
}