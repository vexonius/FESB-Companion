package com.tstudioz.fax.fme.database

import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

open class KolegijTjedan : RealmObject {

    var index = 0
    var opis: String? = null
    var tjedan: String? = null
    var materijali: RealmList<Materijal>? = null

}
