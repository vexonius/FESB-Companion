package com.tstudioz.fax.fme.feature.iksica.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StudentDataRoom(
    @PrimaryKey
    var id: String,
    var imageUrl: String?,
    var nameSurname: String,
    var rightsLevel: String,
    var dailySupport: Double,
    var oib: String,
    var jmbag: String,
    var cardNumber: String,
    var rightsFrom: String,
    var rightsTo: String,
    var balance: Double,
    var spentToday: Double,
) {

    constructor(model: StudentData) : this(
        ID,
        model.imageUrl,
        model.nameSurname,
        model.rightsLevel,
        model.dailySupport,
        model.oib,
        model.jmbag,
        model.cardNumber,
        model.rightsFrom,
        model.rightsTo,
        model.balance,
        model.spentToday
    )

    companion object {
        const val ID = "1"
    }
}

