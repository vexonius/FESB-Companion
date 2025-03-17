package com.tstudioz.fax.fme.common.user.models

import com.tstudioz.fax.fme.database.models.UserRealm

data class User(
    val fullName: String,
    var username: String,
    var password: String
) {

    constructor(model: UserRealm) : this(model.fullName, model.username, model.password)

    val email: String
        get() = "$username@fesb.hr"

    fun toRealmModel(): UserRealm = UserRealm().also {
        it.fullName = fullName
        it.username = username
        it.password = password
    }

}

