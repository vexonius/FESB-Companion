package com.tstudioz.fax.fme.feature.iksica

import com.tstudioz.fax.fme.feature.iksica.models.Receipt
import com.tstudioz.fax.fme.feature.iksica.models.ReceiptItem
import com.tstudioz.fax.fme.feature.iksica.models.StudentData
import io.realm.kotlin.ext.realmListOf
import org.jsoup.Jsoup
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun parseStudentInfo(body: String): StudentData {
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

    val studentData = StudentData(
        imageUrl = image,
        nameSurname = user ?: "",
        rightsLevel = rightsLevel,
        dailySupport = dailySupport.toDoubleOrNull() ?: 0.0,
        oib = oib ?: "",
        jmbag = jmbag ?: "",
        cardNumber = number ?: "",
        rightsFrom = rightsFrom ?: "",
        rightsTo = rightsTo ?: "",
        balance = balance.toDoubleOrNull() ?: 0.0,
        spentToday = spentToday.toDoubleOrNull() ?: 0.0,
        receipts = realmListOf()
    )
    return studentData
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
                    cols[0].text() ?: "",
                    LocalDate.parse(cols[1].text(), DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())) ?: LocalDate.MIN,
                    cols[1].text() ?: "",
                    cols[2].text() ?: "",
                    cols[3].text().toDoubleOrNull() ?: 0.0,
                    cols[4].text().toDoubleOrNull() ?: 0.0,
                    ((cols[3].text().toDoubleOrNull() ?: 0.0) - (cols[4].text().toDoubleOrNull() ?: 0.0)) ?: 0.0,
                    cols[5].text() ?: "",
                    cols[6].selectFirst("a")?.attr("href") ?: ""
                )
            )
        }
    }
    return racuni
        .sortedByDescending { LocalTime.parse(it.time, DateTimeFormatter.ofPattern("H:mm", Locale.getDefault())) ?: LocalTime.MIN}
        .sortedByDescending { it.date }
}

fun parseDetaljeRacuna(doc: String): MutableList<ReceiptItem> {
    val detaljiRacuna = mutableListOf<ReceiptItem>()
    val table = Jsoup.parse(doc).selectFirst(".table-responsive")
    val rows = table?.selectFirst("tbody")?.select("tr")
    rows?.forEach { row ->
        val cols = row.select("td")
        val item = ReceiptItem(
            cols[0].text() ?: "",
            cols[1].text().toIntOrNull() ?: 0,
            cols[2].text().replace(",", ".").toDoubleOrNull() ?: 0.0,
            cols[3].text().replace(",", ".").toDoubleOrNull() ?: 0.0,
            cols[4].text().replace(",", ".").toDoubleOrNull() ?: 0.0
        )
        if (!detaljiRacuna.any { it.articleName == item.articleName && it.price == item.price }) {
            detaljiRacuna.add(item)
        } else {
            val index = detaljiRacuna.indexOf(
                detaljiRacuna.first {
                    it.articleName == item.articleName && it.price == item.price
                })
            detaljiRacuna[index].amount = (detaljiRacuna[index].amount + item.amount)
        }
    }
    return detaljiRacuna
}

