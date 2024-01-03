package com.tstudioz.fax.fme.database

import io.realm.RealmObject

/**
 * Created by amarthus on 26-Apr-17.
 */
open class Korisnik : RealmObject() {
    var username: String? = null
    var lozinka: String? = null
}
