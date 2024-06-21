package com.tstudioz.fax.fme.database.models

import java.time.LocalDate

data class Receipt(
    val restoran: String,
    val datum: LocalDate,
    val datumString: String,
    val vrijeme: String,
    val iznosRacuna: Double,
    val iznosSubvencije: Double,
    val autorizacija: String,
    val urlSastavnica: String,
    var detaljiRacuna: List<ReceiptItem>? = null
)