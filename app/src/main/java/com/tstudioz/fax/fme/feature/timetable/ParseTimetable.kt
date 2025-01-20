package com.tstudioz.fax.fme.feature.timetable

import androidx.compose.ui.graphics.toArgb
import com.google.gson.GsonBuilder
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Recurring
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.database.models.TimetableType
import com.tstudioz.fax.fme.database.models.color
import com.tstudioz.fax.fme.util.ColorDeserializer
import com.tstudioz.fax.fme.util.LocalDateDeserializer
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


fun parseTimetable(body: String): List<Event> {
    val events = ArrayList<Event>()

    val doc = Jsoup.parse(body)
    val elements = doc.select("div.event")

    elements.let {
        for (e in elements) {
            val id = e.attr("data-id").toInt()
            val startdate = e.attr("data-startsdate").toString()
            val starth = e.attr("data-startshour").toInt()
            val startmin = e.attr("data-startsmin").toInt()
            val enddate = e.attr("data-endsdate").toString()
            val endh = e.attr("data-endshour").toInt()
            val endmin = e.attr("data-endsmin").toInt()
            val type = setType(e.selectFirst("span.groupCategory")?.text()?.split(",")?.get(0) ?: "")
            val name = e.selectFirst("span.name.normal")?.text()
                ?: e.selectFirst("div.popup > div.eventContent > div.header > div > span.title")?.text()
                ?: ""
            val group = e.select("span.group.normal").first()?.text() ?: ""
            val studycode = e.selectFirst("span.studyCode")?.text() ?: ""
            val room = e.selectFirst("div.eventContent > div.eventInfo > span.resource")?.text() ?: ""
            val detailTime = e.selectFirst("div.detailItem.datetime")?.text() ?: ""
            val professor = e.selectFirst("div.detailItem.user")?.text() ?: ""
            val repetsType = parseRecurring(e.selectFirst("div.recurring > span.type > span"))
            val isItRecurring = !(repetsType == Recurring.ONCE || repetsType == Recurring.UNDEFINED)

            val repeatsUntil = e.selectFirst("span.repeat")?.text() ?: ""

            events.add(
                Event(
                    id = id.toString(),
                    name = name,
                    shortName = makeAcronym(name),
                    professor = professor,
                    eventType = type,
                    groups = group,
                    classroom = room,
                    colorId = type.color().toArgb(),
                    color = type.color(),
                    start = LocalDateTime.of(
                        LocalDate.parse(startdate),
                        LocalTime.of(starth, startmin)
                    ),
                    end = LocalDateTime.of(
                        LocalDate.parse(enddate),
                        LocalTime.of(endh, endmin)
                    ),
                    description = detailTime,
                    recurring = isItRecurring,
                    recurringType = repetsType,
                    recurringUntil = repeatsUntil,
                    studyCode = studycode
                )
            )
        }
    }

    return events
}

private fun setType(typeValue: String): TimetableType = TimetableType
    .entries.firstOrNull { it.value == typeValue } ?: TimetableType.OTHER

fun makeAcronym(name: String): String {
    val acronym = StringBuilder()
    if (name.isNotEmpty() && name.contains(" ")) {
        val nameSplit = name.split(" ").toTypedArray()
        for (str in nameSplit)
            acronym.append(str[0])
        return acronym.toString().uppercase()
    }

    return name
}
// TODO: Improve sometime in the future
val periodColors = mapOf(
    "Bijela" to 1,
    "Siva" to 2,
    "Zelena" to 3,
    "Ljubičasta" to 4,
    "Crvena" to 5,
    "Yellow" to 6,
    "Plava" to 7,
    "Narančasta" to 8,
)

// TODO: Improve parsing or replace with API in the future
fun parseTimetableInfo(json: String): Map<LocalDate, TimeTableInfo> {
    val gson = GsonBuilder()
        .registerTypeAdapter(Long::class.java, ColorDeserializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .create()

    val daysInPeriods: MutableMap<LocalDate, TimeTableInfo> = mutableMapOf()

    gson.fromJson(json, Array<TimeTableInfo>::class.java)
        .filter { it.startDate.isBefore(it.endDate.plusDays(1)) }
        .forEach { period ->
            var date = period.startDate
            while (date.isBefore(period.endDate.plusDays(1))) {
                val day = daysInPeriods[date]
                val savedColorImportance = periodColors.getOrDefault(day?.category, 0)
                val checkingColorImportance = periodColors.getOrDefault(period.category, 0)
                val isMoreImportant = checkingColorImportance > savedColorImportance
                if (day == null || isMoreImportant) {
                    daysInPeriods[date] = period
                }
                date = date.plusDays(1)
            }
        }
    return daysInPeriods.toMap()
}

private fun parseRecurring(element: Element?): Recurring {
    return when {
        element == null -> Recurring.ONCE
        element.hasClass("weekly") -> Recurring.WEEKLY
        element.hasClass("everyTwoWeeks") -> Recurring.EVERY_TWO_WEEKS
        element.hasClass("monthly") -> Recurring.MONTHLY
        else -> Recurring.UNDEFINED
    }
}
