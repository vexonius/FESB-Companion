package com.tstudioz.fax.fme.database.models

data class ReceiptItem(
    val nazivArtikla: String,
    val kolicina: String,
    val cijenaJednogArtikla: String,
    val cijenaUkupno: String,
    val iznosSubvencije: String
)
