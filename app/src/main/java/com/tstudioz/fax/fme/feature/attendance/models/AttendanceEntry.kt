package com.tstudioz.fax.fme.feature.attendance.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class AttendanceEntry : RealmObject {

    @PrimaryKey
    var id: String = ""
    var `class`: String = ""
    var type: String = ""
    var link: String = ""
    var attended = 0
    var absent = 0
    var required: Int = 0
    var semester = 0
    var total = 0

}
