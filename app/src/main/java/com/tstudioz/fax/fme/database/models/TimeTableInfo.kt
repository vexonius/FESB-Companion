package com.tstudioz.fax.fme.database.models

data class TimeTableInfo (
    var Id: Int = 0,
    var Name: String = "",
    var StartDate: Long = 0,
    var EndDate: Long = 0,
    var StartDateText: String = "",
    var EndDateText: String = "",
    var Category: String = "",
    var ColorCode: String = "",
    var IsWorking: Boolean = false,
)