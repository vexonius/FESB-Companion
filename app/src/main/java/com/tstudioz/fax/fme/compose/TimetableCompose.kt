package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun TimetableCompose(mainViewModel: MainViewModel) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    BottomSheetScaffold(
        sheetContent = {
            if (mainViewModel.showDay.observeAsState(initial = false).value) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { mainViewModel.hideEvent() },
                    containerColor = mainViewModel.showDayEvent.observeAsState().value?.color ?: Color.Transparent,
                    windowInsets = WindowInsets(0.dp),
                    dragHandle = { },
                    shape = RectangleShape
                ) {
                    mainViewModel.showDayEvent.observeAsState().value?.let {
                        BottomInfoCompose(it)
                    }
                }
            }
            if (mainViewModel.shownWeekChooseMenu.observeAsState(initial = false).value) {
                ModalBottomSheet(sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets(0.dp),
                    onDismissRequest = {
                        mainViewModel.showWeekChooseMenu(false)
                    })
                {
                    Column(Modifier.padding(8.dp, 8.dp, 8.dp, 20.dp)) {
                        val currentMonth = remember { YearMonth.now() }
                        val startMonth = remember { currentMonth.minusMonths(100) }
                        val endMonth = remember { currentMonth.plusMonths(100) }
                        val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
                        val periods = mainViewModel.periods.value
                        var selection by remember {
                            mutableStateOf<CalendarDay?>(
                                CalendarDay(
                                    LocalDate.now(),
                                    DayPosition.MonthDate
                                )
                            )
                        }
                        val state = rememberCalendarState(
                            startMonth = startMonth,
                            endMonth = endMonth,
                            firstVisibleMonth = currentMonth,
                            firstDayOfWeek = firstDayOfWeek
                        )
                        val coroutineScope = rememberCoroutineScope()
                        SimpleCalendarTitle(
                            modifier = Modifier.fillMaxWidth(),
                            currentMonth = state.firstVisibleMonth.yearMonth,
                            goToPrevious = {
                                coroutineScope.launch {
                                    state.scrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                                }
                            },
                            goToNext = {
                                coroutineScope.launch {
                                    state.scrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                                }
                            },
                        )
                        Spacer(modifier = Modifier.padding(0.dp, 5.dp))
                        HorizontalCalendar(state = state, dayContent = { day ->
                            Day(day, isSelected = selection == day, periods = periods ?: emptyList()) { clicked ->
                                selection = if (clicked == selection) {
                                    null
                                } else {
                                    clicked
                                }

                            }
                        })
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 5.dp)
                        ) {
                            Button(onClick = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    delay(300)
                                    mainViewModel.showWeekChooseMenu(false)
                                }
                            }) {
                                Text(text = "Odustani")
                            }
                            Button(onClick = {
                                selection?.let {
                                    val start = it.date.dayOfWeek.value
                                    val startDate = it.date.minusDays((start - 1).toLong())
                                    val endDate = it.date.plusDays(7 - start.toLong())
                                    mainViewModel.fetchUserTimetable(
                                        startDate = startDate,
                                        endDate = endDate,
                                        shownWeekMonday = startDate
                                    )
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        delay(300)
                                        mainViewModel.showWeekChooseMenu(false)
                                    }
                                }
                            }) {
                                Text(text = "Odaberi")
                            }
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
    ) {
        Column {
            val mapped = mainViewModel.lessonsToShow.observeAsState(emptyList()).value.onEach {
                it.color = colorResource(id = it.colorId)
            }
            val subExists: Boolean = mapped.any { it.start.dayOfWeek.value == 6 }
            val eventBefore8AM = mapped.any { it.start.toLocalTime().isBefore(LocalTime.of(8, 0)) }
            val eventAfter8PM = mapped.any { it.end.toLocalTime().isAfter(LocalTime.of(20, 0)) }
            val eventAfter9PM = mapped.any { it.end.toLocalTime().isAfter(LocalTime.of(21, 0)) }

            Schedule(
                events = mapped,
                minTime = if (eventBefore8AM) LocalTime.of(7, 0) else LocalTime.of(8, 0),
                maxTime = if (eventAfter9PM) LocalTime.of(22, 0)
                else if (eventAfter8PM) LocalTime.of(21, 0)
                else LocalTime.of(20, 0),
                minDate = mainViewModel.shownWeek.observeAsState().value ?: LocalDate.now(),
                maxDate = (mainViewModel.shownWeek.observeAsState().value
                    ?: LocalDate.now()).plusDays(if (subExists) 5 else 4),
                onClick = { event ->
                    mainViewModel.showEvent(event)
                }
            )

        }
    }
}

val orderOfPeriodImportance = listOf(
    "Narančasta",
    "Plava",
    "Yellow",
    "Crvena",
    "Ljubičasta",
    "Zelena",
    "Siva",
    "Bijela",
)

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    periods: List<TimeTableInfo>,
    onClick: (CalendarDay) -> Unit = {},
) {
    val selectedItemColor = MaterialTheme.colorScheme.secondary
    val inActiveTextColor = Color.DarkGray
    val textColor = when (day.position) {
        DayPosition.MonthDate -> Color.Unspecified
        DayPosition.InDate, DayPosition.OutDate -> inActiveTextColor
    }
    val inPeriods = mutableListOf<TimeTableInfo>()

    periods.filter { (it.startDate?.compareTo(day.date) ?: 1) <= 0 && (it.endDate?.compareTo(day.date) ?: -1) >= 0 }
        .forEach {
            inPeriods.add(it)
        }
    val dayColor = inPeriods.minByOrNull { orderOfPeriodImportance.indexOf(it.category) }?.colorCode?.let { Color(it) }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.dp, 3.dp)
            .border(
                width = if (isSelected) 1.dp else 1.dp,
                color = if (isSelected) selectedItemColor else (dayColor ?: MaterialTheme.colorScheme.background),
            )
            .background(color = (dayColor ?: MaterialTheme.colorScheme.background))
            .clickable { onClick(day) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}