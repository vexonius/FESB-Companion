package com.tstudioz.fax.fme.feature.studomat.models

import io.realm.kotlin.types.RealmObject

open class StudomatYearInfo : RealmObject {
    var courseName: String = ""
    var studyProgram: String = ""
    var parallelStudy: String = ""
    var academicYear: Int = 0
    var enrollmentIndicator: String = ""
    var payment: Boolean = false
    var fundingBasis: String = ""
    var universityCenter: String = ""
    var studentRightsValidUntil: String = ""
    var enrollmentDate: String = ""
    var enrollmentCompleted: Boolean = false
    var year: String = ""
    var href: String = ""

    override fun toString(): String {
        return "StudomatYearInfo(courseName='$courseName', studyProgram='$studyProgram', parallelStudy='$parallelStudy', academicYear=$academicYear, enrollmentIndicator='$enrollmentIndicator', payment=$payment, fundingBasis='$fundingBasis', universityCenter='$universityCenter', studentRightsValidUntil='$studentRightsValidUntil', enrollmentDate='$enrollmentDate', enrollmentCompleted=$enrollmentCompleted, year='$year', href='$href')"
    }
}