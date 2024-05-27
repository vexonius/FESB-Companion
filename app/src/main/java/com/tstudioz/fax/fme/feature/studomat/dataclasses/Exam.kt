package com.example.studomatisvu.model.dataclasses

data class Exam(
    var name: String = "",
    var date: String = "",
    var time: String = "",
    var registerUntilDate: String = "",
    var registerUntilTime: String = "",
    var unregisterUntilDate: String = "",
    var unregisterUntilTime: String = "",
    var type: String = "",
    var description: String = "",
    var totalAttendances: String = "",
    var attendancesThisYear: String = "")
