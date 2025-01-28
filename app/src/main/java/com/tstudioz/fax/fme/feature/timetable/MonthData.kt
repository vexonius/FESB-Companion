package com.tstudioz.fax.fme.feature.timetable

import java.time.DayOfWeek
import java.time.YearMonth

data class MonthData(
    val currentMonth: YearMonth, 
    val startMonth: YearMonth, 
    val endMonth: YearMonth, 
    val firstDayOfWeek: DayOfWeek)
