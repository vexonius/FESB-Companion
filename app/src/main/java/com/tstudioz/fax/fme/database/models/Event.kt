package com.tstudioz.fax.fme.database.models

import androidx.compose.ui.graphics.Color
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.blueNice
import com.tstudioz.fax.fme.compose.greenNice
import com.tstudioz.fax.fme.compose.greyNice
import com.tstudioz.fax.fme.compose.purpleNice
import com.tstudioz.fax.fme.compose.redNice
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime


data class Event(
    val id: String,
    val name: String,
    val shortName: String,
    var color: Color = Color.Blue,
    val colorId: Int = 0,
    val professor: String = "",
    val eventType: TimetableType = TimetableType.OTHER,
    val groups: String = "",
    val classroom: String = "",
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null,
    val recurring: Boolean = false,
    val recurringType: Recurring = Recurring.UNDEFINED,
    val recurringUntil: String = "",
    val studyCode: String = "",
)

open class EventRealm : RealmObject {
    @PrimaryKey
    var id: String? = null
    var name: String? = null
    var shortName: String? = null
    var colorId: Int? = null
    var professor: String? = null
    var eventType: String? = null
    var groups: String? = null
    var classroom: String? = null
    var start: String? = null
    var end: String? = null
    var description: String? = null
    var recurring: Boolean? = null
    var recurringType: String? = null
    var recurringUntil: String? = null
    var studyCode: String? = null
}

enum class Recurring {
    ONCE, WEEKLY, EVERY_TWO_WEEKS, MONTHLY, UNDEFINED
}

fun toRealmObject(event: Event): EventRealm {
    return EventRealm().apply {
        id = event.id
        name = event.name
        shortName = event.shortName
        colorId = event.colorId
        professor = event.professor
        eventType = event.eventType.name
        groups = event.groups
        classroom = event.classroom
        start = event.start.toString()
        end = event.end.toString()
        description = event.description
        recurring = event.recurring
        recurringType = event.recurringType.name
        recurringUntil = event.recurringUntil
        studyCode = event.studyCode
    }
}

fun fromRealmObject(eventRealm: EventRealm): Event {
    return Event(
        id = eventRealm.id ?: "",
        name = eventRealm.name ?: "",
        shortName = eventRealm.shortName ?: "",
        colorId = eventRealm.colorId ?: 0,
        color = Color(eventRealm.colorId ?: 0),
        professor = eventRealm.professor ?: "",
        eventType = eventRealm.eventType?.let {
            try {
                TimetableType.valueOf(it)
            } catch (error: IllegalArgumentException) {
                TimetableType.OTHER
            }
        } ?: TimetableType.OTHER,
        groups = eventRealm.groups ?: "",
        classroom = eventRealm.classroom ?: "",
        start = LocalDateTime.parse(eventRealm.start),
        end = LocalDateTime.parse(eventRealm.end),
        description = eventRealm.description,
        recurring = eventRealm.recurring ?: false,
        recurringType = Recurring.valueOf(eventRealm.recurringType ?: ""),
        recurringUntil = eventRealm.recurringUntil ?: "",
        studyCode = eventRealm.studyCode ?: ""
    )
}

enum class TimetableType(val type: String, val color: Color) {
    PREDAVANJE("Predavanje", blueNice),
    AUDITORNA_VJEZBA("Auditorna vježba", greenNice),
    KOLOKVIJ("Kolokvij", purpleNice),
    LABORATORIJSKA_VJEZBA("Laboratorijska vježba", redNice),
    KONSTRUKCIJSKA_VJEZBA("Konstrukcijska vježba", greyNice),
    SEMINAR("Seminar", blueNice),
    ISPIT("Ispit", purpleNice),
    OTHER("Other", blueNice)
}

