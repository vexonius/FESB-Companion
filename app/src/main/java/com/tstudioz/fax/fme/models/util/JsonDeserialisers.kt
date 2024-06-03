package com.tstudioz.fax.fme.models.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset


class LocalDateDeserializer : JsonDeserializer<LocalDate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate? {
        val dateString = json?.asString?.removePrefix("/Date(")?.removeSuffix(")/")?.toLong() ?: 0
        return dateString.div(1000).let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC).toLocalDate().plusDays(1)
        }
    }
}

class ColorDeserializer : JsonDeserializer<Long> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long {
        return when (json?.asString) {
            "White" -> 0xFF191C1D
            "Blue" -> 0xff0060ff
            "Yellow" -> 0xffe5c700
            "Orange" -> 0xffff6600
            "Purple" -> 0xffa200ff
            "Red" -> 0xffff0000
            "Green" -> 0xff0b9700
            else -> 0xFF191C1D
        }
    }
}
