package com.tstudioz.fax.fme.models.util

import android.util.Log
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.models.data.EventRecurring
import com.tstudioz.fax.fme.models.data.TimetableEvent
import com.tstudioz.fax.fme.models.data.TimetableItem
import com.tstudioz.fax.fme.weather.Current
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset


suspend fun parseTimetable(body: String): List<TimetableItem> {
    val items = ArrayList<TimetableItem>()

    val doc = Jsoup.parse(body)
    val elements = doc.select("div.event")

    elements.let {
        for (e in elements) {
            val id = e.attr("data-id").toInt() ?: -1
            val startdate = e.attr("data-startsdate").toString() ?: ""
            val starth = e.attr("data-startshour").toInt() ?: -1
            val startmin = e.attr("data-startsmin").toInt() ?: -1
            val enddate = e.attr("data-endsdate").toString() ?: ""
            val endh = e.attr("data-endshour").toInt() ?: -1
            val endmin = e.attr("data-endsmin").toInt() ?: -1
            val type = parseEventType(e.selectFirst("span.groupCategory"))
            val name = e.selectFirst("span.name.normal")?.text()
                ?: e.selectFirst("div.popup > div.eventContent > div.header > div > span.title")?.text() ?: ""
            val timespan = e.selectFirst("div.timespan")?.text() ?: ""
            val group = e.select("span.group.normal").first()?.text() ?: ""
            val studycode = e.selectFirst("span.studyCode")?.text() ?: ""
            val room = e.selectFirst("div.eventContent > div.eventInfo > span.resource")?.text() ?: ""
            val detailTime = e.selectFirst("div.detailItem.datetime")?.text() ?: ""
            val professor = e.selectFirst("div.detailItem.user")?.text() ?: ""
            val repetsType = parseNumbersFromString(e.selectFirst("div.recurring > span.type > span"))
            val isItRecurring = !(repetsType == EventRecurring.ONCE || repetsType == EventRecurring.UNDEFINED)
            val classDruration = parseClassDuration(e.selectFirst("div.detailItem.datetime > span"))

            val repeatsUntil = e.selectFirst("span.repeat")?.text() ?: ""

            items.add(
                TimetableItem(
                    id,
                    startdate,
                    enddate,
                    starth,
                    startmin,
                    endh,
                    endmin,
                    name,
                    type,
                    group,
                    room,
                    timespan,
                    studycode,
                    isItRecurring,
                    repetsType,
                    detailTime,
                    professor,
                    classDruration,
                    repeatsUntil
                )
            )
        }
    }

    return items
}


suspend fun parseTimetableInfo(body: String): List<TimeTableInfo> {
    val items = mutableListOf<TimeTableInfo>()
    val jsons = body.split("[", "]")[1].split("{", "},{", "}")
    for (jsn in jsons) {
        if (jsn.isEmpty()) continue
        items.add(
            TimeTableInfo(
                jsn.split("Id\":\"")[1].split("\",")[0].toInt(),
                jsn.split("Name\":\"")[1].split("\"")[0],
                jsn.split("StartDate\":\"\\/Date(")[1].split(")")[0].toLong(),
                jsn.split("EndDate\":\"\\/Date(")[1].split(")")[0].toLong(),
                jsn.split("StartDateText\":\"")[1].split("\"")[0],
                jsn.split("EndDateText\":\"")[1].split("\"")[0],
                jsn.split("Category\":\"")[1].split("\"")[0],
                jsn.split("ColorCode\":\"")[1].split("\"")[0],
                jsn.split("IsWorking\":")[1].split(",")[0].toBoolean(),
                LocalDateTime.ofEpochSecond(
                    jsn.split("StartDate\":\"\\/Date(")[1].split(")")[0].toLong().div(1000), 0, ZoneOffset.UTC
                ).toLocalDate().plusDays(1),
                LocalDateTime.ofEpochSecond(
                    jsn.split("EndDate\":\"\\/Date(")[1].split(")")[0].toLong().div(1000), 0, ZoneOffset.UTC
                ).toLocalDate().plusDays(1)
            )
        )
    }

    return items
}

private fun parseClassDuration(element: Element?): Int {
    if (element == null) return -1

    var duration = -1
    try {
        val pattern = "\\d+".toRegex()
        val num = pattern.find(element.text())
        num?.value?.let {
            duration = it.toInt()
        }
    } catch (e: Exception) {
        e.message?.let { Log.e("Parsing util", it) }
    }
    return duration
}

private fun parseNumbersFromString(element: Element?): EventRecurring {
    return when {
        element == null -> EventRecurring.ONCE
        element.hasClass("weekly") -> EventRecurring.WEEKLY
        element.hasClass("everyTwoWeeks") -> EventRecurring.EVERY_TWO_WEEKS
        element.hasClass("monthly") -> EventRecurring.MONTHLY
        else -> EventRecurring.UNDEFINED
    }
}

private fun parseEventType(element: Element?): TimetableEvent {
    if (element == null) return TimetableEvent.GENERIC

    return when (element.text()) {
        "Predavanja," -> TimetableEvent.PREDAVANJA
        "Auditorne vježbe," -> TimetableEvent.AUDITORNE_VJEZBE
        "Kolokviji," -> TimetableEvent.KOLOKVIJ
        "Laboratorijske vježbe," -> TimetableEvent.LABARATORIJSKE_VJEZBE
        "Konstrukcijske vježbe," -> TimetableEvent.KONSTRUKCIJSKE_VJEZBE
        "Seminar," -> TimetableEvent.SEMINAR
        "Ispiti," -> TimetableEvent.ISPIT
        "Terenska nastava," -> TimetableEvent.TERENSKA_NASTAVA
        else -> TimetableEvent.GENERIC
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

fun parseRacuni(doc: String): List<Receipt> {
    val racuni = mutableListOf<Receipt>()
    val table = Jsoup.parse(doc).select("table")
    val rows = table.select("tr")
    for (row in rows) {
        val cols = row.select("td")
        if (cols.size >= 6) {
            racuni.add(
                Receipt(
                    cols[0].text(),
                    LocalDate.of(
                        cols[1].text().split(".")[2].toInt(),
                        cols[1].text().split(".")[1].toInt(),
                        cols[1].text().split(".")[0].toInt()
                    ),
                    cols[1].text(),
                    cols[2].text(),
                    cols[3].text(),
                    cols[4].text(),
                    cols[5].text(),
                    cols[6].select("a").attr("href")
                )
            )
        }
    }
    return racuni.sortedByDescending {
        LocalTime.of(
            it.vrijeme.split(":")[0].toInt(), it.vrijeme.split(":")[1].toInt()
        )
    }.sortedByDescending { it.datum }
}

fun parseDetaljeRacuna(doc: String): MutableList<ReceiptItem> {
    val detaljiRacuna = mutableListOf<ReceiptItem>()
    val table = Jsoup.parse(doc).select(".table-responsive").first()
    val rows = table?.select("tbody")?.select("tr")
    rows?.forEach { row ->
        val cols = row.select("td")
        val item = ReceiptItem(
            cols[0].text(),
            cols[1].text(),
            cols[2].text(),
            cols[3].text(),
            cols[4].text()
        )
        if (!detaljiRacuna.any { it.nazivArtikla == item.nazivArtikla && it.cijenaJednogArtikla == item.cijenaJednogArtikla }) {
            detaljiRacuna.add(item)
        } else {
            val index = detaljiRacuna.indexOf(
                detaljiRacuna.first {
                    it.nazivArtikla == item.nazivArtikla && it.cijenaJednogArtikla == item.cijenaJednogArtikla
                })
            detaljiRacuna[index].kolicina = (detaljiRacuna[index].kolicina.toInt() + item.kolicina.toInt()).toString()
        }
    }
    return detaljiRacuna
}

