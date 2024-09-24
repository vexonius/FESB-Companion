package com.tstudioz.fax.fme.feature.iksica.models

import io.realm.kotlin.types.RealmObject

class StudentDataIksica(
    var nameSurname: String,
    var rightsLevel: String,
    var dailySupport: Double,
    var oib: String,
    var jmbag: String,
    var iksicaNumber: String,
    var rightsFrom: String,
    var rightsTo: String
) : RealmObject {
    constructor() : this("", "", 0.0, "", "", "", "", "")
}
