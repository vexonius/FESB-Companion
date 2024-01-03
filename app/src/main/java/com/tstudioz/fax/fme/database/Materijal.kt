package com.tstudioz.fax.fme.database

import io.realm.RealmObject

/**
 * Created by amarthus on 09-May-17.
 */
open class Materijal : RealmObject() {
    var url: String? = null
    var vrsta: String? = null
    var imeMtarijala: String? = null
    var icon = 0
    var downloadable = 0
}
