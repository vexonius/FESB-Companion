package com.tstudioz.fax.fme.util

import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.EventRoom
import com.tstudioz.fax.fme.database.models.TimetableType
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.StudomatYearInfo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

val studomatSubjectTestData = listOf(
    StudomatSubject(
        id = "1",
        name = "Introduction to Programming",
        electiveGroup = "Core",
        semester = "1",
        lectures = "30",
        exercises = "15",
        ectsEnrolled = "5",
        isTaken = "Yes",
        status = "Passed",
        grade = "A",
        examDate = "2023-06-01",
        year = "2023",
        course = "Computer Science",
        isPassed = true
    ),
    StudomatSubject(
        id = "2",
        name = "Discrete Mathematics",
        electiveGroup = "Core",
        semester = "1",
        lectures = "45",
        exercises = "30",
        ectsEnrolled = "6",
        isTaken = "Yes",
        status = "Passed",
        grade = "B",
        examDate = "2023-06-15",
        year = "2023",
        course = "Computer Science",
        isPassed = true
    ),
    StudomatSubject(
        id = "3",
        name = "Digital Logic",
        electiveGroup = "Core",
        semester = "1",
        lectures = "30",
        exercises = "20",
        ectsEnrolled = "5",
        isTaken = "Yes",
        status = "Passed",
        grade = "C",
        examDate = "2023-07-01",
        year = "2023",
        course = "Computer Science",
        isPassed = true
    ),
    StudomatSubject(
        id = "4",
        name = "Data Structures",
        electiveGroup = "Core",
        semester = "3",
        lectures = "45",
        exercises = "30",
        ectsEnrolled = "6",
        isTaken = "Yes",
        status = "Passed",
        grade = "A",
        examDate = "2023-12-20",
        year = "2024",
        course = "Computer Science 2",
        isPassed = true
    ),
    StudomatSubject(
        id = "5",
        name = "Computer Architecture",
        electiveGroup = "Core",
        semester = "3",
        lectures = "45",
        exercises = "30",
        ectsEnrolled = "6",
        isTaken = "Yes",
        status = "Passed",
        grade = "B",
        examDate = "2024-01-10",
        year = "2024",
        course = "Computer Science 2",
        isPassed = true
    ),
    StudomatSubject(
        id = "6",
        name = "Operating Systems",
        electiveGroup = "Core",
        semester = "3",
        lectures = "45",
        exercises = "30",
        ectsEnrolled = "6",
        isTaken = "Yes",
        status = "Passed",
        grade = "A",
        examDate = "2024-02-05",
        year = "2024",
        course = "Computer Science 2",
        isPassed = true
    ),
)

val studomatYearInfoTestData = listOf(
    StudomatYearInfo().apply {
        id = "1"
        courseName = "Computer Science"
        studyProgram = "Undergraduate"
        parallelStudy = "Full-time"
        yearOfCourse = 1
        enrollmentIndicator = "Enrolled"
        payment = false
        fundingBasis = "State-funded"
        universityCenter = "Main Campus"
        studentRightsValidUntil = "2024-06-30"
        enrollmentDate = "2023-09-01"
        enrollmentCompleted = true
        academicYear = "2023"
        href = "http://example.com/enrollment/2023"
    },
    StudomatYearInfo().apply {
        id = "2"
        courseName = "Computer Science 2"
        studyProgram = "Undergraduate"
        parallelStudy = "Full-time"
        yearOfCourse = 2
        enrollmentIndicator = "Enrolled"
        payment = false
        fundingBasis = "State-funded"
        universityCenter = "Main Campus"
        studentRightsValidUntil = "2025-06-30"
        enrollmentDate = "2024-09-01"
        enrollmentCompleted = true
        academicYear = "2024"
        href = "http://example.com/enrollment/2024"
    }
)

// Monday of this week
private val thisWeekMonday: LocalDate = LocalDate.now()
    .with(DayOfWeek.MONDAY)

private fun thisWeekDate(dayOfWeek: DayOfWeek, hour: Int, minute: Int): LocalDateTime {
    return thisWeekMonday.with(dayOfWeek).atTime(hour, minute)
}

val eventsTestData = listOf(
    EventRoom(
        Event(
            id = "532059",
            name = "Kriptografija i mrežna sigurnost",
            shortName = "KIMS",
            colorId = -65536, // bright red
            professor = "Čagalj Mario",
            eventType = TimetableType.PREDAVANJE,
            groups = "",
            classroom = "C501",
            start = thisWeekDate(DayOfWeek.MONDAY, 10, 15),
            end = thisWeekDate(DayOfWeek.MONDAY, 12, 0),
            description = "C501"
        )
    ),
    EventRoom(
        Event(
            id = "534198",
            name = "Metode optimizacije",
            shortName = "MO",
            colorId = -16776961, // bright blue
            professor = "Bašić Martina",
            eventType = TimetableType.LABORATORIJSKA_VJEZBA,
            groups = "Grupa 1,",
            classroom = "B420",
            start = thisWeekDate(DayOfWeek.MONDAY, 18, 30),
            end = thisWeekDate(DayOfWeek.MONDAY, 20, 0),
            description = "B420"
        )
    ),
    EventRoom(
        Event(
            id = "532144",
            name = "Podržano strojno učenje",
            shortName = "PSU",
            colorId = -16711936, // bright green
            professor = "Vasilj Josip",
            eventType = TimetableType.PREDAVANJE,
            groups = "",
            classroom = "A243",
            start = thisWeekDate(DayOfWeek.TUESDAY, 8, 15),
            end = thisWeekDate(DayOfWeek.TUESDAY, 10, 0),
            description = "A243"
        )
    ),
    EventRoom(
        Event(
            id = "532084",
            name = "Metode optimizacije",
            shortName = "MO",
            colorId = -256, // bright yellow
            professor = "Marasović Jadranka",
            eventType = TimetableType.PREDAVANJE,
            groups = "",
            classroom = "C502",
            start = thisWeekDate(DayOfWeek.TUESDAY, 10, 15),
            end = thisWeekDate(DayOfWeek.TUESDAY, 12, 0),
            description = "C502"
        )
    ),
    EventRoom(
        Event(
            id = "532120",
            name = "IP komunikacije",
            shortName = "IK",
            colorId = -65536, // bright red
            professor = "Russo Mladen",
            eventType = TimetableType.PREDAVANJE,
            groups = "",
            classroom = "A105",
            start = thisWeekDate(DayOfWeek.TUESDAY, 12, 15),
            end = thisWeekDate(DayOfWeek.TUESDAY, 14, 0),
            description = "A105"
        )
    ),
    EventRoom(
        Event(
            id = "538989",
            name = "Podržano strojno učenje",
            shortName = "PSU",
            colorId = -16711936, // bright green
            professor = "Vasilj Josip",
            eventType = TimetableType.LABORATORIJSKA_VJEZBA,
            groups = "Grupa 1,",
            classroom = "A507",
            start = thisWeekDate(DayOfWeek.THURSDAY, 10, 0),
            end = thisWeekDate(DayOfWeek.THURSDAY, 12, 15),
            description = "A507"
        )
    ),
    EventRoom(
        Event(
            id = "535595",
            name = "Jezici i prevoditelji",
            shortName = "JIP",
            colorId = -65536, // bright red
            professor = "Sikora Marjan",
            eventType = TimetableType.LABORATORIJSKA_VJEZBA,
            groups = "Grupa 1,",
            classroom = "B526",
            start = thisWeekDate(DayOfWeek.THURSDAY, 8, 30),
            end = thisWeekDate(DayOfWeek.THURSDAY, 10, 0),
            description = "B526"
        )
    ),
    EventRoom(
        Event(
            id = "535336",
            name = "IP komunikacije",
            shortName = "IK",
            colorId = -16711936, // bright green
            professor = "Meter Davor",
            eventType = TimetableType.LABORATORIJSKA_VJEZBA,
            groups = "Grupa 1,",
            classroom = "B526",
            start = thisWeekDate(DayOfWeek.FRIDAY, 8, 0),
            end = thisWeekDate(DayOfWeek.FRIDAY, 9, 30),
            description = "B526"
        )
    ),
)