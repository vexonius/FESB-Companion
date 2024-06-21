package com.tstudioz.fax.fme.models.util

import com.tstudioz.fax.fme.database.models.IksicaSaldo
import com.tstudioz.fax.fme.database.models.Receipt
import com.tstudioz.fax.fme.database.models.ReceiptItem
import com.tstudioz.fax.fme.database.models.StudentDataIksica
import com.tstudioz.fax.fme.weather.Current
import org.json.JSONObject
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


fun parseWeatherDetails(data: String): Current {
    val forecastjson = JSONObject(data)

    val currently0 = forecastjson.getJSONObject("properties")
    val currentlyArray = currently0.getJSONArray("timeseries")
    val currently = currentlyArray.getJSONObject(0).getJSONObject("data")
        .getJSONObject("instant").getJSONObject("details")
    val currentlyNextOneHours = currentlyArray.getJSONObject(0).getJSONObject("data")
        .getJSONObject("next_1_hours")
    val currentlyNextOneHoursSummary = currentlyNextOneHours.getJSONObject("summary")
    val currentlyNextOneHoursDetails = currentlyNextOneHours.getJSONObject("details")
    val unparsedsummary = currentlyNextOneHoursSummary.getString("symbol_code")
    val summarycode: String? = if (unparsedsummary.contains("_")) {
        unparsedsummary.substring(0, unparsedsummary.indexOf('_'))
    } else {
        unparsedsummary
    }

    val current = Current()
    current.humidity = currently.getDouble("relative_humidity")
    current.icon = currentlyNextOneHoursSummary.getString("symbol_code")
    current.precipChance = currentlyNextOneHoursDetails.getDouble("precipitation_amount")
    current.summary = summarycode
    current.wind = currently.getDouble("wind_speed")
    current.setTemperature(currently.getDouble("air_temperature"))
    return current
}


fun parseStudentInfo(body: String): Pair<IksicaSaldo, StudentDataIksica> {
    val doc = Jsoup.parse(body)

    val image = doc.select(".slikastud").attr("src")
    val user = doc.select(".card-title").first()?.text()
    val number = doc.select("td:contains(Izdana)").first()?.parent()?.select("td")?.first()?.text()
    val oib = doc.select("span:contains(OIB:)").first()?.nextSibling()?.toString()?.trim()
    val jmbag = doc.select("span:contains(JMBAG:)").first()?.nextSibling()?.toString()?.trim()
    val university = doc.select("span:contains(Nadležna ustanova:)").first()?.nextSibling().toString()
    val rightsLevel = doc.select("p:contains(RAZINA PRAVA)").first()?.parent()?.select("u")?.text().toString()
    val rightsFrom = doc.select("span:contains(Prava od datuma:)").first()?.nextSibling().toString()
    val rightsTo = doc.select("span:contains(Prava do datuma:)").first()?.nextSibling().toString()
    val balance =
        doc.select("p:contains(RASPOLOŽIVI SALDO)")
            .first()?.parent()?.lastElementChild()?.text()
            ?.substringBefore(" €").toString().replace(",", ".")
    val spentToday =
        doc.select("p:contains(POTROŠENO DANAS)").first()?.parent()?.lastElementChild()?.text()
            ?.substringBefore(" €").toString().replace(",", ".")
    val dailySupport =
        doc.select("p:contains(DNEVNA POTPORA)")
            .first()?.parent()?.lastElementChild()?.text()
            ?.substringBefore(" €").toString().replace(",", ".")
    val iksicaSaldo = IksicaSaldo(
        balance.toDoubleOrNull() ?: 0.0,
        spentToday.toDoubleOrNull() ?: 0.0,
    )
    val studentData = StudentDataIksica(
        nameSurname = user ?: "",
        rightsLevel = rightsLevel,
        dailySupport = dailySupport.toDoubleOrNull() ?: 0.0,
        oib = oib ?: "",
        jmbag = jmbag ?: "",
        iksicaNumber = number ?: "",
        rightsFrom = rightsFrom ?: "",
        rightsTo = rightsTo ?: ""
    )
    return Pair(iksicaSaldo, studentData)
}

fun parseRacuni(doc: String): List<Receipt> {
    val racuni = mutableListOf<Receipt>()
    val table = Jsoup.parse(doc).select("table")
    val rows = table.select("tr")
    for (row in rows) {
        val cols = row.select("td")
        if (cols.size >= 6) {
            racuni.add(
                Receipt(
                    cols[0].text(),
                    LocalDate.parse(cols[1].text(), DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    cols[1].text(),
                    cols[2].text(),
                    cols[3].text().toDoubleOrNull() ?: 0.0,
                    cols[4].text().toDoubleOrNull() ?: 0.0,
                    cols[5].text(),
                    cols[6].select("a").attr("href")
                )
            )
        }
    }
    val test = racuni
        .sortedByDescending { LocalTime.parse(it.vrijeme) }
        .sortedByDescending { it.datum }
    return test
}

fun parseDetaljeRacuna(doc: String): MutableList<ReceiptItem> {
    val detaljiRacuna = mutableListOf<ReceiptItem>()
    val table = Jsoup.parse(doc).select(".table-responsive").first()
    val rows = table?.select("tbody")?.select("tr")
    rows?.forEach { row ->
        val cols = row.select("td")
        val item = ReceiptItem(
            cols[0].text(),
            cols[1].text().toIntOrNull() ?: 0,
            cols[2].text().replace(",", ".").toDoubleOrNull() ?: 0.0,
            cols[3].text().replace(",", ".").toDoubleOrNull() ?: 0.0,
            cols[4].text().replace(",", ".").toDoubleOrNull() ?: 0.0
        )
        if (!detaljiRacuna.any { it.nazivArtikla == item.nazivArtikla && it.cijenaJednogArtikla == item.cijenaJednogArtikla }) {
            detaljiRacuna.add(item)
        } else {
            val index = detaljiRacuna.indexOf(
                detaljiRacuna.first {
                    it.nazivArtikla == item.nazivArtikla && it.cijenaJednogArtikla == item.cijenaJednogArtikla
                })
            detaljiRacuna[index].kolicina = (detaljiRacuna[index].kolicina + item.kolicina)
        }
    }
    return detaljiRacuna
}

