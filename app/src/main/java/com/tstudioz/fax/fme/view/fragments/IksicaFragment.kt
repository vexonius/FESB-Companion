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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject

class IksicaFragment : Fragment() {

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private val iksicaViewModel: IksicaViewModel by inject()
    private var binding: FragmentIksicaBinding? = null
    private val shPref: SharedPreferences by inject()

    @OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIksicaBinding.inflate(inflater, container, false)
        val composeView = binding?.composeView!!

        iksicaViewModel.login(
            (shPref.getString("username", "") ?: "") + "@fesb.hr",
            shPref.getString("password", "") ?: ""
        ) // Unsafe call on a nullable receiver of type "IksicaViewModel?

        iksicaViewModel.receipts.observe(viewLifecycleOwner) {
            it.forEach(::println)
            composeView.setContent {
                AppTheme { IksicaCompose() }
            }
        }
        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.refresMe).isVisible = true
        menu.findItem(R.id.choosesched).setVisible(true)
        super.onPrepareOptionsMenu(menu)
    }
}