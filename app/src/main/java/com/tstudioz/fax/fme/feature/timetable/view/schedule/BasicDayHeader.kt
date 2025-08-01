package com.tstudioz.fax.fme.feature.timetable.view.schedule

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle


private val DayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d")

@Composable
fun BasicDayHeader(day: LocalDate) {
    val title = day.dayOfWeek.getDisplayName(TextStyle.SHORT, java.util.Locale.getDefault()).take(3).lowercase()
        .replaceFirstChar { it.uppercase() } + " " + day.format(DayFormatter)
    Text(
        text = title,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}