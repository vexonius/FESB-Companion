package com.tstudioz.fax.fme.database.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class TimeTableInfo (
    var Id: Int = 0,
    var Name: String = "",
    var StartDate: LocalDate? = null,
    var EndDate: LocalDate?= null,
    var StartDateText: String = "",
    var EndDateText: String = "",
    var Category: String = "",
    var ColorCode: Long = 0xFF191C1D,
    var IsWorking: Boolean = false,
)