package com.tstudioz.fax.fme.feature.iksica.models

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class StudentDataRealm(
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

