package com.tstudioz.fax.fme.feature.timetable.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.contentColors
import com.tstudioz.fax.fme.compose.eventCardBackground
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.timetable.MonthData
import com.tstudioz.fax.fme.feature.timetable.utils.TimetableDateFormatter
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun TimetableCompose(timetableViewModel: TimetableViewModel) {

    val showDayEvent = timetableViewModel.currentEventShown
    val shownWeekChooseMenu = timetableViewModel.shownWeekChooseMenu.observeAsState(initial = false).value
    val lessonsToShow = timetableViewModel.events
    val shownWeek = timetableViewModel.mondayOfSelectedWeek
    val daysInPeriods = timetableViewModel.daysInPeriods.value ?: emptyMap()
    val monthData = timetableViewModel.monthData
    val fetchUserTimetable = { selectedDate: LocalDate -> timetableViewModel.fetchUserTimetable(selectedDate) }
    val showEvent = { it: Event -> timetableViewModel.showEvent(it) }
    val showWeekChooseMenu = { it: Boolean -> timetableViewModel.showWeekChooseMenu(it) }
    val hideEvent = { timetableViewModel.hideEvent() }
    val snackbarHostState = timetableViewModel.snackbarHostState

    val sheetStateEvent = rememberModalBottomSheetState()
    val sheetStateCalendar = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        sheetContent = {
            if (event != null) {
                ModalBottomSheet(
                    sheetState = sheetStateEvent,
                    onDismissRequest = { hideEvent() },
                    windowInsets = WindowInsets(0.dp),
                    dragHandle = { },
                ) {
                    EventBottomSheet(event)
                }
            } else if (shownWeekChooseMenu) {
                ModalBottomSheet(
                    sheetState = sheetStateCalendar,
                    onDismissRequest = { showWeekChooseMenu(false) },
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets(0.dp),
                    dragHandle = { },
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    monthData.value?.let {
                        BottomSheetCalendar(
                            monthData = it,
                            daysInPeriods = daysInPeriods,
                            fetchUserTimetable = fetchUserTimetable,
                            coroutineScope = coroutineScope,
                            hideSheet = {
                                coroutineScope.launch {
                                    sheetStateCalendar.hide()
                                    showWeekChooseMenu(false)
                                }
                            }
                        )
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
    ) {
        val mapped = lessonsToShow.observeAsState(emptyList()).value
        val subExists: Boolean = mapped.any { it.start.dayOfWeek.value == 6 }
        val eventBefore8AM = mapped.minByOrNull { it.start.toLocalTime() }
        val eventExistsBefore8AM = eventBefore8AM?.start?.toLocalTime()?.isBefore(LocalTime.of(8, 0))
        val eventAfter8PM = mapped.maxByOrNull { it.end.toLocalTime() }
        val eventExistsAfter8PM = eventAfter8PM?.end?.toLocalTime()?.isAfter(LocalTime.of(20, 0))
        val minTime = if (eventExistsBefore8AM == true) eventBefore8AM.start.toLocalTime() else LocalTime.of(8, 0)
        val maxTime = if (eventExistsAfter8PM == true) eventAfter8PM.end.toLocalTime() else LocalTime.of(20, 0)

        Schedule(
            events = mapped,
            eventContent = { posEvent ->
                EventCard(
                    positionedEvent = posEvent,
                    onClick = { showEvent(posEvent.event) }
                )
            },
            dayHeader = { day ->
                DayHeader(day)
            },
            timeLabel = { time ->
                SidebarLabel(time)
            },
            minTime = minTime,
            maxTime = maxTime,
            minDate = shownWeek.observeAsState().value ?: LocalDate.now(),
            maxDate = (shownWeek.observeAsState().value ?: LocalDate.now()).plusDays(if (subExists) 5 else 4),
            onClick = { showEvent(it) })
    }
}

@Composable
fun BottomSheetCalendar(
    monthData: MonthData,
    daysInPeriods: Map<LocalDate, TimeTableInfo>,
    fetchUserTimetable: (LocalDate) -> Unit,
    hideSheet: () -> Unit,
    coroutineScope: CoroutineScope,
) {
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
            startMonth = monthData.startMonth,
            endMonth = monthData.endMonth,
            firstVisibleMonth = monthData.currentMonth,
            firstDayOfWeek = monthData.firstDayOfWeek
        )
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
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        HorizontalCalendar(state = state, dayContent = { day ->
            Day(
                day,
                isSelected = selection == day,
                daysInPeriods = daysInPeriods
            ) { clicked ->
                selection = clicked.takeUnless { it == selection }
            }
        })
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 16.dp)
        ) {
            TextButton(hideSheet) {
                Text(stringResource(id = R.string.cancelChoosingWeek), color = MaterialTheme.contentColors.tertiary)
            }
            TextButton({
                selection?.let {
                    fetchUserTimetable(it.date)
                    hideSheet()
                }
            }) {
                Text(stringResource(id = R.string.chooseChoosingWeek), color = MaterialTheme.colorScheme.secondaryContainer)
            }
        }
    }
}

@Composable
fun SidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(TimetableDateFormatter.hourFormatter),
        textAlign = TextAlign.End,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )

}

@Composable
fun DayHeader(day: LocalDate) {
    val dayOfWeek = day
        .dayOfWeek
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
        .take(3)
        .lowercase()
        .replaceFirstChar { it.uppercase() }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = day.format(TimetableDateFormatter.dayFormatter),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
        Text(
            text = dayOfWeek,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun EventCard(
    positionedEvent: PositionedEvent,
    modifier: Modifier = Modifier,
    onClick: (Event) -> Unit = {}
) {
    val event = positionedEvent.event
    val topRadius =
        if (positionedEvent.splitType == SplitType.Start || positionedEvent.splitType == SplitType.Both) 0.dp else 8.dp
    val bottomRadius =
        if (positionedEvent.splitType == SplitType.End || positionedEvent.splitType == SplitType.Both) 0.dp else 8.dp

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 2.dp)
            .clipToBounds()
            .clip(
                RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = bottomRadius,
                    bottomStart = bottomRadius,
                )
            )
            .background(eventCardBackground)
            .clickable { onClick(positionedEvent.event) }
    ) {

        VerticalDivider(Modifier.padding(end = 2.dp), color = event.color, thickness = 4.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .padding(4.dp)
        ) {
            Text(
                text = event.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(0.7f, fill = false),
            )

            Text(
                text = event.classroom,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(0.3f)
                    .padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    daysInPeriods: Map<LocalDate, TimeTableInfo>,
    onClick: (CalendarDay) -> Unit = {},
) {
    val inactiveTextColor = Color.DarkGray
    val textColor = when (day.position) {
        DayPosition.MonthDate -> Color.Unspecified
        DayPosition.InDate, DayPosition.OutDate -> inactiveTextColor
    }
    val nextDay = day.date.plusDays(1)
    val prevDay = day.date.minusDays(1)
    val inPeriod = daysInPeriods[day.date]
    val leftPeriod = daysInPeriods[prevDay]
    val rightPeriod = daysInPeriods[nextDay]

    val dayColor = inPeriod?.colorCode?.let { Color(it) }
    val leftColor = leftPeriod?.colorCode?.let { Color(it) }
    val rightColor = rightPeriod?.colorCode?.let { Color(it) }
    val sameColorOnLeft = leftColor == dayColor && day.date.dayOfWeek != DayOfWeek.MONDAY
    val sameColorOnRight = rightColor == dayColor && day.date.dayOfWeek != DayOfWeek.SUNDAY
    val borderShape = remember(sameColorOnLeft, sameColorOnRight) {
        RoundedCornerShape(
            topStartPercent = if (sameColorOnLeft) 0 else 50,
            topEndPercent = if (sameColorOnRight) 0 else 50,
            bottomStartPercent = if (sameColorOnLeft) 0 else 50,
            bottomEndPercent = if (sameColorOnRight) 0 else 50
        )
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .background(shape = borderShape, color = dayColor ?: Color.Transparent)
            .clip(CircleShape)
            .clickable { onClick(day) }
            .border(2.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
            .padding(8.dp),
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