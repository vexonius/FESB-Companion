package com.tstudioz.fax.fme.database.models

import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime


data class Event(
    val id : String,
    val name: String,
    val fullName : String,
    val shortName: String,
    var color: Color,
    val colorId : Int,
    val teacher: String = "",
    val groups: String = "",
    val classroom: String = "",
    val classroomShort: String = "",
    val start: LocalDateTime,
    val end: LocalDateTime,
    val week: String = "",
    val description: String? = null,
)
