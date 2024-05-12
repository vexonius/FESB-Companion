package com.tstudioz.fax.fme.database.models

import androidx.compose.ui.graphics.Color
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.LocalDateTime


data class Event(
    val id : String,
    val name: String,
    val shortName: String,
    var color: Color = Color.Blue,
    val colorId : Int = 0,
    val professor: String = "",
    val eventType: String = "",
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
        eventType = event.eventType
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
        professor = eventRealm.professor ?: "",
        eventType = eventRealm.eventType ?: "",
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
