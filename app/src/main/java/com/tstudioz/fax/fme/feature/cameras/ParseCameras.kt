package com.tstudioz.fax.fme.feature.cameras

import org.jsoup.Jsoup

fun parseImageUrls(body: String): List<String> {
    return Jsoup.parse(body).select("a")
        .map { it.attr("href") }
        .filter { !it.contains("medium") }
        .filter { !it.contains("small") }
        .filter { !it.contains("../") }
}