package com.tstudioz.fax.fme.feature.iksica.view

import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.StudentData

sealed class IksicaViewState {
    data object Initial : IksicaViewState()
    data object Loading : IksicaViewState()
    data class Fetching(val data: StudentData) : IksicaViewState()
    data class Success(val data: StudentData) : IksicaViewState()
    data object Empty : IksicaViewState()
}

sealed class IksicaReceiptState {
    data object None : IksicaReceiptState()
    data object Fetching : IksicaReceiptState()
    data class Success(val data: Receipt) : IksicaReceiptState()
    data class Error(val message: String) : IksicaReceiptState()
}