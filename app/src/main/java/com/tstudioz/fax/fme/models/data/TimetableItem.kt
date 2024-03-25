package com.tstudioz.fax.fme.models.data

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
                         val recurringUntil: String = "")

enum class TimetableEvent(val type: String) {

    PREDAVANJA("Predavanje"),
    AUDITORNE_VJEZBE("Auditorne vježbe"),
    LABARATORIJSKE_VJEZBE("Laboratorijske vježbe"),
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
