package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class Dolazak : RealmObject {

    @PrimaryKey
    var id: String? = null
    var predmet: String? = null
    var vrsta: String? = null
    var link: String? = null
    var attended = 0
    var absent = 0
    var required: String? = null
    var semestar = 0
    var total = 0

}
