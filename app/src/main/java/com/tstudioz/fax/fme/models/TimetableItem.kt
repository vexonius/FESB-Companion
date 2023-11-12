package com.tstudioz.fax.fme.models

data class TimetableItem(val id: Int,
                         val startDate: String = "",
                         val endDate: String = "",
                         val startHour: Int = 0,
                         val startMin: Int = 0,
                         val endHour: Int = 0,
                         val endMin: Int = 0,
                         val name: String = "",
                         val eventType: TimetableEvent = TimetableEvent.GENERIC,
                         val group: String = "",
                         val room: String = "",
                         val timeSpan: String = "",
                         val studyCode: String = "",
                         val recurring: Boolean = false,
                         val recurringType: EventRecurring = EventRecurring.UNDEFINED,
                         val detailDateWithDayName: String = "",
                         val professor: String = "",
                         val classDuration: Int = 0,
                         val recurringUntil: String = ""
) {

    override fun equals(other: Any?): Boolean {
        if (other !is TimetableItem) throw IllegalArgumentException("You can only compare same class instances")

        return other.id == this.id &&
                other.name == this.name &&
                other.group == this.group &&
                other.professor == this.professor &&
                other.startDate == this.startDate &&
                other.endDate == this.endDate &&
                other.startHour == this.startHour &&
                other.startMin == this.startMin &&
                other.endHour == this.endHour &&
                other.endMin == this.endMin &&
                other.eventType == this.eventType &&
                other.room == this.room &&
                other.timeSpan == this.timeSpan &&
                other.studyCode == this.studyCode &&
                other.recurring == this.recurring &&
                other.recurringType == this.recurringType &&
                other.detailDateWithDayName == this.detailDateWithDayName &&
                other.classDuration == this.classDuration &&
                other.recurringUntil == this.recurringUntil

    }

}

enum class TimetableEvent(val type: String) {

    PREDAVANJA("Predavanje"),
    AUDITORNE_VJEZBE("Auditorne vježbe"),
    LABARATORIJSKE_VJEZBE("Labaratorijske vježbe"),
    KONSTRUKCIJSKE_VJEZBE("Konstrukcijske vježbe"),
    KOLOKVIJ("Kolokvij"),
    SEMINAR("Seminar"),
    ISPIT("Ispit"),
    GENERIC(""),
    TERENSKA_NASTAVA("Terenska nastava")
}

enum class EventRecurring {
    ONCE, WEEKLY, EVERY_TWO_WEEKS, MONTHLY, UNDEFINED
}
