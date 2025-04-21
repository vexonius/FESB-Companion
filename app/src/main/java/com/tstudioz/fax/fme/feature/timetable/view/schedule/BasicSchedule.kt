package com.tstudioz.fax.fme.feature.timetable.view.schedule

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.database.models.Event
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

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
    val dividerColor = MaterialTheme.colorScheme.outline

    Box {
        Layout(content = {
            val cornersRadius = 8.dp
            val glowingRadius = 10.dp
            positionedEvents.forEach { positionedEvent ->
                Box(
                    Modifier
                        .eventData(positionedEvent)
                        .padding(horizontal = 2.dp)
                        .drawBehind {
                            val canvasSize = size
                            drawContext.canvas.nativeCanvas.apply {
                                drawRoundRect(
                                    0f,
                                    0f,
                                    canvasSize.width, canvasSize.height,
                                    cornersRadius.toPx(), cornersRadius.toPx(),
                                    Paint().apply {
                                        isAntiAlias = true
                                        setShadowLayer(
                                            glowingRadius.toPx(),
                                            0f,
                                            0f,
                                            positionedEvent.event.color.toArgb()
                                        )
                                    }
                                )
                            }
                        }
                )
            }
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
                    val eventOffsetMinutes =
                        minDayTime.until(splitEvent.start, ChronoUnit.MINUTES).takeIf { it > 0 } ?: 0

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
        Layout(content = {
            positionedEvents.forEach { positionedEvent ->
                Box(
                    modifier = Modifier
                        .eventData(positionedEvent)
                        .padding(horizontal = 2.dp)
                ) {
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
                    val eventOffsetMinutes =
                        minDayTime.until(splitEvent.start, ChronoUnit.MINUTES).takeIf { it > 0 } ?: 0

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
}