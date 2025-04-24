package com.tstudioz.fax.fme.database.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tstudioz.fax.fme.compose.blueNice
import com.tstudioz.fax.fme.compose.greenNice
import com.tstudioz.fax.fme.compose.greyNice
import com.tstudioz.fax.fme.compose.purpleNice
import com.tstudioz.fax.fme.compose.redNice
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
) {

    constructor(eventRoom: EventRoom) : this(
        id = eventRoom.id,
        name = eventRoom.name ?: "",
        shortName = eventRoom.shortName ?: "",
        colorId = eventRoom.colorId ?: 0,
        color = Color(eventRoom.colorId ?: 0),
        professor = eventRoom.professor ?: "",
        eventType = eventRoom.eventType?.let {
            try {
                TimetableType.valueOf(it)
            } catch (error: IllegalArgumentException) {
                TimetableType.OTHER
            }
        } ?: TimetableType.OTHER,
        groups = eventRoom.groups ?: "",
        classroom = eventRoom.classroom ?: "",
        start = LocalDateTime.parse(eventRoom.start),
        end = LocalDateTime.parse(eventRoom.end),
        description = eventRoom.description,
        recurring = eventRoom.recurring == true,
        recurringType = Recurring.valueOf(eventRoom.recurringType ?: ""),
        recurringUntil = eventRoom.recurringUntil ?: "",
        studyCode = eventRoom.studyCode ?: ""
    )
}

@Entity
data class EventRoom(
    @PrimaryKey
    var id: String = "",
    var name: String? = null,
    var shortName: String? = null,
    var colorId: Int? = null,
    var professor: String? = null,
    var eventType: String? = null,
    var groups: String? = null,
    var classroom: String? = null,
    var start: String? = null,
    var end: String? = null,
    var description: String? = null,
    var recurring: Boolean? = null,
    var recurringType: String? = null,
    var recurringUntil: String? = null,
    var studyCode: String? = null,
) {
    constructor(event: Event) : this(
        id = event.id,
        name = event.name,
        shortName = event.shortName,
        colorId = event.colorId,
        professor = event.professor,
        eventType = event.eventType.value,
        groups = event.groups,
        classroom = event.classroom,
        start = event.start.toString(),
        end = event.end.toString(),
        description = event.description,
        recurring = event.recurring,
        recurringType = event.recurringType.name,
        recurringUntil = event.recurringUntil,
        studyCode = event.studyCode
    )
}

enum class Recurring {
    ONCE, WEEKLY, EVERY_TWO_WEEKS, MONTHLY, UNDEFINED
}

enum class TimetableType(val value: String) {
    PREDAVANJE("Predavanja"),
    AUDITORNA_VJEZBA("Auditorne vježbe"),
    KOLOKVIJ("Kolokviji"),
    LABORATORIJSKA_VJEZBA("Laboratorijske vježbe"),
    KONSTRUKCIJSKA_VJEZBA("Konstrukcijske vježbe"),
    SEMINAR("Seminari"),
    ISPIT("Ispiti"),
    OTHER("Other")
}

fun TimetableType.color(): Color {
    return when (this) {
        TimetableType.PREDAVANJE -> blueNice
        TimetableType.AUDITORNA_VJEZBA -> greenNice
        TimetableType.KOLOKVIJ -> purpleNice
        TimetableType.LABORATORIJSKA_VJEZBA -> redNice
        TimetableType.KONSTRUKCIJSKA_VJEZBA -> greyNice
        TimetableType.SEMINAR -> blueNice
        TimetableType.ISPIT -> purpleNice
        TimetableType.OTHER -> blueNice
    }
}

