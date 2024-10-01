package com.tstudioz.fax.fme.feature.iksica.models

data class ReceiptItem(
    val articleName: String,
    var amount: Int,
    val price: Double,
    val total: Double,
    val subsidizedAmount: Double
)
