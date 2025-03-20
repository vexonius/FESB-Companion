package com.tstudioz.fax.fme.feature.iksica.view

import android.app.Application
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.cameras.CamerasRepositoryInterface
import com.tstudioz.fax.fme.feature.iksica.models.IksicaResult
import com.tstudioz.fax.fme.feature.iksica.models.MenzaLocation
import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import com.tstudioz.fax.fme.feature.iksica.repository.IksicaRepositoryInterface
import com.tstudioz.fax.fme.feature.menza.MenzaResult
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.repository.MenzaRepositoryInterface
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis

@InternalCoroutinesApi
class IksicaViewModel(
    private val repository: IksicaRepositoryInterface,
    private val application: Application,
    private val camerasRepository: CamerasRepositoryInterface,
    private val menzaRepository: MenzaRepositoryInterface
) : ViewModel() {

    val snackbarHostState = SnackbarHostState()

    private val _studentData = MutableLiveData<StudentData?>(null)
    val studentData: LiveData<StudentData?> = _studentData

    private val _receiptSelected = MutableLiveData<IksicaReceiptState>(IksicaReceiptState.None)
    val receiptSelected: LiveData<IksicaReceiptState> = _receiptSelected

    private val _viewState = MutableLiveData<IksicaViewState>(IksicaViewState.Initial)
    val viewState: LiveData<IksicaViewState> = _viewState

    private val _images = MutableLiveData<List<Pair<MenzaLocation, HttpUrl?>>?>(null)
    val images: LiveData<List<Pair<MenzaLocation, HttpUrl?>>?> = _images

    private val _menza = MutableLiveData<List<Pair<MenzaLocation, Menza?>>>()
    val menza: LiveData<List<Pair<MenzaLocation, Menza?>>> = _menza

    val menzaOpened: MutableLiveData<Boolean> = MutableLiveData(false)

    private var updateUrlsJob: Job? = null

    val map = listOf(
        MenzaLocation(
            name = "STOP",
            address = "Ruđera Boškovića 32, Split",
            meniName = "fesb_stop",
            cameraName = "b8_27_eb_ac_55_f5",
        ),
        MenzaLocation(
            name = "Medicinski Fakultet",
            address = "Šoltanska 2, Split",
            meniName = "medicina",
            cameraName = "b8_27_eb_47_b4_60",
        ),
        MenzaLocation(
            name = "Hostel Spinut",
            address = "Spinutska ulica 2, Split",
            meniName = "hostel",
            cameraName = "b8_27_eb_56_1c_fa",
        ),
        MenzaLocation(
            name = "Indeks",
            address = "Svačićeva 8, Split",
            meniName = "indeks",
            cameraName = "b8_27_eb_82_01_dd",
        ),
        MenzaLocation(
            name = "Kampus",
            address = "Cvite Fiskovića 3, Split",
            meniName = "kampus",
            cameraName = "b8_27_eb_aa_ed_1c",
        ),
        MenzaLocation(
            name = "Ekonomski Fakultet",
            address = "Cvite Fiskovića 5, Split",
            meniName = "efst",
            cameraName = "b8_27_eb_d4_79_96",
        ),
        MenzaLocation(
            name = "FGAG",
            address = "Ul. Matice hrvatske 15, Split",
            meniName = "fgag",
            cameraName = "b8_27_eb_ff_a3_7c",
        ),
        MenzaLocation(
            name = "FESB",
            address = "Ruđera Boškovića 32, Split",
            meniName = "fesb_vrh",
            cameraName = "b8_27_eb_d1_4b_4a",
        ),
    )

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Iksica", throwable.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(application.getString(R.string.error_general_iksica))
        }
    }

    init {
        loadReceiptsFromCache()
        fetchMenza()
    }

    private fun loadReceiptsFromCache() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _viewState.postValue(IksicaViewState.Loading)
            val model = repository.getCache() ?: return@launch

            _viewState.postValue(IksicaViewState.Success(model))
            _studentData.postValue(model)
        }
    }

    fun getReceipts() {
        _studentData.value?.let { _viewState.value = IksicaViewState.Fetching(it) }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (val result = repository.getCardDataAndReceipts()) {
                is IksicaResult.CardAndReceiptsResult.Success -> {
                    val model = result.data
                    _viewState.postValue(IksicaViewState.Success(model))
                    _studentData.postValue(model)
                }

                is IksicaResult.CardAndReceiptsResult.Failure -> {
                    snackbarHostState.showSnackbar(
                        application.getString(R.string.error_fetching_receipts_iksica),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    fun getReceiptDetails(receipt: Receipt?) {
        if (receipt == null) {
            hideReceiptDetails()
            return
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _receiptSelected.postValue(IksicaReceiptState.Fetching)
            when (val details = repository.getReceipt(receipt.url)) {
                is IksicaResult.ReceiptResult.Success -> {
                    _receiptSelected.postValue(IksicaReceiptState.Success(receipt.copy(receiptDetails = details.data)))
                }

                is IksicaResult.ReceiptResult.Failure -> {
                    _receiptSelected.postValue(IksicaReceiptState.Error(details.throwable.message.toString()))
                    snackbarHostState.showSnackbar(
                        application.getString(R.string.error_receipt_details_iksica),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    private fun fetchMenza() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _menza.postValue(map.map {
                it to when (val menza = menzaRepository.fetchMenzaDetails(it.meniName, false)) {
                    is MenzaResult.Success -> {
                        if (menza.data == null) {
                            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja menze")
                            null
                        } else menza.data
                    }

                    is MenzaResult.Failure -> {
                        snackbarHostState.showSnackbar("Greška prilikom dohvaćanja menze")
                        null
                    }
                }
            })
            Log.d("Menza", "Menza fetched")
        }
    }

    private fun getImageUrlsReal(onlyFesb: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            if (!onlyFesb) {
                _images.postValue(map.map { async { it to camerasRepository.getImages(it.cameraName) } }.awaitAll())
            } else {
                _images.postValue(map.map { location ->
                    location to if (location.meniName == "fesb_vrh") { camerasRepository.getImages(location.cameraName) }
                    else { _images.value?.find { it.first == location }?.second }
                })
            }
        }
    }


    private fun getImageUrlsApproximately() {
        _images.value = map.map {
            val twentySecsAgo = LocalTime.now().minusMinutes(1)
            val nowSecs = twentySecsAgo.second.div(5).times(5).toString().padStart(2, '0')
            val nowMins = twentySecsAgo.minute.toString().padStart(2, '0')
            val nowHours = twentySecsAgo.hour.toString().padStart(2, '0')
            val filename = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern(
                        if (it.meniName == "fesb_vrh") "yyyy-MM-dd_'$nowHours''i'$nowMins'i$nowSecs.jpg'"
                        else "yyyy-MM-dd_HH'i${nowMins}i00.jpg'"
                    )
                )
            it to HttpUrl.Builder()
                .scheme("https")
                .host("camerasfiles.dbtouch.com")
                .addPathSegment("images")
                .addPathSegment(it.cameraName)
                .addPathSegment(filename)
                .build()
        }

    }

    fun updateMenzaUrls() {
        //getImageUrlsReal()
        getImageUrlsApproximately()
        updateUrlsJob = viewModelScope.launch {
            while (isActive) {
                val now = LocalTime.now().second
                if (now.mod(5) == 4) {
                    Log.d("images", "Fetching images FESB")
                    getImageUrlsReal(onlyFesb = true)
                }
                if (now.mod(20) == 0) {
                    Log.d("images", "Fetching images all")
                    getImageUrlsReal()
                }
                delay(999)
            }
        }
    }

    private fun cancelUpdateUrlsJob() {
        updateUrlsJob?.cancel()
    }

    fun openMenza() {
        menzaOpened.postValue(true)
        fetchMenza()
    }

    fun closeMenza() {
        cancelUpdateUrlsJob()
        menzaOpened.postValue(false)
    }

    fun hideReceiptDetails() {
        _receiptSelected.postValue(IksicaReceiptState.None)
    }
}