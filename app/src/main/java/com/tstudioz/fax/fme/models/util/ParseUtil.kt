package com.tstudioz.fax.fme.models.util

import com.tstudioz.fax.fme.database.models.IksicaBalance
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
    val summarycode: String = unparsedsummary.substringBefore("_")

    val current = Current()
    current.humidity = currently.getDouble("relative_humidity")
    current.icon = currentlyNextOneHoursSummary.getString("symbol_code")
    current.precipChance = currentlyNextOneHoursDetails.getDouble("precipitation_amount")
    current.summary = summarycode
    current.wind = currently.getDouble("wind_speed")
    current.setTemperature(currently.getDouble("air_temperature"))
    return current
}


fun parseStudentInfo(body: String): Pair<IksicaBalance, StudentDataIksica> {
    val doc = Jsoup.parse(body)

    val image = doc.selectFirst(".slikastud")?.attr("src")
    val user = doc.selectFirst(".card-title")?.text()
    val number = doc.selectFirst("td:contains(Izdana)")?.parent()?.selectFirst("td")?.text()
    val oib = doc.selectFirst("span:contains(OIB:)")?.nextSibling()?.toString()?.trim()
    val jmbag = doc.selectFirst("span:contains(JMBAG:)")?.nextSibling()?.toString()?.trim()
    val university = doc.selectFirst("span:contains(Nadležna ustanova:)")?.nextSibling().toString()
    val rightsLevel = doc.selectFirst("p:contains(RAZINA PRAVA)")?.parent()?.selectFirst("u")?.text().toString()
    val rightsFrom = doc.selectFirst("span:contains(Prava od datuma:)")?.nextSibling().toString()
    val rightsTo = doc.selectFirst("span:contains(Prava do datuma:)")?.nextSibling().toString()
    val balance =
        doc.selectFirst("p:contains(RASPOLOŽIVI SALDO)")
            ?.parent()?.lastElementChild()?.text()
            ?.substringBefore(" €").toString().replace(",", ".")
    val spentToday =
        doc.selectFirst("p:contains(POTROŠENO DANAS)")?.parent()?.lastElementChild()?.text()
            ?.substringBefore(" €").toString().replace(",", ".")
    val dailySupport =
        doc.selectFirst("p:contains(DNEVNA POTPORA)")
            ?.parent()?.lastElementChild()?.text()
            ?.substringBefore(" €").toString().replace(",", ".")
    val iksicaBalance = IksicaBalance(
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
    return Pair(iksicaBalance, studentData)
}

fun parseRacuni(doc: String): List<Receipt> {
    val racuni = mutableListOf<Receipt>()
    val table = Jsoup.parse(doc).selectFirst("table")
    val rows = table?.select("tr")
    rows?.forEach { row ->
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
                    cols[6].selectFirst("a")?.attr("href") ?: ""
                )
            )
        }
    }
    return racuni
        .sortedByDescending { LocalTime.parse(it.vrijeme) }
        .sortedByDescending { it.datum }
}

fun parseDetaljeRacuna(doc: String): MutableList<ReceiptItem> {
    val detaljiRacuna = mutableListOf<ReceiptItem>()
    val table = Jsoup.parse(doc).selectFirst(".table-responsive")
    val rows = table?.selectFirst("tbody")?.select("tr")
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

