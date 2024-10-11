package com.tstudioz.fax.fme.feature.iksica.models

import io.realm.kotlin.ext.toRealmList

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
    val spentToday: Double,
    var receipts: List<Receipt>
) {

    constructor(model: StudentDataRealm) : this(
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
        spentToday = model.spentToday,
        receipts = model.receipts.map { it.fromRealmObject() }
    )

    fun with(receipts: List<Receipt>): StudentData {
        val model = this
        model.receipts = receipts

        return model
    }

    fun toRealmModel(): StudentDataRealm =
         StudentDataRealm(
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
            this.spentToday,
            this.receipts.map { it.toRealmObject() }.toRealmList()
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
            spentToday = 0.0,
            receipts = emptyList()
        )
    }
}
