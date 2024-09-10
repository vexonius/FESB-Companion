package com.tstudioz.fax.fme.feature.studomat.data

import com.tstudioz.fax.fme.feature.studomat.models.Student
import com.tstudioz.fax.fme.feature.studomat.models.StudomatSubject
import com.tstudioz.fax.fme.feature.studomat.models.Year
import org.jsoup.Jsoup

fun parseYears(body: String): List<Year> {
    val data = Jsoup.parse(body)
    val listOfYears = data.select(".price-table__item").map { element ->
        Year(
            element.selectFirst(".price-table__title")?.text() ?: "",
            element.selectFirst("a[title=Prikaži podatke o upisu]")?.attr("href") ?: ""
        )
    }
    return listOfYears
}

fun parseStudent(body: String): Student {
    val data = Jsoup.parse(body)
    return Student(
        name = data.selectFirst(".user__name")?.text()?.substringBefore(" ") ?: "",
        surname = data.selectFirst(".user__name")?.text()?.substringAfter(" ") ?: "",
        jmbag = data.selectFirst(".user__email")?.text()?.substringBefore(" ") ?: "",
    )
}

fun parseCurrentYear(body: String): Pair<List<StudomatSubject>, String> {
    val data = Jsoup.parse(body)
    val list: List<StudomatSubject> = data.select(".responsive-table tbody tr").map { tr ->
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
            data.title().substringAfter("godinu ") ?: "",
            (tr.selectFirst("td[data-title=Ocjena:]")?.text() ?: "") in listOf("2", "3", "4", "5")
        )
    }
    val generated = data.selectFirst(".prijavaVrijeme span:nth-child(2)")?.text() ?: ""
    return Pair(list, generated)
}