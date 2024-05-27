package com.tstudioz.fax.fme.models.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Recurring
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.database.models.TimetableType
import com.tstudioz.fax.fme.weather.Current
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset


suspend fun parseTimetable(body: String): List<Event> {
    val events = ArrayList<Event>()

    val doc = Jsoup.parse(body)
    val elements = doc.select("div.event")

    elements.let {
        for (e in elements) {
            val id = e.attr("data-id").toInt()
            val startdate = e.attr("data-startsdate").toString()
            val startDateSplit = startdate.split("-")
            val starth = e.attr("data-startshour").toInt()
            val startmin = e.attr("data-startsmin").toInt()
            val enddate = e.attr("data-endsdate").toString()
            val endDateSplit = enddate.split("-")
            val endh = e.attr("data-endshour").toInt()
            val endmin = e.attr("data-endsmin").toInt()
            val type = editType(e.selectFirst("span.groupCategory")?.text()?.split(",")?.get(0) ?: "")
            val name = e.selectFirst("span.name.normal")?.text()
                ?: e.selectFirst("div.popup > div.eventContent > div.header > div > span.title")?.text()
                ?: ""
            val group = e.select("span.group.normal").first()?.text() ?: ""
            val studycode = e.selectFirst("span.studyCode")?.text() ?: ""
            val room = e.selectFirst("div.eventContent > div.eventInfo > span.resource")?.text() ?: ""
            val detailTime = e.selectFirst("div.detailItem.datetime")?.text() ?: ""
            val professor = e.selectFirst("div.detailItem.user")?.text() ?: ""
            val repetsType = parseNumbersFromString(e.selectFirst("div.recurring > span.type > span"))
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
                    colorId = getBoja(type),
                    start = LocalDateTime.of(
                        startDateSplit[0].toInt(),
                        startDateSplit[1].toInt(),
                        startDateSplit[2].toInt(),
                        starth,
                        startmin
                    ),
                    end = LocalDateTime.of(
                        endDateSplit[0].toInt(),
                        endDateSplit[1].toInt(),
                        endDateSplit[2].toInt(),
                        endh,
                        endmin
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

private fun editType(type: String): TimetableType {
    return when (type) {
        "Predavanja" -> TimetableType.PREDAVANJE
        "Auditorne vježbe" -> TimetableType.AUDITORNA_VJEZBA
        "Kolokviji" -> TimetableType.KOLOKVIJ
        "Laboratorijske vježbe" -> TimetableType.LABORATORIJSKA_VJEZBA
        "Konstrukcijske vježbe" -> TimetableType.KONSTRUKCIJSKA_VJEZBA
        "Seminari" -> TimetableType.SEMINAR
        "Ispiti" -> TimetableType.ISPIT
        else -> TimetableType.OTHER
    }
}

private fun getBoja(type: TimetableType): Int {
    return when (type) {
        TimetableType.PREDAVANJE -> R.color.blue_nice
        TimetableType.AUDITORNA_VJEZBA -> R.color.green_nice
        TimetableType.KOLOKVIJ -> R.color.purple_nice
        TimetableType.LABORATORIJSKA_VJEZBA -> R.color.red_nice
        TimetableType.KONSTRUKCIJSKA_VJEZBA -> R.color.grey_nice
        TimetableType.SEMINAR -> R.color.blue_nice
        TimetableType.ISPIT -> R.color.purple_dark
        else -> {
            R.color.blue_nice
        }
    }
}

suspend fun makeAcronym(name: String): String {
    val acronym = StringBuilder()
    if (name.isNotEmpty() && name.contains(" ")
    ) {
        val nameSplit = name.split(" ").toTypedArray()
        for (str in nameSplit)
            acronym.append(str[0])
        return acronym.toString().uppercase()
    }
    return name
}


suspend fun parseTimetableInfo(json: String): List<TimeTableInfo> {
    val gson = GsonBuilder()
        .registerTypeAdapter(Long::class.java, ColorDeserializer())
        .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
        .create()
    return gson.fromJson(json, Array<TimeTableInfo>::class.java).toList()
}

class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        val dateString = json?.asString?.split("/Date(")?.get(1)?.split(")")?.get(0)
        return dateString?.toLong()?.div(1000)?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC).toLocalDate().plusDays(1)
        }
    }
}

class ColorDeserializer : JsonDeserializer<Long> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long {
        return when (json?.asString) {
            "White" -> 0xFF191C1D
            "Blue" -> 0xff0060ff
            "Yellow" -> 0xffe5c700
            "Orange" -> 0xffff6600
            "Purple" -> 0xffa200ff
            "Red" -> 0xffff0000
            "Green" -> 0xff0b9700
            else -> 0xFF191C1D
        }
    }
}


private fun parseNumbersFromString(element: Element?): Recurring {
    return when {
        element == null -> Recurring.ONCE
        element.hasClass("weekly") -> Recurring.WEEKLY
        element.hasClass("everyTwoWeeks") -> Recurring.EVERY_TWO_WEEKS
        element.hasClass("monthly") -> Recurring.MONTHLY
        else -> Recurring.UNDEFINED
    }
}

fun parseWeatherDetails(data: String): Current {
    val forecastjson = JSONObject(data)

    val currently0 = forecastjson.getJSONObject("properties")
    val currentlyArray = currently0.getJSONArray("timeseries")
    val currently =
        currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("instant").getJSONObject("details")
    val currentlyNextOneHours = currentlyArray.getJSONObject(0).getJSONObject("data").getJSONObject("next_1_hours")
    val currentlyNextOneHoursSummary = currentlyNextOneHours.getJSONObject("summary")
    val currentlyNextOneHoursDetails = currentlyNextOneHours.getJSONObject("details")
    val unparsedsummary = currentlyNextOneHoursSummary.getString("symbol_code")
    val summarycode: String? = if (unparsedsummary.contains("_")) {
        unparsedsummary.substring(0, unparsedsummary.indexOf('_'))
    } else {
        unparsedsummary
    }

    val current = Current()
    current.humidity = currently.getDouble("relative_humidity")
    current.icon = currentlyNextOneHoursSummary.getString("symbol_code")
    current.precipChance = currentlyNextOneHoursDetails.getDouble("precipitation_amount")
    current.summary = summarycode
    current.wind = currently.getDouble("wind_speed")
    current.setTemperature(currently.getDouble("air_temperature"))
    return current
}
