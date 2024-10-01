package com.tstudioz.fax.fme.feature.iksica

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


enum class LoginStatus(val text: String) {
    UNSET("Setting up..."),
    AUTH_STATE("Getting AuthState..."),
    LOGIN("Logging in..."),
    ASP_NET_SESSION("Getting ASP.NET Session..."),
    SUCCESS("Parsing Data..."),
    FAILURE("Failure")
}

fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            if (value is Boolean && value) {
                removeObserver(this)
            } else if (value !is Boolean) {
                removeObserver(this)
            }
            observer(value)
        }
    })
}