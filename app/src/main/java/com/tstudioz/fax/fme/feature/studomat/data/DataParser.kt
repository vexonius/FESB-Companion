package com.example.studomatisvu.model.data

import com.example.studomatisvu.model.dataclasses.Exam
import com.example.studomatisvu.model.dataclasses.Predmet
import com.example.studomatisvu.model.dataclasses.Student
import org.jsoup.nodes.Document

fun parseExamData(data: Document): Exam {

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
}

fun parseUpisaneGodine(data: Document): List<Pair<String, String>> {
    val listOfYears = mutableListOf<Pair<String, String>>()
    val elements = data.select(".price-table__item")
    for (element in elements) {
        listOfYears.add(
            Pair(
                element.select(".price-table__title").text(),
                element.select("a[title=Prikaži podatke o upisu]").attr("href")
            )
        )
    }
    return listOfYears
}

fun parseStudent(data: Document): Student {
    return Student(
        name = data.select(".user__name").text(),//.split(" ")[0]
        surname = data.select(".user__name").text(),//.split(" ")[1]
        jmbag = data.select(".user__email")[0].text(),
    )
}

fun parseTrenutnuGodinu(data: Document): Triple<MutableList<Predmet>, String, Pair<Int, Int>> {
    val table = data.select(".responsive-table").select("tbody").select("tr")
    val listaPredmeta: MutableList<Predmet> = mutableListOf()
    var polozeniKrozUpisani = Pair(0, 0)
    for (tr in table) {
        listaPredmeta.add(
            Predmet(
                tr.select("td[data-title=Naziv predmeta:]").text(),
                tr.select("td[data-title=Izborna grupa:]").text(),
                tr.select("td[data-title=Semestar:]").text(),
                tr.select("td[data-title=Predavanja:]").text(),
                tr.select("td[data-title=Vježbe:]").text(),
                tr.select("td[data-title=ECTS upisano:]").text(),
                tr.select("td[data-title=Polaže se:]").text(),
                tr.select("td[data-title=Status:]").text(),
                tr.select("td[data-title=Ocjena:]").text(),
                tr.select("td[data-title=Datum ispitnog roka:]").text()
            )
        )
        val ocjena = tr.select("td[data-title=Ocjena:]").text()
        var polozen = 0
        if (ocjena == "2" || ocjena== "3" || ocjena == "4" || ocjena == "5"){
            polozen = 1
        }
        polozeniKrozUpisani = Pair(
            first = polozeniKrozUpisani.first + polozen,
            second = polozeniKrozUpisani.second + 1
        )
    }
    return Triple(listaPredmeta, data.select(".prijavaVrijeme")[0].select("span")[1].text(), polozeniKrozUpisani)
}