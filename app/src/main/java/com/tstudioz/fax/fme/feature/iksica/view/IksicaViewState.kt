package com.tstudioz.fax.fme.feature.iksica.view

import com.tstudioz.fax.fme.feature.iksica.models.IksicaData
import com.tstudioz.fax.fme.feature.iksica.models.Receipt

sealed class IksicaViewState {
    data object Initial : IksicaViewState()
    data object Loading : IksicaViewState()
    data class Fetching(val data: IksicaData) : IksicaViewState()
    data class Success(val data: IksicaData) : IksicaViewState()
    data object Empty : IksicaViewState()
}

sealed class IksicaReceiptState {
    data object None : IksicaReceiptState()
    data object Fetching : IksicaReceiptState()
    data class Success(val data: Receipt) : IksicaReceiptState()
    data class Error(val message: String) : IksicaReceiptState()
}