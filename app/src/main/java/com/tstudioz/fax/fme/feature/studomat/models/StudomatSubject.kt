package com.tstudioz.fax.fme.feature.studomat.models

import io.realm.kotlin.types.RealmObject

open class StudomatSubject(
    var name: String = "",
    var electiveGroup: String = "",
    var semester: String = "",
    var lectures: String = "",
    var exercises: String = "",
    var ectsEnrolled: String = "",
    var isTaken: String = "",
    var status: String = "",
    var grade: String = "",
    var examDate: String = "",
    var year: String = "",
    var isPassed: Boolean = false
) : RealmObject {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", false)

    override fun toString(): String {
        return "StudomatSubject(name='$name', semester='$semester', lectures='$lectures', exercises='$exercises', ectsEnrolled='$ectsEnrolled', \nisTaken='$isTaken', status='$status', grade='$grade', examDate='$examDate', year='$year')\n"
    }
}
