package com.tstudioz.fax.fme.database.models

import androidx.compose.runtime.remember
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.YearMonth

data class MonthData(
    val currentMonth: YearMonth, 
    val startMonth: YearMonth, 
    val endMonth: YearMonth, 
    val firstDayOfWeek: DayOfWeek)
