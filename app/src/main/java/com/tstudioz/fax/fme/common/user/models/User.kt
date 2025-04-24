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
    constructor(user: User) : this(
        id = ID,
        username = user.username,
        password = user.password
    )

    companion object {
        const val ID = 0
    }
}

data class User(
    var username: String,
    var password: String
) {

    constructor(userRoom: UserRoom) : this(userRoom.username, userRoom.password)

    val email: String
        get() = "$username@fesb.hr"

}

