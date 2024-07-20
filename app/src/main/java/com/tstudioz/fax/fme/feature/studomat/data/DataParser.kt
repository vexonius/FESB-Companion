package com.tstudioz.fax.fme.feature.studomat.data

import com.example.studomatisvu.model.dataclasses.Predmet
import com.example.studomatisvu.model.dataclasses.Student
import org.jsoup.Jsoup

/*fun parseExamData(data: Document): Exam {

    val segment = data.select(".price-table__info").select("li")

    val exam = Exam()
    exam.name = data.select(".price-table__title").select("span").first()?.text() ?: ""
    exam.date = data.select(".datumRok").text() ?: ""
    exam.registerUntilDate = segment[0].text() ?: ""
    exam.unregisterUntilDate = segment[1].text() ?: ""
    exam.type = segment[2].text() ?: ""
    exam.description = segment[3].text() ?: ""
    exam.totalAttendances = data.select("#naslov1").select("p").first()?.text() ?: ""
    exam.attendancesThisYear = data.select("#naslov2").select("p").first()?.text() ?: ""
    return exam
}*/

fun parseUpisaneGodine(body: String): List<Pair<String, String>> {
    val data = Jsoup.parse(body)
    val listOfYears = mutableListOf<Pair<String, String>>()
    data.select(".price-table__item").forEach { element ->
        listOfYears.add(
            Pair(
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

fun parseTrenutnuGodinu(body: String): Triple<MutableList<Predmet>, String, Pair<Int, Int>> {
    val data = Jsoup.parse(body)
    val listaPredmeta: MutableList<Predmet> = mutableListOf()
    var polozeniKrozUpisani = Pair(0, 0)
    data.select(".responsive-table tbody tr").forEach { tr ->
        listaPredmeta.add(
            Predmet(
                tr.selectFirst("td[data-title=Naziv predmeta:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Izborna grupa:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Semestar:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Predavanja:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Vježbe:]")?.text() ?: "",
                tr.selectFirst("td[data-title=ECTS upisano:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Polaže se:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Status:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Ocjena:]")?.text() ?: "",
                tr.selectFirst("td[data-title=Datum ispitnog roka:]")?.text() ?: ""
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