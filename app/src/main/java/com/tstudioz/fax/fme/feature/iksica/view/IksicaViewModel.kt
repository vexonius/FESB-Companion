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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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

    private val _images = MutableLiveData<List<Pair<String, HttpUrl?>>?>(null)
    val images: LiveData<List<Pair<String, HttpUrl?>>?> = _images

    private val _menza = MutableLiveData<Map<String, Menza?>>()
    val menza: LiveData<Map<String, Menza?>> = _menza

    val menzaOpened: MutableLiveData<Boolean> = MutableLiveData(false)

    private var updateUrlsJob: Job? = null

    val mapOfCameras = mapOf(
        /*"nothin1" to "B8_27_EB_33_5C_A8",
        "nothin2" to "B8_27_EB_40_18_25",
        "nothin3" to "b8_27_eb_27_10_43",
        "nothin5" to "b8_27_eb_62_eb_61",
        "nothin6" to "b8_27_eb_69_c3_d3",
        "nothin7" to "b8_27_eb_84_6b_7f",
        "nothin8" to "b8_27_eb_92_2f_df",
        "nothin9" to "b8_27_eb_96_25_80",
        "nothin10" to "b8_27_eb_99_71_4a",
        "nothin12" to "b8_27_eb_ca_18_85",
        "nothin13" to "b8_27_eb_f6_28_58",*/
        "fesb_stop" to "b8_27_eb_ac_55_f5",
        "medicina" to "b8_27_eb_47_b4_60",
        "hostel" to "b8_27_eb_56_1c_fa",
        "indeks" to "b8_27_eb_82_01_dd",
        "kampus" to "b8_27_eb_aa_ed_1c",
        "efst" to "b8_27_eb_d4_79_96",
        "fgag" to "b8_27_eb_ff_a3_7c",
        "fesb_vrh" to "b8_27_eb_d1_4b_4a",
    )

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Iksica", throwable.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar(application.getString(R.string.error_general_iksica))
        }
    }

    init {
        loadReceiptsFromCache()
        //loadMenzaAndImages()
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

    fun loadMenzaAndImages() {
        //getImages()
        fetchMenza()
    }

    private fun getImages() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _images.postValue(mapOfCameras.map { it.key to camerasRepository.getImages(it.value) })
        }
    }

    private fun fetchMenza() {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _menza.postValue(
                mapOfCameras.mapValues {
                    when (val menza = menzaRepository.fetchMenzaDetails(it.key, false)) {
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
                }
            )
        }
    }

    fun getImageUrlsApproximately() {
        _images.value = mapOfCameras.map {
            val nowSecs = LocalTime.now().minusSeconds(20).second.div(5).times(5).toString().padStart(2, '0')
            val nowMins = LocalTime.now().minusSeconds(20).minute
            it.key to HttpUrl.Builder()
                .scheme("https")
                .host("camerasfiles.dbtouch.com")
                .addPathSegment("images")
                .addPathSegment(it.value)
                .addPathSegment(
                    LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern(if (it.key != "fesb_vrh") "yyyy-MM-dd_HH'i'mm'i00.jpg'" else "yyyy-MM-dd_HH'i'$nowMins'i$nowSecs.jpg'")
                    )
                )
                .build()
        }

    }

    fun openMenza() {
        menzaOpened.postValue(true)
        loadMenzaAndImages()
    }

    fun updateMenzaUrls() {
        updateUrlsJob = viewModelScope.launch {
            while (true) {
                delay(999)
                if (LocalTime.now().second.mod(5) == 4)
                    getImageUrlsApproximately()
            }
        }
    }

    private fun cancelUpdateUrlsJob() {
        updateUrlsJob?.cancel()
    }

    fun closeMenza() {
        cancelUpdateUrlsJob()
        menzaOpened.postValue(false)
    }

    fun hideReceiptDetails() {
        _receiptSelected.postValue(IksicaReceiptState.None)
    }
}