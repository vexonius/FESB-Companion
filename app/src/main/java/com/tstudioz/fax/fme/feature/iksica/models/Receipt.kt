package com.tstudioz.fax.fme.feature.iksica.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Receipt(
    val restaurant: String,
    val date: LocalDate,
    val dateString: String,
    val time: String,
    val receiptAmount: Double,
    val subsidizedAmount: Double,
    val paidAmount: Double,
    val authorised: String,
    val url: String,
    var receiptDetails: List<ReceiptItem>? = null
)

fun Receipt.toRoomObject(): ReceiptRoom {
    val receipt = this
    val rlm = ReceiptRoom().apply {
        restaurant = receipt.restaurant
        date = receipt.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        dateString = receipt.dateString
        time = receipt.time
        receiptAmount = receipt.receiptAmount
        subsidizedAmount = receipt.subsidizedAmount
        paidAmount = receipt.paidAmount
        authorised = receipt.authorised
        href = receipt.url
    }
    return rlm
}

fun ReceiptRoom.fromRoomObject(): Receipt {
    val receiptRealm = this
    return Receipt(
        restaurant = receiptRealm.restaurant ?: "",
        date = LocalDate.parse(receiptRealm.date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
        dateString = receiptRealm.dateString ?: "",
        time = receiptRealm.time ?: "",
        receiptAmount = receiptRealm.receiptAmount ?: 0.0,
        subsidizedAmount = receiptRealm.subsidizedAmount ?: 0.0,
        paidAmount = receiptRealm.paidAmount ?: 0.0,
        authorised = receiptRealm.authorised ?: "",
        url = receiptRealm.href ?: ""
    )
}

@Entity
data class ReceiptRoom (
    @PrimaryKey
    var id : String = UUID.randomUUID().toString(),
    var restaurant: String? = null,
    var date: String? = null,
    var dateString: String? = null,
    var time: String? = null,
    var receiptAmount: Double? = null,
    var subsidizedAmount: Double? = null,
    var paidAmount: Double? = null,
    var authorised: String? = null,
    var href: String? = null
)