package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimetableType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt


@JvmInline
value class SplitType private constructor(val value: Int) {
    companion object {
        val None = SplitType(0)
        val Start = SplitType(1)
        val End = SplitType(2)
        val Both = SplitType(3)
    }
}

data class PositionedEvent(
    val event: Event,
    val splitType: SplitType,
    val date: LocalDate,
    val start: LocalTime,
    val end: LocalTime,
    val col: Int = 0,
    val colSpan: Int = 1,
    val colTotal: Int = 1,
)

val EventTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun BasicEvent(
    positionedEvent: PositionedEvent,
    modifier: Modifier = Modifier,
    onClick : (Event) -> Unit = {}
) {
    val event = positionedEvent.event
    val topRadius =
        if (positionedEvent.splitType == SplitType.Start || positionedEvent.splitType == SplitType.Both) 0.dp else 8.dp
    val bottomRadius =
        if (positionedEvent.splitType == SplitType.End || positionedEvent.splitType == SplitType.Both) 0.dp else 8.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
            .clipToBounds()
            .background(
                event.color,
                shape = RoundedCornerShape(
                    topStart = topRadius,
                    topEnd = topRadius,
                    bottomEnd = bottomRadius,
                    bottomStart = bottomRadius,
                )
            )
            .padding(4.dp)
            .clickable { onClick(positionedEvent.event) }
    ) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = event.classroom,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        )
    }
}

private class EventDataModifier(
    val positionedEvent: PositionedEvent,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = positionedEvent
}

private fun Modifier.eventData(positionedEvent: PositionedEvent) =
    this.then(EventDataModifier(positionedEvent))

private val DayFormatter = DateTimeFormatter.ofPattern("E d.M.")

@Composable
fun BasicDayHeader(
    day: LocalDate,
    modifier: Modifier = Modifier,
) {
    Text(
        text = day.format(DayFormatter).replaceFirstChar { day.format(DayFormatter)[0].uppercase() },
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview(showBackground = true)
@Composable
fun BasicDayHeaderPreview() {
    BasicDayHeader(day = LocalDate.now())
}

@Composable
fun ScheduleHeader(
    minDate: LocalDate,
    maxDate: LocalDate,
    dayWidth: Dp,
    modifier: Modifier = Modifier,
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
) {
    Row(modifier = modifier) {
        val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
        repeat(numDays) { i ->
            Box(modifier = Modifier.width(dayWidth)) {
                dayHeader(minDate.plusDays(i.toLong()))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleHeaderPreview() {
    ScheduleHeader(
        minDate = LocalDate.now(),
        maxDate = LocalDate.now().plusDays(5),
        dayWidth = 256.dp,
    )
}

private val HourFormatter = DateTimeFormatter.ofPattern("H")

@Composable
fun BasicSidebarLabel(
    time: LocalTime,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time.format(HourFormatter),
        textAlign = TextAlign.End,
        modifier = modifier
            .fillMaxHeight()
            .padding(vertical = 0.dp, horizontal = 8.dp),
        color = MaterialTheme.colorScheme.onSurface,
    )

}

@Preview(showBackground = true)
@Composable
fun BasicSidebarLabelPreview() {
    BasicSidebarLabel(time = LocalTime.NOON, Modifier.sizeIn(maxHeight = 64.dp))
}

@Composable
fun ScheduleSidebar(
    hourHeight: Dp,
    modifier: Modifier = Modifier,
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    label: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val numHours = numMinutes / 60
    val firstHour = minTime.truncatedTo(ChronoUnit.HOURS)
    val firstHourOffsetMinutes =
        if (firstHour == minTime) 0 else ChronoUnit.MINUTES.between(minTime, firstHour.plusHours(1))
    val firstHourOffset = hourHeight * (firstHourOffsetMinutes / 60f)
    val startTime = if (firstHour == minTime) firstHour else firstHour.plusHours(1)
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(firstHourOffset))
        repeat(numHours) { i ->
            Box(modifier = Modifier.height(hourHeight)) {
                label(startTime.plusHours(i.toLong()))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleSidebarPreview() {
    ScheduleSidebar(hourHeight = 64.dp)
}

private fun splitEvents(events: List<Event>): List<PositionedEvent> {
    return events
        .map { event ->
            val startDate = event.start.toLocalDate()
            val endDate = event.end.toLocalDate()
            if (startDate == endDate) {
                listOf(
                    PositionedEvent(
                        event,
                        SplitType.None,
                        event.start.toLocalDate(),
                        event.start.toLocalTime(),
                        event.end.toLocalTime()
                    )
                )
            } else {
                val days = ChronoUnit.DAYS.between(startDate, endDate)
                val splitEvents = mutableListOf<PositionedEvent>()
                for (i in 0..days) {
                    val date = startDate.plusDays(i)
                    splitEvents += PositionedEvent(
                        event,
                        splitType = if (date == startDate) SplitType.End else if (date == endDate) SplitType.Start else SplitType.Both,
                        date = date,
                        start = if (date == startDate) event.start.toLocalTime() else LocalTime.MIN,
                        end = if (date == endDate) event.end.toLocalTime() else LocalTime.MAX,
                    )
                }
                splitEvents
            }
        }.flatten()
}

private fun PositionedEvent.overlapsWith(other: PositionedEvent): Boolean {
    return date == other.date && start < other.end && end > other.start
}

private fun List<PositionedEvent>.timesOverlapWith(event: PositionedEvent): Boolean {
    return any { it.overlapsWith(event) }
}

private fun arrangeEvents(events: List<PositionedEvent>): List<PositionedEvent> {
    val positionedEvents = mutableListOf<PositionedEvent>()
    val groupEvents: MutableList<MutableList<PositionedEvent>> = mutableListOf()

    fun resetGroup() {
        groupEvents.forEachIndexed { colIndex, col ->
            col.forEach { e ->
                positionedEvents.add(e.copy(col = colIndex, colTotal = groupEvents.size))
            }
        }
        groupEvents.clear()
    }

    events.forEach { event ->
        var firstFreeCol = -1
        var numFreeCol = 0
        for (i in 0 until groupEvents.size) {
            val col = groupEvents[i]
            if (col.timesOverlapWith(event)) {
                if (firstFreeCol < 0) continue else break
            }
            if (firstFreeCol < 0) firstFreeCol = i
            numFreeCol++
        }

        when {
            // Overlaps with all, add a new column
            firstFreeCol < 0 -> {
                groupEvents += mutableListOf(event)
                // Expand anything that spans into the previous column and doesn't overlap with this event
                for (ci in 0 until groupEvents.size - 1) {
                    val col = groupEvents[ci]
                    col.forEachIndexed { ei, e ->
                        if (ci + e.colSpan == groupEvents.size - 1 && !e.overlapsWith(event)) {
                            col[ei] = e.copy(colSpan = e.colSpan + 1)
                        }
                    }
                }
            }
            // No overlap with any, start a new group
            numFreeCol == groupEvents.size -> {
                resetGroup()
                groupEvents += mutableListOf(event)
            }
            // At least one column free, add to first free column and expand to as many as possible
            else -> {
                groupEvents[firstFreeCol] += event.copy(colSpan = numFreeCol)
            }
        }
    }
    resetGroup()
    return positionedEvents
}

sealed class ScheduleSize {
    class FixedSize(val size: Dp) : ScheduleSize()
    class FixedCount(val count: Float) : ScheduleSize() {
        constructor(count: Int) : this(count.toFloat())
    }

    class Adaptive(val minSize: Dp) : ScheduleSize()
}

@Composable
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onClick: (Event) -> Unit = { },
    eventContent: @Composable (positionedEvent: PositionedEvent) -> Unit = {
        BasicEvent(
            positionedEvent = it,
            onClick = onClick
        )
    },
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
    timeLabel: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    daySize: ScheduleSize = ScheduleSize.Adaptive(64.dp),//FixedSize(64.dp),
    hourSize: ScheduleSize = ScheduleSize.Adaptive(44.dp),
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val numHours = numMinutes.toFloat() / 60f
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    var sidebarWidth by remember { mutableIntStateOf(0) }
    var headerHeight by remember { mutableIntStateOf(0) }
    BoxWithConstraints(modifier = modifier) {
        val dayWidth: Dp = when (daySize) {
            is ScheduleSize.FixedSize -> daySize.size
            is ScheduleSize.FixedCount -> with(LocalDensity.current) { ((constraints.maxWidth - sidebarWidth) / daySize.count).toDp() }
            is ScheduleSize.Adaptive -> with(LocalDensity.current) {
                maxOf(
                    ((constraints.maxWidth - sidebarWidth) / numDays).toDp(),
                    daySize.minSize
                )
            }

        }
        val hourHeight: Dp = when (hourSize) {
            is ScheduleSize.FixedSize -> hourSize.size
            is ScheduleSize.FixedCount -> with(LocalDensity.current) { ((constraints.maxHeight - headerHeight) / hourSize.count).toDp() }
            is ScheduleSize.Adaptive -> with(LocalDensity.current) {
                maxOf(
                    ((constraints.maxHeight - headerHeight) / numHours).toDp(),
                    hourSize.minSize
                )
            }

        }
        Column(modifier = modifier) {
            ScheduleHeader(
                minDate = minDate,
                maxDate = maxDate,
                dayWidth = dayWidth,
                dayHeader = dayHeader,
                modifier = Modifier
                    .padding(start = with(LocalDensity.current) { sidebarWidth.toDp() })
                    .horizontalScroll(horizontalScrollState)
                    .onGloballyPositioned { headerHeight = it.size.height }
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.Start)
            ) {
                ScheduleSidebar(
                    hourHeight = hourHeight,
                    minTime = minTime,
                    maxTime = maxTime,
                    label = timeLabel,
                    modifier = Modifier
                        .verticalScroll(verticalScrollState)
                        .onGloballyPositioned { sidebarWidth = it.size.width }
                )
                BasicSchedule(
                    events = events,
                    eventContent = eventContent,
                    minDate = minDate,
                    maxDate = maxDate,
                    minTime = minTime,
                    maxTime = maxTime,
                    dayWidth = dayWidth,
                    hourHeight = hourHeight,
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(verticalScrollState)
                        .horizontalScroll(horizontalScrollState)
                )
            }
        }
    }
}

@Composable
fun BasicSchedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (positionedEvent: PositionedEvent) -> Unit = {
        BasicEvent(
            positionedEvent = it,
            onClick = { }
        )
    },
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
    minTime: LocalTime = LocalTime.MIN,
    maxTime: LocalTime = LocalTime.MAX,
    dayWidth: Dp,
    hourHeight: Dp,
) {
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val numMinutes = ChronoUnit.MINUTES.between(minTime, maxTime).toInt() + 1
    val numHours = numMinutes / 60
    val dividerColor = Color.DarkGray
    val positionedEvents =
        remember(events) { arrangeEvents(splitEvents(events.sortedBy(Event::start))).filter { it.end > minTime && it.start < maxTime } }
    Layout(
        content = {
            positionedEvents.forEach { positionedEvent ->
                Box(modifier = Modifier.eventData(positionedEvent)) {
                    eventContent(positionedEvent)
                }
            }
        },
        modifier = modifier
            .drawBehind {
                val firstHour = minTime.truncatedTo(ChronoUnit.HOURS)
                val firstHourOffsetMinutes =
                    if (firstHour == minTime) 0 else ChronoUnit.MINUTES.between(
                        minTime,
                        firstHour.plusHours(1)
                    )
                val firstHourOffset = (firstHourOffsetMinutes / 60f) * hourHeight.toPx()
                repeat(numHours) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, it * hourHeight.toPx() + firstHourOffset),
                        end = Offset(size.width, it * hourHeight.toPx() + firstHourOffset),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                repeat(numDays - 1) {
                    drawLine(
                        dividerColor,
                        start = Offset((it + 1) * dayWidth.toPx() - 3, 0f),
                        end = Offset((it + 1) * dayWidth.toPx() - 3, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
    ) { measureables, constraints ->
        val height = (hourHeight.toPx() * (numMinutes / 60f)).roundToInt()
        val width = dayWidth.roundToPx() * numDays
        val placeablesWithEvents = measureables.map { measurable ->
            val splitEvent = measurable.parentData as PositionedEvent
            val eventDurationMinutes =
                ChronoUnit.MINUTES.between(splitEvent.start, minOf(splitEvent.end, maxTime))
            val eventHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
            val eventWidth =
                ((splitEvent.colSpan.toFloat() / splitEvent.colTotal.toFloat()) * dayWidth.toPx()).roundToInt()
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = eventWidth,
                    maxWidth = eventWidth,
                    minHeight = eventHeight,
                    maxHeight = eventHeight
                )
            )
            Pair(placeable, splitEvent)
        }
        layout(width, height) {
            placeablesWithEvents.forEach { (placeable, splitEvent) ->
                val eventOffsetMinutes = if (splitEvent.start > minTime) ChronoUnit.MINUTES.between(
                    minTime,
                    splitEvent.start
                ) else 0
                val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val eventOffsetDays = ChronoUnit.DAYS.between(minDate, splitEvent.date).toInt()
                val eventX = eventOffsetDays * dayWidth.roundToPx() +
                        (splitEvent.col * (dayWidth.toPx() / splitEvent.colTotal.toFloat())).roundToInt()
                placeable.place(eventX, eventY)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventPreview(
    @PreviewParameter(EventsProvider::class) event: Event,
) {
    BasicEvent(
        PositionedEvent(
            event,
            SplitType.Start,
            event.start.toLocalDate(),
            event.start.toLocalTime(),
            event.end.toLocalTime()
        ),
        modifier = Modifier.sizeIn(maxHeight = 64.dp),
        onClick = { }
    )
}

private val sampleEvents = listOf(
    Event(
        name = "Google I/O Keynote",
        shortName = "Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T09:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:00:00"),
        id = "keynote",
        description = "Tune in to find out about how we're furthering our mission to organize the world’s information and make it universally accessible and useful.",
    ),
    Event(
        name = "Developer Keynote",
        shortName = "Dev Keynote",
        color = Color(0xFFAFBBF2),
        start = LocalDateTime.parse("2021-05-18T09:00:00"),
        end = LocalDateTime.parse("2021-05-18T10:00:00"),
        id = "dev-keynote",
        description = "Learn about the latest updates to our developer products and platforms from Google Developers.",
    ),
    Event(
        name = "What's new in Android",
        shortName = "Android",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-18T10:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:00:00"),
        id = "android",
        description = "In this Keynote, Chet Haase, Dan Sandler, and Romain Guy discuss the latest Android features and enhancements for developers.",
    ),
    Event(
        name = "What's new in Material Design",
        shortName = "Material",
        color = Color(0xFF6DD3CE),
        start = LocalDateTime.parse("2021-05-18T11:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:45:00"),
        id = "android",
        description = "Learn about the latest design improvements to help you build personal dynamic experiences with Material Design.",
    ),
    Event(
        name = "What's new in Machine Learning",
        shortName = "ML",
        color = Color(0xFFF4BFDB),
        start = LocalDateTime.parse("2021-05-18T10:00:00"),
        end = LocalDateTime.parse("2021-05-18T11:00:00"),
        id = "android",
        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
    ),
    Event(
        name = "What's new in Machine Learning",
        shortName = "ML",
        color = Color(0xFFF4BFDB),
        start = LocalDateTime.parse("2021-05-18T10:30:00"),
        end = LocalDateTime.parse("2021-05-18T11:30:00"),
        id = "android",
        description = "Learn about the latest and greatest in ML from Google. We’ll cover what’s available to developers when it comes to creating, understanding, and deploying models for a variety of different applications.",
    ),
    Event(
        name = "Jetpack Compose Basics",
        shortName = "Jetpack",
        color = Color(0xFF1B998B),
        start = LocalDateTime.parse("2021-05-20T12:00:00"),
        end = LocalDateTime.parse("2021-05-20T13:00:00"),
        id = "android",
        description = "This Workshop will take you through the basics of building your first app with Jetpack Compose, Android's new modern UI toolkit that simplifies and accelerates UI development on Android.",
    ),
)
private val sampleEvents2 = listOf(
    Event(
        id = "0",
        name = "Ponedjeljak",
        shortName = "P",
        color = Color.Red,
        colorId = 2131099687,
        professor = "matko",
        eventType = TimetableType.OTHER,
        groups = "nemaa",
        classroom = "C502",
        start = LocalDateTime.parse("2024-05-02T10:15"),
        end = LocalDateTime.parse("2024-05-02T11:00"),
        description = "C502"
    ),
    Event(
        id = "532059",
        name = "Kriptografija i mrežna sigurnost",
        shortName = "KIMS",
        color = Color.Yellow,
        colorId = 2131099687,
        professor = "Čagalj Mario",
        eventType = TimetableType.PREDAVANJE,
        groups = "",
        classroom = "C501",
        start = LocalDateTime.parse("2024-04-29T10:15"),
        end = LocalDateTime.parse("2024-04-29T12:00"),
        description = "C501"
    ),
    Event(
        id = "534198",
        name = "Metode optimizacije",
        shortName = "MO",
        color = Color.Green,
        colorId = 2131100480,
        professor = "Bašić Martina",
        eventType = TimetableType.LABORATORIJSKA_VJEZBA,
        groups = "Grupa 1,",
        classroom = "B420",
        start = LocalDateTime.parse("2024-04-29T18:30"),
        end = LocalDateTime.parse("2024-04-29T20:00"),
        description = "B420"
    ),
    Event(
        id = "532144",
        name = "Podržano strojno učenje",
        shortName = "PSU",
        color = Color.Blue,
        colorId = 2131099687,
        professor = "Vasilj Josip",
        eventType = TimetableType.PREDAVANJE,
        groups = "",
        classroom = "A243",
        start = LocalDateTime.parse("2024-04-30T08:15"),
        end = LocalDateTime.parse("2024-04-30T10:00"),
        description = "A243"
    ),
    Event(
        id = "532084",
        name = "Metode optimizacije",
        shortName = "MO",
        color = Color.Blue,
        colorId = 2131099687,
        professor = "Marasović Jadranka",
        eventType = TimetableType.PREDAVANJE,
        groups = "",
        classroom = "C502",
        start = LocalDateTime.parse("2024-04-30T10:15"),
        end = LocalDateTime.parse("2024-04-30T12:00"),
        description = "C502"
    ),
    Event(
        id = "532120",
        name = "IP komunikacije",
        shortName = "IK",
        color = Color.Blue,
        colorId = 2131099687,
        professor = "Russo Mladen",
        eventType = TimetableType.PREDAVANJE,
        groups = "",
        classroom = "A105",
        start = LocalDateTime.parse("2024-04-30T12:15"),
        end = LocalDateTime.parse("2024-04-30T14:00"),
        description = "A105"
    ),
    Event(
        id = "538989",
        name = "Podržano strojno učenje",
        shortName = "PSU",
        color = Color.Blue,
        colorId = 2131100480,
        professor = "Vasilj Josip",
        eventType = TimetableType.LABORATORIJSKA_VJEZBA,
        groups = "Grupa 1,",
        classroom = "A507",
        start = LocalDateTime.parse("2024-05-02T10:00"),
        end = LocalDateTime.parse("2024-05-02T12:15"),
        description = "A507"
    ),
    Event(
        id = "535595",
        name = "Jezici i prevoditelji",
        shortName = "JIP",
        color = Color.Blue,
        colorId = 2131100480,
        professor = "Sikora Marjan",
        eventType = TimetableType.LABORATORIJSKA_VJEZBA,
        groups = "Grupa 1,",
        classroom = "B526",
        start = LocalDateTime.parse("2024-05-02T08:30"),
        end = LocalDateTime.parse("2024-05-02T10:00"),
        description = "B526"
    ),
    Event(
        id = "535336",
        name = "IP komunikacije",
        shortName = "IK",
        color = Color.Blue,
        colorId = 2131100480,
        professor = "Meter Davor",
        eventType = TimetableType.LABORATORIJSKA_VJEZBA,
        groups = "Grupa 1,",
        classroom = "B526",
        start = LocalDateTime.parse("2024-05-03T08:00"),
        end = LocalDateTime.parse("2024-05-03T09:30"),
        description = "B526"
    ),
)

class EventsProvider : PreviewParameterProvider<Event> {
    override val values = sampleEvents2.asSequence()
}

@Preview(showBackground = true)
@Composable
fun SchedulePreview() {
    Schedule(
        minTime = LocalTime.of(8, 0),
        maxTime = LocalTime.of(20, 0),
        events = sampleEvents2)
}
