package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject

open class Korisnik : RealmObject {

    var username: String? = null
    var password: String? = null

}
