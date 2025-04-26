package com.tstudioz.fax.fme.feature.timetable.view.compose

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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.compose.theme_dark_primaryContainer
import com.tstudioz.fax.fme.database.models.Event
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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
    val column: Int = 0,
    val columnSpan: Int = 1,
    val columnTotal: Int = 1,
)

@Composable
fun BasicEvent(
    positionedEvent: PositionedEvent, modifier: Modifier = Modifier, onClick: (Event) -> Unit = {}
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
            .clickable { onClick(positionedEvent.event) }) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
        )

        Text(
            text = event.classroom,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .weight(1f),
        )
    }
}

private class EventDataModifier(val positionedEvent: PositionedEvent) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = positionedEvent
}

private fun Modifier.eventData(positionedEvent: PositionedEvent) = this.then(EventDataModifier(positionedEvent))

private val DayFormatter = DateTimeFormatter.ofPattern("d")

@Composable
fun BasicDayHeader(day: LocalDate) {
    val title = day.dayOfWeek.getDisplayName(TextStyle.SHORT, java.util.Locale.getDefault()).take(3).lowercase()
        .replaceFirstChar { it.uppercase() } + " " + day.format(DayFormatter)
    Text(
        text = title,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun ScheduleHeader(
    minDate: LocalDate,
    maxDate: LocalDate,
    dayWidth: Dp,
    modifier: Modifier = Modifier,
    dayHeader: @Composable (day: LocalDate) -> Unit = { BasicDayHeader(day = it) },
) {
    Row(
        modifier = modifier.background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
        repeat(numDays) { i ->
            Box(modifier = Modifier.width(dayWidth)) {
                dayHeader(minDate.plusDays(i.toLong()))
            }
        }
    }
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
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
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

private fun splitEvents(events: List<Event>): List<PositionedEvent> {
    return events.map { event ->
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

private fun List<PositionedEvent>.anyEventOverlapsWith(event: PositionedEvent): Boolean {
    return any { it.overlapsWith(event) }
}

private fun arrangeEvents(events: List<PositionedEvent>): List<PositionedEvent> {
    /**
     * Final list of events with their positions
     */
    val positionedEvents = mutableListOf<PositionedEvent>()

    /**
     * List of columns, each column is a list of events that are in that column
     */
    val columnsOfEvents: MutableList<MutableList<PositionedEvent>> = mutableListOf()

    fun moveElementsFromGroup() {
        columnsOfEvents.forEachIndexed { columnIndex, groupColumn ->
            groupColumn.forEach { event ->
                positionedEvents.add(event.copy(column = columnIndex, columnTotal = columnsOfEvents.size))
            }
        }
        columnsOfEvents.clear()
    }

    events.forEach { eventToAdd ->
        /**
         * Value is -1 if there is no free column, otherwise it's the index of the first free column*/
        var firstFreeColumn = -1

        /**
         * Number of free columns after the first free column*/
        var numberOfFreeColumns = 0

        //Goes trough all columns and checks if the event overlaps with any of the events in the column.
        //If it does, it checks how many columns are free after the first non-overlapping event.
        //So it ends up with the first free column and the number of free columns after it.

        for (i in 0 until columnsOfEvents.size) {
            if (columnsOfEvents[i].anyEventOverlapsWith(eventToAdd)) {
                if (firstFreeColumn < 0) continue else break
            }
            if (firstFreeColumn < 0) firstFreeColumn = i
            numberOfFreeColumns++
        }
        val noOverlap = numberOfFreeColumns == columnsOfEvents.size
        val overlapsWithAll = firstFreeColumn < 0
        when {
            noOverlap -> {
                moveElementsFromGroup()
                columnsOfEvents += mutableListOf(eventToAdd)
            }

            overlapsWithAll -> {
                columnsOfEvents += mutableListOf(eventToAdd)
                // Expand anything that spans into the previous column and doesn't overlap with this event
                for (columnIndex in 0 until columnsOfEvents.size - 1) {
                    val column = columnsOfEvents[columnIndex]
                    column.forEachIndexed { eIndex, eventInColumn ->
                        if (columnIndex + eventInColumn.columnSpan == columnsOfEvents.size - 1 && !eventInColumn.overlapsWith(
                                eventToAdd
                            )
                        ) {
                            column[eIndex] = eventInColumn.copy(columnSpan = +1)
                        }
                    }
                }
            }
            // At least one column free, add to first free column and expand to as many as possible
            else -> {
                columnsOfEvents[firstFreeColumn] += eventToAdd.copy(columnSpan = numberOfFreeColumns)
            }
        }
    }
    moveElementsFromGroup()
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
            positionedEvent = it, onClick = onClick
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
    var sidebarWidth by remember { mutableIntStateOf(95) }
    var headerHeight by remember { mutableIntStateOf(83) }
    BoxWithConstraints(modifier = modifier) {
        val dayWidth: Dp = when (daySize) {
            is ScheduleSize.FixedSize -> daySize.size
            is ScheduleSize.FixedCount -> with(LocalDensity.current) { ((constraints.maxWidth - sidebarWidth) / daySize.count).toDp() }
            is ScheduleSize.Adaptive -> with(LocalDensity.current) {
                maxOf(
                    ((constraints.maxWidth - sidebarWidth) / numDays).toDp(), daySize.minSize
                )
            }
        }
        val hourHeight: Dp = when (hourSize) {
            is ScheduleSize.FixedSize -> hourSize.size
            is ScheduleSize.FixedCount -> with(LocalDensity.current) { ((constraints.maxHeight - headerHeight) / hourSize.count).toDp() }
            is ScheduleSize.Adaptive -> with(LocalDensity.current) {
                maxOf(
                    ((constraints.maxHeight - headerHeight) / numHours).toDp(), hourSize.minSize
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
                    .onGloballyPositioned { headerHeight = it.size.height })
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
                        .onGloballyPositioned { sidebarWidth = it.size.width })
                BasicSchedule(
                    events = events,
                    eventContent = eventContent,
                    minDate = minDate,
                    maxDate = maxDate,
                    minDayTime = minTime,
                    maxDayTime = maxTime,
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
        BasicEvent(positionedEvent = it, onClick = { })
    },
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
    minDayTime: LocalTime = LocalTime.MIN,
    maxDayTime: LocalTime = LocalTime.MAX,
    dayWidth: Dp,
    hourHeight: Dp,
) {
    val numberOfDaysToShow = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val numberOfMinutesToShow = ChronoUnit.MINUTES.between(minDayTime, maxDayTime).toInt() + 1
    val numberOfHours = numberOfMinutesToShow / 60
    val positionedEvents = remember(events) {
        arrangeEvents(
            splitEvents(
                events.sortedBy(Event::start)
            )
        ).filter { it.end > minDayTime && it.start < maxDayTime }
    }
    val dividerColor = theme_dark_primaryContainer

    Layout(content = {
        positionedEvents.forEach { positionedEvent ->
            Box(modifier = Modifier.eventData(positionedEvent)) {
                eventContent(positionedEvent)
            }
        }
    }, modifier = modifier.drawBehind {
        drawScheduleBackground(
            minDayTime = minDayTime,
            numberOfDaysToShow = numberOfDaysToShow,
            numberOfHours = numberOfHours,
            hourHeight = hourHeight,
            dayWidth = dayWidth,
            dividerColor = dividerColor,
        )
    }) { measureables, _ ->
        val dayWidthPx = dayWidth.toPx()
        val hourHeightPx = hourHeight.toPx()
        val height = (hourHeightPx * numberOfMinutesToShow / 60f).roundToInt()
        val width = dayWidthPx.roundToInt() * numberOfDaysToShow

        layout(width, height) {
            measureables.forEach { measurable ->
                val splitEvent = measurable.parentData as PositionedEvent
                val apparentStartOfEvent = minOf(splitEvent.end, maxDayTime)
                val eventDurationMinutes = splitEvent.start.until(apparentStartOfEvent, ChronoUnit.MINUTES)

                val eventOffsetDays = minDate.until(splitEvent.date, ChronoUnit.DAYS).toInt()
                val eventOffsetMinutes = minDayTime.until(splitEvent.start, ChronoUnit.MINUTES).takeIf { it > 0 } ?: 0

                val eventColumnStart = splitEvent.column / splitEvent.columnTotal.toFloat()
                val eventColumnSpanPercent = splitEvent.columnSpan / splitEvent.columnTotal.toFloat()

                val eventHeight = (hourHeightPx * eventDurationMinutes / 60f).roundToInt()
                val eventWidth = (dayWidthPx * eventColumnSpanPercent).roundToInt()

                val eventYCoordinate = (hourHeightPx * eventOffsetMinutes / 60f).roundToInt()
                val eventXCoordinate = (dayWidthPx * (eventOffsetDays + eventColumnStart)).roundToInt()
                measurable.measure(
                    Constraints(
                        minWidth = eventWidth,
                        maxWidth = eventWidth,
                        minHeight = eventHeight,
                        maxHeight = eventHeight
                    )
                ).place(eventXCoordinate, eventYCoordinate)
            }
        }
    }
}

fun DrawScope.drawScheduleBackground(
    minDayTime: LocalTime,
    numberOfDaysToShow: Int,
    numberOfHours: Int,
    hourHeight: Dp,
    dayWidth: Dp,
    dividerColor: Color
) {
    val dayWidthPx = dayWidth.toPx()
    val hourHeightPx = hourHeight.toPx()
    val firstHour = minDayTime.truncatedTo(ChronoUnit.HOURS)
    val firstHourOffsetMinutes = minDayTime.until(firstHour, ChronoUnit.MINUTES)
    val firstHourOffset = (firstHourOffsetMinutes / 60f) * hourHeightPx

    val strokeWidthFullLine = 1.dp.toPx()
    val dashWidth = 2.dp.toPx()
    val dashHeight = 2.dp.toPx()
    val gapWidth = 2.dp.toPx()
    val pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(dashWidth, gapWidth), phase = 0f)

    repeat(numberOfHours) {
        drawLine(
            dividerColor,
            start = Offset(0f, (it + 0.5f) * hourHeightPx + firstHourOffset),
            end = Offset(size.width, (it + 0.5f) * hourHeightPx + firstHourOffset),
            strokeWidth = dashHeight,
            pathEffect = pathEffect,
            alpha = 0.5f,
        )
    }

    repeat(numberOfHours + 1) {
        drawLine(
            dividerColor,
            start = Offset(0f, it * hourHeightPx + firstHourOffset),
            end = Offset(size.width, it * hourHeightPx + firstHourOffset),
            strokeWidth = strokeWidthFullLine
        )
    }

    repeat(numberOfDaysToShow + 1) {
        drawLine(
            dividerColor,
            start = Offset(it * dayWidthPx, 0f),
            end = Offset(it * dayWidthPx, size.height),
            strokeWidth = strokeWidthFullLine
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BasicDayHeaderPreview() {
    BasicDayHeader(day = LocalDate.now())
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

@Preview(showBackground = true)
@Composable
fun BasicSidebarLabelPreview() {
    BasicSidebarLabel(time = LocalTime.NOON, Modifier.sizeIn(maxHeight = 64.dp))
}

@Preview(showBackground = true)
@Composable
fun ScheduleSidebarPreview() {
    ScheduleSidebar(hourHeight = 64.dp)
}

