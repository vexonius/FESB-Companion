package com.tstudioz.fax.fme.database.models

import java.time.LocalDate

data class TimeTableInfo (
    var Id: Int = 0,
    var Name: String = "",
    var StartDateEpochSec: Long = 0,
    var EndDateEpochSec: Long = 0,
    var StartDateText: String = "",
    var EndDateText: String = "",
    var Category: String = "",
    var ColorCode: String = "",
    var IsWorking: Boolean = false,
    var StartDate: LocalDate? = null,
    var EndDate: LocalDate?= null
)