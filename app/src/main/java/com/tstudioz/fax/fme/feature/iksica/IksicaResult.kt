package com.tstudioz.fax.fme.feature.iksica

import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem

sealed class IksicaResult {

    sealed class ReceiptResult : IksicaResult() {
        data class Success(val data: MutableList<ReceiptItem>) : ReceiptResult()
        class Failure(val throwable: Throwable) : ReceiptResult()
    }

    sealed class ReceiptsResult : IksicaResult() {
        data class Success(val data: List<Receipt>) : ReceiptsResult()
        class Failure(val throwable: Throwable) : ReceiptsResult()
    }

}