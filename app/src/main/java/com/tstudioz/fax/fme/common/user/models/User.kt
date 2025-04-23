package com.tstudioz.fax.fme.common.user.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserRoom(
    @PrimaryKey
    var id: Int = 0,
    var username: String = "",
    var password: String = ""
) {
    constructor(model: User) : this(
        id = 0,
        username = model.username,
        password = model.password
    )

    companion object {
        const val ID = 0
    }
}

data class User(
    var username: String,
    var password: String
) {

    constructor(model: UserRoom) : this(model.username, model.password)

    val email: String
        get() = "$username@fesb.hr"

}

