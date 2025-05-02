package com.tstudioz.fax.fme.feature.attendance

import android.util.Log
import com.tstudioz.fax.fme.feature.attendance.models.AttendanceEntry
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.UUID

class ParseAttendance {

    fun parseAttendance(
        element: Element,
        body: String,
        semester: Int
    ): List<AttendanceEntry> {
        val attendanceForOneSemester = mutableListOf<AttendanceEntry>()
        Jsoup.parse(body).select(".courseCategories div.courseCategory").forEach { kat ->
            val mAttendanceEntry = AttendanceEntry()
            mAttendanceEntry.semester = semester
            mAttendanceEntry.subject = element.select(".cellContent").first()?.text() ?: ""
            mAttendanceEntry.type =
                (kat.getElementsByClass("name").first()?.text() ?: "").replaceFirstChar { it.uppercase() }
            mAttendanceEntry.attended = kat.select(".attended > span.num").first()?.text()?.toInt() ?: -1
            mAttendanceEntry.absent = kat.select(".absent > span.num").first()?.text()?.toInt() ?: -1
            val reqAttend = kat.select(".required-attendance > span").first()?.text()
            mAttendanceEntry.required = (reqAttend?.split("od")?.firstOrNull()?.trim() ?: "").toIntOrNull() ?: -1
            mAttendanceEntry.total = (reqAttend?.split("od")?.last()?.trim())?.toIntOrNull() ?: -1
            mAttendanceEntry.id = UUID.nameUUIDFromBytes(
                ("${mAttendanceEntry.attended}${mAttendanceEntry.absent}${mAttendanceEntry.subject}" +
                        "${mAttendanceEntry.type}${mAttendanceEntry.required}${mAttendanceEntry.total}${mAttendanceEntry.semester}").toByteArray()
            )
                .toString()
            attendanceForOneSemester.add(mAttendanceEntry)
        }

        return attendanceForOneSemester
    }

    fun parseAttendList(body: String): List<Pair<Element, Int>> {
        val doc = body.let { Jsoup.parse(it) }
        val attendanceUrls: MutableList<Pair<Element, Int>> = mutableListOf()
        try {
            attendanceUrls.addAll(
                doc.select("div.semster.winter div.body.clearfix a").map { element -> Pair(element, 1) })
            attendanceUrls.addAll(
                doc.select("div.semster.summer div.body.clearfix a").map { element -> Pair(element, 2) })
        } catch (ex: Exception) {
            ex.message?.let { Log.d("Parsing Attendance data failed.", it) }
            ex.printStackTrace()
        }
        return attendanceUrls
    }
}