package com.tstudioz.fax.fme.common.user.models

import com.tstudioz.fax.fme.database.models.UserRealm

data class User(
    var username: String,
    var password: String
) {

    constructor(model: UserRealm) : this(model.username, model.password)

    val email: String
        get() = "$username@fesb.hr"

    fun toRealmModel(): UserRealm = UserRealm().also {
        it.username = username
        it.password = password
    }

}

