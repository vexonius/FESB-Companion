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

    constructor(studentData: StudentData) : this(
        id = ID,
        imageUrl = studentData.imageUrl,
        nameSurname = studentData.nameSurname,
        rightsLevel = studentData.rightsLevel,
        dailySupport = studentData.dailySupport,
        oib = studentData.oib,
        jmbag = studentData.jmbag,
        cardNumber = studentData.cardNumber,
        rightsFrom = studentData.rightsFrom,
        rightsTo = studentData.rightsTo,
        balance = studentData.balance,
        spentToday = studentData.spentToday
    )

    companion object {
        const val ID = "1"
    }
}

