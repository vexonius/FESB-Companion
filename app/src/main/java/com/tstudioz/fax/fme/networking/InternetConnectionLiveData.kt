package com.tstudioz.fax.fme.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.tstudioz.fax.fme.di.getSharedPreferences
import com.tstudioz.fax.fme.util.PreferenceHelper.get
import com.tstudioz.fax.fme.util.SPKey

class InternetConnectionLiveData(context: Context) : LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val shPref = getSharedPreferences(context)
    var testMode = shPref[SPKey.TEST_MODE, false]

    init {
        // Set initial value based on current network state
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        postValue(hasInternet)
    }


    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (testMode) return
            postValue(true)
        }

        override fun onLost(network: Network) {
            if (testMode) return
            postValue(false)
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            if (testMode) return
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            postValue(hasInternet)
        }
    }

    override fun onActive() {
        super.onActive()
        testMode = shPref[SPKey.TEST_MODE, false]
        if (testMode) {
            postValue(false)
            return
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        if (testMode) return
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}

object InternetConnectionObserver {
    private lateinit var internetLiveData: InternetConnectionLiveData

    fun init(context: Context) {
        internetLiveData = InternetConnectionLiveData(context.applicationContext)
    }

    fun get(): LiveData<Boolean> {
        return internetLiveData
    }
}


