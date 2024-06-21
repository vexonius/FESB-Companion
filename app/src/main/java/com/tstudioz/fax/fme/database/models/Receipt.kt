package com.tstudioz.fax.fme.database.models

import android.util.Log
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

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

fun Receipt.toRealmObject(): ReceiptRealm {
    val receipt = this
    val rlm = ReceiptRealm().apply {
        restoran = receipt.restoran
        datum = receipt.datum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        datumString = receipt.datumString
        vrijeme = receipt.vrijeme
        iznosRacuna = receipt.iznosRacuna
        iznosSubvencije = receipt.iznosSubvencije
        autorizacija = receipt.autorizacija
        urlSastavnica = receipt.urlSastavnica
    }
    return rlm
}

fun ReceiptRealm.fromRealmObject(): Receipt {
    val receiptRealm = this
    return Receipt(
        restoran = receiptRealm.restoran ?: "",
        datum = LocalDate.parse(receiptRealm.datum, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
        datumString = receiptRealm.datumString ?: "",
        vrijeme = receiptRealm.vrijeme ?: "",
        iznosRacuna = receiptRealm.iznosRacuna ?: 0.0,
        iznosSubvencije = receiptRealm.iznosSubvencije ?: 0.0,
        autorizacija = receiptRealm.autorizacija ?: "",
        urlSastavnica = receiptRealm.urlSastavnica ?: ""
    )
}

open class ReceiptRealm : RealmObject {
    @PrimaryKey
    var id : String = UUID.randomUUID().toString()
    var restoran: String? = null
    var datum: String? = null
    var datumString: String? = null
    var vrijeme: String? = null
    var iznosRacuna: Double? = null
    var iznosSubvencije: Double? = null
    var autorizacija: String? = null
    var urlSastavnica: String? = null
}