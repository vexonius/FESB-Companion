package com.tstudioz.fax.fme.feature.iksica.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class StudentDataRealm(
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
    var receipts: RealmList<ReceiptRealm>
) : RealmObject {

    constructor() : this(
        imageUrl = "",
        "",
        "",
        0.0,
        "",
        "",
        "",
        "",
        "",
        0.0,
        0.0,
        realmListOf())
}

