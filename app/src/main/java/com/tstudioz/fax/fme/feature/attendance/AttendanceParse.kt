package com.tstudioz.fax.fme.feature.attendance

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.UUID

fun parseAttendance(
    element: Element,
    body: String,
    semester: Int
): List<Dolazak> {
    val attendanceForOneKolegij = mutableListOf<Dolazak>()
    Jsoup.parse(body).select(".courseCategories div.courseCategory").forEach { kat ->
        val mDolazak = Dolazak()
        mDolazak.semestar = semester
        mDolazak.predmet = element.select(".cellContent").first()?.text()
        mDolazak.vrsta = kat.getElementsByClass("name").first()?.text()
        mDolazak.attended = kat.select(".attended > span.num").first()?.text()?.toInt() ?: 99
        mDolazak.absent = kat.select(".absent > span.num").first()?.text()?.toInt() ?: 99
        mDolazak.required = kat.select(".required-attendance > span").first()?.text()
        mDolazak.total = (mDolazak.required?.split("od")?.last()?.trim() ?: "99").toInt()
        mDolazak.id = UUID.nameUUIDFromBytes(("${mDolazak.attended}${mDolazak.absent}${mDolazak.predmet}" +
                    "${mDolazak.vrsta}${mDolazak.required}${mDolazak.total}${mDolazak.semestar}").toByteArray())
                .toString()
        attendanceForOneKolegij.add(mDolazak)
    }

    return attendanceForOneKolegij
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
        ex.message?.let { Log.d("Exception pris", it) }
        ex.printStackTrace()
    }
    return attendanceUrls
}