package com.tstudioz.fax.fme.util

import android.util.Log
import com.tstudioz.fax.fme.models.EventRecurring
import com.tstudioz.fax.fme.models.TimetableEvent
import com.tstudioz.fax.fme.models.TimetableItem
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*


suspend fun parseTimetable(body: String): List<TimetableItem> {
    val items = ArrayList<TimetableItem>()

    val doc = Jsoup.parse(body)
    val elements = doc.select("div.event")

    elements?.let {
        for (e in elements) {
            val id = e?.attr("data-id")?.toInt() ?: -1
            val startdate = e?.attr("data-startsdate")?.toString() ?: ""
            val starth = e?.attr("data-startshour")?.toInt() ?: -1
            val startmin = e?.attr("data-startsmin")?.toInt() ?: -1
            val enddate = e?.attr("data-endsdate")?.toString() ?: ""
            val endh = e?.attr("data-endshour")?.toInt() ?: -1
            val endmin = e?.attr("data-endsmin")?.toInt() ?: -1
            val type = parseEventType(e?.selectFirst("span.groupCategory"))
            val name = e?.selectFirst("span.name.normal")?.text()
                    ?: e?.selectFirst("div.popup > div.eventContent > div.header > div > span.title")?.text()
                    ?: ""
            val timespan = e?.selectFirst("div.timespan")?.text() ?: ""
            val group = e?.select("span.group.normal")?.first()?.text() ?: ""
            val studycode = e?.selectFirst("span.studyCode")?.text() ?: ""
            val room = e?.selectFirst("div.eventContent > div.eventInfo > span.resource")?.text() ?: ""
            val detailTime = e?.selectFirst("div.detailItem.datetime")?.text() ?: ""
            val professor = e?.selectFirst("div.detailItem.user")?.text() ?: ""
            val repetsType = parseNumbersFromString(e?.selectFirst("div.recurring > span.type > span") ?: null)
            val isItRecurring = !(repetsType==EventRecurring.ONCE || repetsType==EventRecurring.UNDEFINED)
            val classDruration = parseClassDuration(e?.selectFirst("div.detailItem.datetime > span"))

            val repeatsUntil = e?.selectFirst("span.repeat")?.text() ?: ""

            items.add(TimetableItem(id, startdate, enddate, starth, startmin, endh, endmin, name, type, group, room, timespan, studycode, isItRecurring, repetsType, detailTime, professor, classDruration, repeatsUntil))
        }
    }

    return items
}

private fun parseClassDuration(element: Element?): Int {
    if (element==null) return -1

    var duration = -1;
    try {
        val pattern = "\\d+".toRegex()
        val num = pattern.find(element.text())
        num?.value?.let {
            duration = it.toInt()
        }
    } catch (e: Exception){
        Log.e("Parsing util", e.message)
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
