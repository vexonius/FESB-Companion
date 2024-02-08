package com.tstudioz.fax.fme.models.data

import com.tstudioz.fax.fme.database.models.Korisnik

data class User(
    var username: String,
    var password: String) {

    val email: String
        get() = "$username@fesb.hr"

    fun toRealmModel(): Korisnik = Korisnik().also {
        it.username = username
        it.password = password
    }

}

