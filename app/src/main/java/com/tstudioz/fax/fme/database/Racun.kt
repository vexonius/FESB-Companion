package com.tstudioz.fax.fme.database

import io.realm.kotlin.types.RealmObject

open class Racun : RealmObject {

    var pare: String? = null
    var ime_prezime: String? = null
    var broj_kartice: String? = null

}
