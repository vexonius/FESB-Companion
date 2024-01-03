package com.tstudioz.fax.fme.database

import com.tstudioz.fax.fme.R
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Predavanja : RealmObject() {
    @PrimaryKey
    var id: String? = null
    var objectId = 0
    var predavanjeIme: String? = null
    var predmetPredavanja: String? = null
    var profesor: String? = null
    var rasponVremena: String? = null
    var brojSati: String? = null
    var grupa: String? = null
    var grupaShort: String? = null
    var detaljnoVrijeme: String? = null
    var dvorana: String? = null
    var boja: String? = null
    fun setPredavanjeVrsta(predavanjeVrsta: String?) {
        predavanjeIme = predavanjeVrsta
    }

    fun setBoja(boja: String?): Int {
        var bojaId = R.color.blue_nice
        when (boja) {
            "predavanje" -> bojaId = R.color.blue_nice
            "Auditorne" -> bojaId = R.color.green_nice
            "Kolokviji" -> bojaId = R.color.purple_nice
            "Laboratorijske vježbe" -> bojaId = R.color.red_nice
            "Konstrukcijske vježbe" -> bojaId = R.color.grey_nice
            "Ispiti" -> bojaId = R.color.purple_dark
        }
        return bojaId
    }
}
