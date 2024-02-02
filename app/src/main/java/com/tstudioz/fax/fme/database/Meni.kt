package com.tstudioz.fax.fme.database

import io.realm.kotlin.types.RealmObject

open class Meni : RealmObject {

    var id: String? = null
    var type: String? = null
    var jelo1: String? = null
    var jelo2: String? = null
    var jelo3: String? = null
    var jelo4: String? = null
    var desert: String? = null
    var cijena: String? = null

}
