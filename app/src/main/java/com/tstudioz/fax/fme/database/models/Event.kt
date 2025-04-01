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
    fun toRoomObject(): EventRoom {
        return EventRoom(
            id = this.id,
            name = this.name,
            shortName = this.shortName,
            colorId = this.colorId,
            professor = this.professor,
            eventType = this.eventType.name,
            groups = this.groups,
            classroom = this.classroom,
            start = this.start.toString(),
            end = this.end.toString(),
            description = this.description,
            recurring = this.recurring,
            recurringType = this.recurringType.name,
            recurringUntil = this.recurringUntil,
            studyCode = this.studyCode,
        )
    }
}

@Entity
data class EventRoom (
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
)
{
    fun fromRoomObject(): Event {
        return Event(
            id = this.id,
            name = this.name ?: "",
            shortName = this.shortName ?: "",
            colorId = this.colorId ?: 0,
            color = Color(this.colorId ?: 0),
            professor = this.professor ?: "",
            eventType = this.eventType?.let {
                try {
                    TimetableType.valueOf(it)
                } catch (error: IllegalArgumentException) {
                    TimetableType.OTHER
                }
            } ?: TimetableType.OTHER,
            groups = this.groups ?: "",
            classroom = this.classroom ?: "",
            start = LocalDateTime.parse(this.start),
            end = LocalDateTime.parse(this.end),
            description = this.description,
            recurring = this.recurring ?: false,
            recurringType = Recurring.valueOf(this.recurringType ?: ""),
            recurringUntil = this.recurringUntil ?: "",
            studyCode = this.studyCode ?: ""
        )
    }
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

