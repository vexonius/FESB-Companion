package com.tstudioz.fax.fme.database.models

import com.tstudioz.fax.fme.R
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.FullText
import io.realm.kotlin.types.annotations.PrimaryKey

open class Predavanja : RealmObject {

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

    @FullText
    var detaljnoVrijeme: String? = null
    var dvorana: String? = null
    var boja: String? = null


    fun getBoja(): Int {
        return when (predavanjeIme) {
            "Predavanje" -> R.color.blue_nice
            "Auditorne vježbe" -> R.color.green_nice
            "Kolokvij" -> R.color.purple_nice
            "Laboratorijske vježbe" -> R.color.red_nice
            "Konstrukcijske vježbe" -> R.color.grey_nice
            "Seminar" -> R.color.blue_nice
            "Ispit" -> R.color.purple_dark
            else -> {
                R.color.blue_nice
            }
        }
    }


    val getCompactTitle: String
        get() {
            val compactImePredavanja = StringBuilder()
            if (predmetPredavanja != null
                && predmetPredavanja?.isEmpty() == false
                && predmetPredavanja?.contains(" ") == true
            ) {
                val predmetPredavanja = predmetPredavanja?.split(" ")?.toTypedArray()
                if (predmetPredavanja != null) {
                    for (str in predmetPredavanja)
                        compactImePredavanja.append(str[0])
                }
                return compactImePredavanja.toString()
            }
            return predmetPredavanja ?: ""
        }

    fun setPredavanjeVrsta(predavanjeVrsta: String?) {
        predavanjeIme = predavanjeVrsta
    }

    val getTimeRange: String
        get() = rasponVremena ?: ""

    val getHall: String
        get() = dvorana ?: ""
}
