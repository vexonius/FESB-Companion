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

    constructor(model: StudentDataRoom) : this(
        imageUrl = model.imageUrl,
        rightsLevel = model.rightsLevel,
        dailySupport = model.dailySupport,
        nameSurname = model.nameSurname,
        rightsTo = model.rightsTo,
        rightsFrom = model.rightsFrom,
        cardNumber = model.cardNumber,
        oib = model.oib,
        jmbag = model.jmbag,
        balance = model.balance,
        spentToday = model.spentToday
    )

    fun toRoomModel(): StudentDataRoom =
         StudentDataRoom(
             "1",
            this.imageUrl,
            this.nameSurname,
            this.rightsLevel,
            this.dailySupport,
            this.oib,
            this.jmbag,
            this.cardNumber,
            this.rightsFrom,
            this.rightsTo,
            this.balance,
            this.spentToday
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
