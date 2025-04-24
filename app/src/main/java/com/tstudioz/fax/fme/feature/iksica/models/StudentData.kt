package com.tstudioz.fax.fme.feature.iksica.models

data class StudentData(
    val imageUrl: String?,
    val nameSurname: String,
    val rightsLevel: String,
    val dailySupport: Double,
    val oib: String,
    val jmbag: String,
    val cardNumber: String,
    val rightsFrom: String,
    val rightsTo: String,
    val balance: Double,
    val spentToday: Double
) {

    constructor(studentDataRoom: StudentDataRoom) : this(
        imageUrl = studentDataRoom.imageUrl,
        rightsLevel = studentDataRoom.rightsLevel,
        dailySupport = studentDataRoom.dailySupport,
        nameSurname = studentDataRoom.nameSurname,
        rightsTo = studentDataRoom.rightsTo,
        rightsFrom = studentDataRoom.rightsFrom,
        cardNumber = studentDataRoom.cardNumber,
        oib = studentDataRoom.oib,
        jmbag = studentDataRoom.jmbag,
        balance = studentDataRoom.balance,
        spentToday = studentDataRoom.spentToday
    )

    companion object {
        val empty = StudentData(
            imageUrl = null,
            nameSurname = "",
            rightsLevel = "",
            dailySupport = 0.0,
            oib = "",
            jmbag = "",
            cardNumber = "",
            rightsFrom = "",
            rightsTo = "",
            balance = 0.0,
            spentToday = 0.0
        )
    }
}
