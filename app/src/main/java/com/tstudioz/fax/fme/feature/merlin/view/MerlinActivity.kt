package com.tstudioz.fax.fme.feature.merlin.view

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.databinding.ActivityLoginBinding
import com.tstudioz.fax.fme.view.activities.MainActivity
import com.tstudioz.fax.fme.view.activities.Welcome
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel

@OptIn(InternalCoroutinesApi::class)
class MerlinActivity : AppCompatActivity() {

    private val merlinViewModel: MerlinViewModel by viewModel()

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        merlinViewModel.list.observe(this){
            setContent() {
                LazyColumn {
                    merlinViewModel.list.value?.size?.let {
                        items(it) { course ->
                            ListItem(headlineContent = {
                                Text(text = merlinViewModel.list.observeAsState().value?.get(course)?.fullname!!)
                            })
                        }
                    }
                }
            }
        }
        onBackListen()

    }



    private fun onBackListen(){
        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        })
    }




}