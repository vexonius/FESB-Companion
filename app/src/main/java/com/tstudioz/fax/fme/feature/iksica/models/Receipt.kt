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
) {
    constructor(model: ReceiptRoom) : this(
        restaurant = model.restaurant ?: "",
        date = LocalDate.parse(model.date, DateTimeFormatter.ofPattern("dd.MM.yyyy")),
        dateString = model.dateString ?: "",
        time = model.time ?: "",
        receiptAmount = model.receiptAmount ?: 0.0,
        subsidizedAmount = model.subsidizedAmount ?: 0.0,
        paidAmount = model.paidAmount ?: 0.0,
        authorised = model.authorised ?: "",
        url = model.href ?: ""
    )
}

@Entity
data class ReceiptRoom(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var restaurant: String? = null,
    var date: String? = null,
    var dateString: String? = null,
    var time: String? = null,
    var receiptAmount: Double? = null,
    var subsidizedAmount: Double? = null,
    var paidAmount: Double? = null,
    var authorised: String? = null,
    var href: String? = null
) {
    constructor(model: Receipt) : this() {
        restaurant = model.restaurant
        date = model.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        dateString = model.dateString
        time = model.time
        receiptAmount = model.receiptAmount
        subsidizedAmount = model.subsidizedAmount
        paidAmount = model.paidAmount
        authorised = model.authorised
        href = model.url
    }
}