package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject

data class StudentDataIksica (
    val nameSurname: String,
    val rightsLevel: String,
    val dailySupport: Double,
    val oib: String,
    val jmbag : String,
    val iksicaNumber: String,
    val rightsFrom : String,
    val rightsTo : String,
)

class StudentDataIksicaRealm: RealmObject {
    var nameSurname: String = ""
    var rightsLevel: String = ""
    var dailySupport: Double = 0.0
    var oib: String = ""
    var jmbag: String = ""
    var iksicaNumber: String = ""
    var rightsFrom: String = ""
    var rightsTo: String = ""
}

fun StudentDataIksica.toRealmObject(): StudentDataIksicaRealm {
    val studentDataIksica = this
    return StudentDataIksicaRealm().apply {
        nameSurname = studentDataIksica.nameSurname
        rightsLevel = studentDataIksica.rightsLevel
        dailySupport = studentDataIksica.dailySupport
        oib = studentDataIksica.oib
        jmbag = studentDataIksica.jmbag
        iksicaNumber = studentDataIksica.iksicaNumber
        rightsFrom = studentDataIksica.rightsFrom
        rightsTo = studentDataIksica.rightsTo
    }
}

fun StudentDataIksicaRealm.fromRealmObject(): StudentDataIksica {
    return StudentDataIksica(
        nameSurname = nameSurname,
        rightsLevel = rightsLevel,
        dailySupport = dailySupport,
        oib = oib,
        jmbag = jmbag,
        iksicaNumber = iksicaNumber,
        rightsFrom = rightsFrom,
        rightsTo = rightsTo
    )
}