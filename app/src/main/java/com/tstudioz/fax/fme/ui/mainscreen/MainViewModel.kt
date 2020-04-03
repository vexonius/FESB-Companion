package com.tstudioz.fax.fme.ui.mainscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject


@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainViewModel : ViewModel() {

    private val repository: Repository by inject(Repository::class.java)

    init {
        loginUser()
    }

    private fun loginUser() {
        viewModelScope.launch {
            repository.attemptLogin()
                    .onStart { println("Started") }
                    .catch { println("Doslo je do pogreske") }
                    .collect { result -> Log.d("hello", result.fullname) }
        }
    }

}

