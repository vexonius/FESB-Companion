package com.tstudioz.fax.fme.feature.timetable.view.schedule

import com.tstudioz.fax.fme.database.models.Event
import java.time.LocalTime
import java.time.temporal.ChronoUnit


fun splitEvents(events: List<Event>): List<PositionedEvent> {
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

fun arrangeEvents(events: List<PositionedEvent>): List<PositionedEvent> {
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