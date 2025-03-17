package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject

open class UserRealm : RealmObject {

    var fullName: String = ""
    var username: String = ""
    var password: String = ""

}
