package com.tstudioz.fax.fme.feature.menza

import com.tstudioz.fax.fme.feature.menza.models.Menza

sealed class MenzaResult {
    data class Success(val data: Menza) : MenzaResult()
    class Failure(exception: Throwable) : MenzaResult()
}