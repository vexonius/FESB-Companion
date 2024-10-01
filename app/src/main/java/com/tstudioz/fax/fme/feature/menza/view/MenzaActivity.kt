package com.tstudioz.fax.fme.feature.menza.view

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.ActivityMenzaBinding
import com.tstudioz.fax.fme.feature.menza.MenzaCompose
import com.tstudioz.fax.fme.random.NetworkUtils
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


@OptIn(InternalCoroutinesApi::class)
class MenzaActivity : AppCompatActivity() {

    private var snack: Snackbar? = null
    private var binding: ActivityMenzaBinding? = null
    private val menzaViewModel: MenzaViewModel by viewModel()
    private val networkUtils: NetworkUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenzaBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setTextTypeface()
        checkConditionsAndFetch()
        handleBackPress()/*
        setContent { MenzaCompose(menzaViewModel.menza) }*/
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun checkConditionsAndFetch() {
        if (networkUtils.isNetworkAvailable()) {
            menzaViewModel.getMenza("http://sc.dbtouch.com/menu/api.php/?place=fesb_vrh")
        } else {
            menzaViewModel.readMenza()
            showSnac("Niste povezani (Prikazuje se stari meni)")
            //binding?.menzaProgress?.visibility = View.VISIBLE
        }
        menzaViewModel.menzaGot.observe(this) { menzaGot ->
            if (menzaGot) {
                //binding?.menzaProgress?.visibility = View.INVISIBLE
                showMenies()
            } else {
                //binding?.menzaProgress?.visibility = View.VISIBLE
            }
        }
        menzaViewModel.menzaError.observe(this) { menzaError ->
            if (menzaError) {
                //binding?.menzaProgress?.visibility = View.INVISIBLE
                showSnac("Greška prilikom dohvaćanja menija")
            }
        }
    }

    private fun showMenies() {
        /*val results = menzaViewModel.menza.value
        if ((results?.isEmpty()) != null) {
            val adapter = MeniesAdapter(results)
            binding?.menzaRecyclerview?.layoutManager = LinearLayoutManager(this)
            binding?.menzaRecyclerview?.setHasFixedSize(true)
            binding?.menzaRecyclerview?.adapter = adapter
        } else {
            binding?.menzaRecyclerview?.visibility = View.INVISIBLE
            binding?.cookieHeaderRoot?.visibility = View.VISIBLE
        }*/
    }

    private fun setTextTypeface() {
        /*binding?.menzaTitle?.typeface = Typeface.createFromAsset(assets, "fonts/OpenSans-Bold.ttf")
        binding?.cookieHeaderText?.typeface = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular.ttf")
    */}

    private fun showSnac(text: String) {
        snack = Snackbar.make(findViewById(R.id.menza_root), text, Snackbar.LENGTH_INDEFINITE)
        snack?.view?.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.red_nice))
        snack?.setAction("PONOVI") {
            snack?.dismiss()
            checkConditionsAndFetch()
        }
        snack?.setActionTextColor(ContextCompat.getColor(this , R.color.white))
        snack?.show()
    }
}