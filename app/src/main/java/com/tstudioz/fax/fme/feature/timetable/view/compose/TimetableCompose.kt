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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.feature.timetable.MonthData
import com.tstudioz.fax.fme.feature.timetable.view.TimetableViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import org.jetbrains.annotations.Async.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun TimetableCompose(timetableViewModel: TimetableViewModel) {

    val showDayEvent = timetableViewModel.currentEventShown
    val shownWeekChooseMenu = timetableViewModel.shownWeekChooseMenu
    val lessonsToShow = timetableViewModel.events
    val shownWeek = timetableViewModel.mondayOfSelectedWeek
    val daysWithStuff = timetableViewModel.daysWithStuff
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
            event?.let {
                ModalBottomSheet(
                    sheetState = sheetStateEvent,
                    onDismissRequest = { hideEvent() },
                    windowInsets = WindowInsets(0.dp),
                    dragHandle = { },
                ) {
                    BottomInfoCompose(it)
                }
            }
            if (shownWeekChooseMenu.observeAsState(initial = false).value) {
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
                            daysWithStuff = daysWithStuff.value ?: emptyMap(),
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
        val mapped = lessonsToShow.observeAsState(emptyList()).value.onEach {
            it.color = colorResource(id = it.colorId)
        }/*.plus(
            Event(
                name = "Test 1",
                start = LocalDateTime.now().minusDays(3).minusHours(6),
                end = LocalDateTime.now().plusHours(1).minusDays(3).minusHours(6),
                classroom = " ",
                color = Color.Transparent,
                colorId = R.color.red_nice,
                groups = " ",
                professor = " ",
                recurringUntil = " ",
                id = "00",
                shortName = "ww"
            )).plus(
            Event(
                name = "Test 2",
                start = LocalDateTime.now().minusDays(3).minusHours(6),
                end = LocalDateTime.now().plusHours(2).minusDays(3).minusHours(6),
                classroom = " ",
                color = Color.Transparent,
                colorId = R.color.red_nice,
                groups = " ",
                professor = " ",
                recurringUntil = " ",
                id = "01",
                shortName = "ww"
            )).plus(
            Event(
                name = "Test 3",
                start = LocalDateTime.now().minusDays(3).minusHours(6),
                end = LocalDateTime.now().plusHours(3).minusDays(3).minusHours(6),
                classroom = " ",
                color = Color.Transparent,
                colorId = R.color.red_nice,
                groups = " ",
                professor = " ",
                recurringUntil = " ",
                id = "02",
                shortName = "ww"
            ),
        )*/
        val subExists: Boolean = mapped.any { it.start.dayOfWeek.value == 6 }
        val eventBefore8AM = mapped.any { it.start.toLocalTime().isBefore(LocalTime.of(8, 0)) }
        val eventAfter8PM = mapped.any { it.end.toLocalTime().isAfter(LocalTime.of(20, 0)) }
        val eventAfter9PM = mapped.any { it.end.toLocalTime().isAfter(LocalTime.of(21, 0)) }

        Schedule(
            events = mapped,
            eventContent = { posEvent ->
                BasicEventCustom(
                    positionedEvent = posEvent,
                    onClick = { showEvent(posEvent.event) }
                )
            },
            dayHeader = { day ->
                BasicDayHeaderCustom(day)
            },
            timeLabel = { time ->
                BasicSidebarLabelCustom(time)
            },
            minTime = if (eventBefore8AM) LocalTime.of(7, 0) else LocalTime.of(8, 0),
            maxTime = if (eventAfter9PM) LocalTime.of(22, 0)
            else if (eventAfter8PM) LocalTime.of(21, 0)
            else LocalTime.of(20, 0),
            minDate = shownWeek.observeAsState().value ?: LocalDate.now(),
            maxDate = (shownWeek.observeAsState().value ?: LocalDate.now()).plusDays(if (subExists) 5 else 4),
            onClick = { showEvent(it) })
    }
}

@Composable
fun BottomSheetCalendar(
    monthData: MonthData,
    daysWithStuff: Map<LocalDate, List<TimeTableInfo>>,
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
                daysWithStuff = daysWithStuff
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
                Text(stringResource(id = R.string.cancelChoosingWeek), color = Color.Gray)
            }
            TextButton({
                selection?.let {
                    fetchUserTimetable(it.date)
                    hideSheet()
                }
            }) {
                Text(stringResource(id = R.string.chooseChoosingWeek))
            }
        }
    }
}

private val HourFormatter = DateTimeFormatter.ofPattern("H")

@Composable
fun BasicSidebarLabelCustom(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter),
        textAlign = TextAlign.End,
        fontSize = 12.sp,
        lineHeight = 12.sp,
        modifier = modifier//.padding(vertical = 0.dp, horizontal = 8.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    )

}

private val DayFormatter = DateTimeFormatter.ofPattern("d")

@Composable
fun BasicDayHeaderCustom(day: LocalDate) {
    val dayOfWeek = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        .take(3).lowercase().replaceFirstChar { it.uppercase() }
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = day.format(DayFormatter) + ". ",
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
fun BasicEventCustom(
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
            .padding(2.dp)
            .clipToBounds()
            .clip(
                RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = bottomRadius,
                    bottomStart = bottomRadius,
                )
            )
            .background(Color(0xFF232323))
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

val orderOfPeriodColors = listOf(
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
    daysWithStuff: Map<LocalDate, List<TimeTableInfo>>,
    onClick: (CalendarDay) -> Unit = {},
) {
    val inActiveTextColor = Color.DarkGray
    val textColor = when (day.position) {
        DayPosition.MonthDate -> Color.Unspecified
        DayPosition.InDate, DayPosition.OutDate -> inActiveTextColor
    }
    val nextDay = day.date.plusDays(1)
    val prevDay = day.date.minusDays(1)
    val inPeriods = daysWithStuff[day.date] ?: emptyList()
    val rightPeriods = daysWithStuff[nextDay] ?: emptyList()
    val leftPeriods = daysWithStuff[prevDay] ?: emptyList()

    val dayColor = inPeriods.minByOrNull { orderOfPeriodColors.indexOf(it.category) }?.colorCode?.let { Color(it) }
    val leftColor = leftPeriods.minByOrNull { orderOfPeriodColors.indexOf(it.category) }?.colorCode?.let { Color(it) }
    val rightColor = rightPeriods.minByOrNull { orderOfPeriodColors.indexOf(it.category) }?.colorCode?.let { Color(it) }
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