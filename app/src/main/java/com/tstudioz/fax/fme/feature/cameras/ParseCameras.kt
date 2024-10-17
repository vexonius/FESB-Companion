package com.tstudioz.fax.fme.feature.cameras

import org.jsoup.Jsoup

fun parseImageUrls(body: String): List<String> {
    val urls = mutableListOf<String>()
    val doc = Jsoup.parse(body)
    doc.select("a").map { it.attr("href") }.forEach { href ->
        if (!href.contains("medium") && !href.contains("small"))
            urls.add(href)
    }
    if (urls.size == 1 && urls.firstOrNull()?.contains("../") == true)
        return emptyList()
    return urls
}