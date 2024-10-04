package com.tstudioz.fax.fme.feature.menza

import com.tstudioz.fax.fme.database.models.Meni

sealed class MenzaResult {
    data class Success(val data: List<Meni>): MenzaResult()
    class Failure(exception: Throwable) : MenzaResult()
}