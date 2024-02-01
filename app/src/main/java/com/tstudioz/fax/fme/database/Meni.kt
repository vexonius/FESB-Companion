package com.tstudioz.fax.fme.database

import io.realm.Realm
import io.realm.RealmObject

/**
 * Created by etino7 on 21/01/2018.
 */
open class Meni : RealmObject() {
    var id: String? = null
    var type: String? = null
    var jelo1: String? = null
    var jelo2: String? = null
    var jelo3: String? = null
    var jelo4: String? = null
    var desert: String? = null
    var cijena: String? = null

    override fun getRealm(): Realm {
        return super.getRealm()
    }
}
