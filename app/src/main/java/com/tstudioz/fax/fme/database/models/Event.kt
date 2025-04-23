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

    constructor(model: EventRoom) : this(
        id = model.id,
        name = model.name ?: "",
        shortName = model.shortName ?: "",
        colorId = model.colorId ?: 0,
        color = Color(model.colorId ?: 0),
        professor = model.professor ?: "",
        eventType = model.eventType?.let {
            try {
                TimetableType.valueOf(it)
            } catch (error: IllegalArgumentException) {
                TimetableType.OTHER
            }
        } ?: TimetableType.OTHER,
        groups = model.groups ?: "",
        classroom = model.classroom ?: "",
        start = LocalDateTime.parse(model.start),
        end = LocalDateTime.parse(model.end),
        description = model.description,
        recurring = model.recurring == true,
        recurringType = Recurring.valueOf(model.recurringType ?: ""),
        recurringUntil = model.recurringUntil ?: "",
        studyCode = model.studyCode ?: ""
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
    constructor(model: Event) : this(
        id = model.id,
        name = model.name,
        shortName = model.shortName,
        colorId = model.colorId,
        professor = model.professor,
        eventType = model.eventType.value,
        groups = model.groups,
        classroom = model.classroom,
        start = model.start.toString(),
        end = model.end.toString(),
        description = model.description,
        recurring = model.recurring,
        recurringType = model.recurringType.name,
        recurringUntil = model.recurringUntil,
        studyCode = model.studyCode
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

