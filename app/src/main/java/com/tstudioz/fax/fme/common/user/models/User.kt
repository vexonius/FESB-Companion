package com.tstudioz.fax.fme.common.user.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserRoom(
    @PrimaryKey
    var id: Int = 0,
    var username: String = "",
    var password: String = ""
)

data class User(
    var username: String,
    var password: String
) {

    constructor(model: UserRoom) : this(model.username, model.password)

    val email: String
        get() = "$username@fesb.hr"

    fun toRoomModel(): UserRoom = UserRoom().also {
        it.username = username
        it.password = password
    }

}

