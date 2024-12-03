package com.tstudioz.fax.fme.feature.timetable.view

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.BottomInfoCompose
import com.tstudioz.fax.fme.compose.Schedule
import com.tstudioz.fax.fme.compose.SimpleCalendarTitle
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun TimetableCompose(timetableViewModel: TimetableViewModel) {

    val showDayEvent = timetableViewModel.currentEventShown
    val shownWeekChooseMenu = timetableViewModel.shownWeekChooseMenu
    val lessonsToShow = timetableViewModel.events
    val shownWeek = timetableViewModel.mondayOfSelectedWeek
    val periods = timetableViewModel.periods
    val monthData = timetableViewModel.monthData
    val fetchUserTimetable = { selectedDate: LocalDate -> timetableViewModel.fetchUserTimetable(selectedDate) }
    val showEvent = { it: Event -> timetableViewModel.showEvent(it) }
    val showWeekChooseMenu = { it: Boolean -> timetableViewModel.showWeekChooseMenu(it) }
    val hideEvent = { timetableViewModel.hideEvent() }
    val snackbarHostState = timetableViewModel.snackbarHostState

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val event = showDayEvent.observeAsState().value
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                timetableViewModel.resetToCurrentWeek()
                timetableViewModel.fetchUserTimetable()
            }
            else -> {}
        }
    }

    BottomSheetScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        sheetContent = {
            event?.let {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { hideEvent() },
                    containerColor = event.color,
                    dragHandle = { },
                    shape = RectangleShape
                ) {
                    BottomInfoCompose(it)
                }
            }

            if (shownWeekChooseMenu.observeAsState(initial = false).value) {
                ModalBottomSheet(sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = {
                        showWeekChooseMenu(false)
                    })
                {
                    Column(Modifier.padding(8.dp, 8.dp, 8.dp, 20.dp)) {
                        var selection by remember {
                            mutableStateOf<CalendarDay?>(
                                CalendarDay(
                                    LocalDate.now(),
                                    DayPosition.MonthDate
                                )
                            )
                        }
                        val state = rememberCalendarState(
                            startMonth = monthData.value?.startMonth ?: YearMonth.now(),
                            endMonth = monthData.value?.endMonth ?: YearMonth.now(),
                            firstVisibleMonth = monthData.value?.currentMonth ?: YearMonth.now(),
                            firstDayOfWeek = monthData.value?.firstDayOfWeek ?: firstDayOfWeekFromLocale()
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
                            Day(day, isSelected = selection == day, periods = periods.value ?: emptyList()) { clicked ->
                                selection = clicked.takeUnless { it == selection }
                            }
                        })
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp, 16.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        showWeekChooseMenu(false)
                                    }
                                }) {
                                Text(text = stringResource(id = R.string.cancelChoosingWeek), color = Color.Gray)
                            }
                            TextButton(onClick = {
                                selection?.let {
                                    fetchUserTimetable(it.date)
                                    coroutineScope.launch {
                                        sheetState.hide()
                                        showWeekChooseMenu(false)
                                    }
                                }
                            }) {
                                Text(text = stringResource(id = R.string.chooseChoosingWeek))
                            }
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
    ) {
        Column {
            val mapped = lessonsToShow.observeAsState(emptyList()).value.onEach {
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
                minDate = shownWeek.observeAsState().value ?: LocalDate.now(),
                maxDate = (shownWeek.observeAsState().value ?: LocalDate.now()).plusDays(if (subExists) 5 else 4),
                onClick = { showEvent(it) })

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
            .background(
                shape = CircleShape, color = dayColor ?: Color.Transparent
            )
            .clip(CircleShape)
            .border(2.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
            .padding(8.dp)
            .clickable { onClick(day) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}