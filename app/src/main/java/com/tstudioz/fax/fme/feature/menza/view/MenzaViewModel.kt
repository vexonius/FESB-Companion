package com.tstudioz.fax.fme.feature.menza.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.feature.menza.repository.CamerasRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.MenzaLocationType
import com.tstudioz.fax.fme.feature.menza.menzaLocations
import com.tstudioz.fax.fme.feature.menza.models.MenzaLocation
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.repository.MenzaRepositoryInterface
import com.tstudioz.fax.fme.networking.InternetConnectionObserver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@InternalCoroutinesApi
class MenzaViewModel(
    application: Application,
    private val menzaRepository: MenzaRepositoryInterface,
    private val camerasRepository: CamerasRepositoryInterface,
) : AndroidViewModel(application) {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("MenzaViewModel", "CoroutineExceptionHandler got $exception")
    }

    private val _images = MutableLiveData<Pair<MenzaLocation, HttpUrl?>?>(null)
    val images: LiveData<Pair<MenzaLocation, HttpUrl?>?> = _images
    val internetAvailable: LiveData<Boolean> = InternetConnectionObserver.get()

    private val _menza = MutableLiveData<List<Pair<MenzaLocation, Menza?>>>()
    val menza: LiveData<List<Pair<MenzaLocation, Menza?>>> = _menza

    val menzaOpened: MutableLiveData<Boolean> = MutableLiveData(false)

    private var updateUrlsJob: Job? = null

    init {
        fetchMenza()
    }

    private fun fetchMenza() {
        if (internetAvailable.value == false) return
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _menza.postValue(menzaLocations.map {
                it to when (val menza = menzaRepository.fetchMenzaDetails(it.meniName, false)) {
                    is MenzaResult.Success -> {
                        menza.data
                    }

                    is MenzaResult.Failure -> {
                        //snackbarHostState.showSnackbar("Greška prilikom dohvaćanja menze")
                        null
                    }
                }
            })
        }
    }


    private fun getImageUrlApproximately(location: MenzaLocation) {
        val minuteAgo = LocalDateTime.now().minusMinutes(1)
        val nowSecs = minuteAgo.second.div(5).times(5).toString().padStart(2, '0')
        val filename = minuteAgo.format(
            DateTimeFormatter.ofPattern(
                if (location.meniName == MenzaLocationType.FESB_VRH) "yyyy-MM-dd_HH'i'mm'i$nowSecs.jpg'"
                else "yyyy-MM-dd_HH'i'mm'i00.jpg'"
            )
        )
        _images.value =
            location to HttpUrl.Builder()
                .scheme("https")
                .host("camerasfiles.dbtouch.com")
                .addPathSegment("images")
                .addPathSegment(location.cameraName)
                .addPathSegment(filename)
                .build()

    }

    private fun getImageUrl(location: MenzaLocation) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _images.postValue(location to camerasRepository.getImages(location.cameraName))
        }
    }

    fun updateMenzaUrl(location: MenzaLocation) {
        _images.value = null
        updateUrlsJob?.cancel()

        val interval = 20

        updateUrlsJob = viewModelScope.launch {
            getImageUrlApproximately(location)
            getImageUrl(location)
            while (isActive) {
                if (LocalTime.now().second.mod(interval) == 4) {
                    getImageUrl(location)
                }
                delay(1000L)
            }
        }
    }

    private fun cancelUpdateUrlJob() {
        updateUrlsJob?.cancel()
    }

    fun openMenza() {
        menzaOpened.postValue(true)
        fetchMenza()
    }

    fun closeMenza() {
        cancelUpdateUrlJob()
        menzaOpened.postValue(false)
    }

}
