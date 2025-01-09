package com.tstudioz.fax.fme.feature.iksica.models

data class ReceiptItem(
    val articleName: String,
    /**
     * Amount of the article.
     */
    var amount: Int,
    /**
     * Discounted price
     */
    val price: Double,
    /**
     * Full price
     */
    val total: Double,
    /**
     * Subsidized amount
     */
    val subsidizedAmount: Double
)
