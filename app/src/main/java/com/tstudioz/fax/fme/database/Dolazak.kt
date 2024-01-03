package com.tstudioz.fax.fme.database

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by amarthus on 18-May-17.
 */
open class Dolazak : RealmObject() {
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
