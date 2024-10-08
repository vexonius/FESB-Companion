package com.tstudioz.fax.fme.feature.iksica.view

import com.tstudioz.fax.fme.feature.iksica.models.Receipt

sealed class IksicaViewState {
    data object Initial : IksicaViewState()
    data object Loading : IksicaViewState()
    data object Empty : IksicaViewState()
    data class Success(val data: List<Receipt>) : IksicaViewState()
    data class Error(val message: String) : IksicaViewState()
}