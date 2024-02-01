package com.tstudioz.fax.fme.database

import io.realm.RealmObject

/**
 * Created by amarthus on 26-Apr-17.
 */
open class Racun : RealmObject() {
    var pare: String? = null
    var ime_prezime: String? = null
    var broj_kartice: String? = null
}
