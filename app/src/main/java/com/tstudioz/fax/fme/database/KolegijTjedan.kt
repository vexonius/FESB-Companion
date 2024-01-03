package com.tstudioz.fax.fme.database

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by amarthus on 09-May-17.
 */
open class KolegijTjedan : RealmObject() {
    var index = 0
    var opis: String? = null
    var tjedan: String? = null
    var materijali: RealmList<Materijal>? = null
}
