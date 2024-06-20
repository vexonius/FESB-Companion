package com.tstudioz.fax.fme.database.models

data class ReceiptItem(
    val nazivArtikla: String,
    var kolicina: Int,
    val cijenaJednogArtikla: Double,
    val cijenaUkupno: Double,
    val iznosSubvencije: Double
)
