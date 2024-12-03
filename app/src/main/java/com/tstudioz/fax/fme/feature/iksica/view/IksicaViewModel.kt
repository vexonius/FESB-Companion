package com.tstudioz.fax.fme.feature.iksica.view

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import okhttp3.HttpUrl

@InternalCoroutinesApi
class IksicaViewModel(
    private val repository: IksicaRepositoryInterface,
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

    private val _imageUrl = MutableLiveData<HttpUrl?>(null)
    val imageUrl: LiveData<HttpUrl?> = _imageUrl

    private val _imageName = MutableLiveData<String?>(null)
    val imageName: LiveData<String?> = _imageName

    private val _menza = MutableLiveData<Menza?>()
    val menza: LiveData<Menza?> = _menza

    private val mapOfCameras = mapOf(
        "nothin1" to "B8_27_EB_33_5C_A8",
        "nothin2" to "B8_27_EB_40_18_25",
        "nothin3" to "b8_27_eb_27_10_43",
        "nothin4" to "b8_27_eb_47_b4_60",
        "nothin5" to "b8_27_eb_62_eb_61",
        "nothin6" to "b8_27_eb_69_c3_d3",
        "nothin7" to "b8_27_eb_84_6b_7f",
        "nothin8" to "b8_27_eb_92_2f_df",
        "nothin9" to "b8_27_eb_96_25_80",
        "nothin10" to "b8_27_eb_99_71_4a",
        "nothin12" to "b8_27_eb_ca_18_85",
        "nothin13" to "b8_27_eb_f6_28_58",

        "hostel" to "b8_27_eb_56_1c_fa",
        "indeks" to "b8_27_eb_82_01_dd",
        "kampus" to "b8_27_eb_aa_ed_1c",
        "fesb_stop" to "b8_27_eb_ac_55_f5",
        "fesb_vrh" to "b8_27_eb_d1_4b_4a",
        "efst" to "b8_27_eb_d4_79_96",
        "fgag" to "b8_27_eb_ff_a3_7c"
    )

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Iksica", throwable.message.toString())
        viewModelScope.launch(Dispatchers.Main) {
            snackbarHostState.showSnackbar("Greška prilikom dohvaćanja podataka")
        }
    }

    init {
        loadReceiptsFromCache()
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
                        "Greška prilikom dohvaćanja liste računa",
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
                        "Greška prilikom dohvaćanja detalja računa",
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    fun runImageMenza(place:String, name:String) {
        clearImage()
        getImage(place)
        fetchMenza(place, name)
    }

    private fun getImage(page: String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            _imageUrl.postValue(mapOfCameras[page]?.let { camerasRepository.getImage(it) })
        }
    }

    private fun fetchMenza(place:String, name:String) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            when (val menza = menzaRepository.fetchMenzaDetails(place,false)) {
                is MenzaResult.Success -> {
                    _menza.postValue(menza.data)
                    setImageName(name)
                }

                is MenzaResult.Failure -> {
                    snackbarHostState.showSnackbar("Greška prilikom dohvaćanja menze")
                }
            }
        }
    }

    fun setImageName(name: String?){
        _imageName.postValue(name)
    }

    fun closeImageMenza(){
        clearImage()
        _menza.postValue(null)
    }

    private fun clearImage() {
        _imageUrl.value = null
        _imageName.value=null
    }

    fun hideReceiptDetails() {
        _receiptSelected.postValue(IksicaReceiptState.None)
    }
}