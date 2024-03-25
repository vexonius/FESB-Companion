package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject

open class Materijal : RealmObject {

    var url: String? = null
    var vrsta: String? = null
    var imeMtarijala: String? = null
    var icon = 0
    var downloadable = 0

}
