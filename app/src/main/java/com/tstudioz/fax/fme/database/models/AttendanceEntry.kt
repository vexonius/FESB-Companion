package com.tstudioz.fax.fme.database.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class AttendanceEntry : RealmObject {

    @PrimaryKey
    var id: String? = null
    var `class`: String? = null
    var type: String? = null
    var link: String? = null
    var attended = 0
    var absent = 0
    var required: String? = null
    var semester = 0
    var total = 0

}
