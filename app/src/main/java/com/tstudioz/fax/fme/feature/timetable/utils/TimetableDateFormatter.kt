package com.tstudioz.fax.fme.feature.timetable.utils

import java.time.format.DateTimeFormatter

class TimetableDateFormatter {

    companion object {
        val hourFormatter = DateTimeFormatter.ofPattern("H")
        val dayFormatter = DateTimeFormatter.ofPattern("d. ")
    }

}