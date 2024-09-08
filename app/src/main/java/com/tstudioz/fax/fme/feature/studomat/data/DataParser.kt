package com.tstudioz.fax.fme.feature.studomat.data

import com.tstudioz.fax.fme.feature.studomat.dataclasses.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Student
import com.tstudioz.fax.fme.feature.studomat.dataclasses.Year
import org.jsoup.Jsoup

fun parseUpisaneGodine(body: String): List<Year> {
    val data = Jsoup.parse(body)
    val listOfYears = mutableListOf<Year>()
    data.select(".price-table__item").forEach { element ->
        listOfYears.add(
            Year(
                element.selectFirst(".price-table__title")?.text() ?: "",
                element.selectFirst("a[title=Prikaži podatke o upisu]")?.attr("href") ?: ""
            )
        )
    }
    return listOfYears
}

fun parseStudent(body: String): Student {
    val data = Jsoup.parse(body)
    return Student(
        name = data.selectFirst(".user__name")?.text() ?: "",
        surname = data.selectFirst(".user__name")?.text() ?: "",
        jmbag = data.selectFirst(".user__email")?.text() ?: "",
    )
}

fun parseTrenutnuGodinu(body: String): Triple<MutableList<StudomatSubject>, String, Pair<Int, Int>> {
    val data = Jsoup.parse(body)
    val listaPredmeta: MutableList<StudomatSubject> = mutableListOf()
    var polozeniKrozUpisani = Pair(0, 0)
    data.select(".responsive-table tbody tr").forEach { tr ->
        listaPredmeta.add(
            StudomatSubject(
                tr.selectFirst("td[data-title=Naziv predmeta:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Izborna grupa:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Semestar:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Predavanja:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Vježbe:]")?.text() ?: "",
                tr.selectFirst("td[data-title=ECTS upisano:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Polaže se:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Status:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Ocjena:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Datum ispitnog roka:]")?.text() ?: "",
                data.title().substringAfter("godinu ")
            )
        )
        val ocjena = tr.selectFirst("td[data-title=Ocjena:]")?.text() ?: ""
        var polozen = 0
        if (ocjena == "2" || ocjena == "3" || ocjena == "4" || ocjena == "5") {
            polozen = 1
        }
        polozeniKrozUpisani = Pair(
            first = polozeniKrozUpisani.first + polozen,
            second = polozeniKrozUpisani.second + 1
        )
    }
    return Triple(
        listaPredmeta,
        data.selectFirst(".prijavaVrijeme span:nth-child(2)")?.text() ?: "",
        polozeniKrozUpisani
    )
}