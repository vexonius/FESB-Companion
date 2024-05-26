package com.tstudioz.fax.fme.feature.attendance

import android.util.Log
import com.tstudioz.fax.fme.database.models.Dolazak
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.StringTokenizer
import java.util.UUID

fun parseAttendance(
    element: Element,
    body: String,
    semester: Int
): Pair<String?, MutableList<Dolazak>> {
    val document = Jsoup.parse(body)
    val content = document.getElementsByClass("courseCategories").first()
    val kategorije = content?.select("div.courseCategory")
    val attendanceForOneKolegij = mutableListOf<Dolazak>()
    if (kategorije != null) {
        for (kat in kategorije) {
            val mDolazak = Dolazak()
            mDolazak.semestar = semester
            mDolazak.predmet = element.select("div.cellContent").first()?.text()
            mDolazak.vrsta = kat.getElementsByClass("name").first()?.text()
            val attended = kat.select("div.attended > span.num").first()?.text()
            if (attended != null) {
                mDolazak.attended = attended.toInt()
            }
            val absent = kat.select("div.absent > span.num").first()?.text()
            if (absent != null) {
                mDolazak.absent = absent.toInt()
            }
            mDolazak.required =
                kat.select("div.required-attendance " + "> span").first()?.text()
            val string = kat.select("div" + ".required-attendance > " + "span").first()?.text()
            val st = StringTokenizer(string, " ")
            st.nextToken()
            st.nextToken()
            val max = st.nextToken()
            mDolazak.total = max.toInt()
            val str =
                "${mDolazak.attended}${mDolazak.absent}${mDolazak.predmet}${mDolazak.vrsta}${mDolazak.required}${mDolazak.total}${mDolazak.semestar}"
            val id = UUID.nameUUIDFromBytes(str.toByteArray())
            mDolazak.id = id.toString()
            attendanceForOneKolegij.add(mDolazak)
        }
    }

    return Pair(attendanceForOneKolegij.first().predmet, attendanceForOneKolegij)
}


fun parseAt(body: String): List<Pair<Element, Int>> {
    val doc = body.let { Jsoup.parse(it) }
    val attendanceUrls: MutableList<Pair<Element, Int>> = mutableListOf()
    try {
        attendanceUrls.addAll(
            doc.select("div.semster.winter div.body.clearfix a")
                .map { element -> Pair(element, 1) })
        attendanceUrls.addAll(
            doc.select("div.semster.summer div.body.clearfix a")
                .map { element -> Pair(element, 2) })
    } catch (ex: Exception) {
        ex.message?.let { Log.d("Exception pris", it) }
        ex.printStackTrace()
    }
    return attendanceUrls
}