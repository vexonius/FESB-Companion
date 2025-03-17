package com.tstudioz.fax.fme.feature.home.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import java.time.Duration
import java.time.format.DateTimeFormatter

@Composable
fun TodayTimetableCompose(events: List<Event>) {
    Column(
        modifier = Modifier.padding(12.dp, 12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.todaysEvents),
            fontSize = 14.sp,
            modifier = Modifier.padding(12.dp, 0.dp),
        )

        if (events.isNotEmpty()) {
            events.sortedBy { it.start.toLocalTime() }.groupBy { it.start.toLocalDate() }.values.toList()[1] .forEach { event ->
                TimetableItem(event)
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.smiley),
                    contentDescription = "Smiley",
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(60.dp)
                        .aspectRatio(1f)
                )
                Text(
                    text = stringResource(id = R.string.getRest),
                    fontSize = 18.sp,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 10.dp),
                    color = colorResource(id = R.color.shady_blue)
                )
            }

        }
    }
}

@Composable
fun TimetableItem(event: Event) {

    val expanded = remember { mutableStateOf(false) }
    Column(
        Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { expanded.value = !expanded.value }
            .padding(12.dp, 5.dp)
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val time = Duration.between(event.start, event.end).toMinutes() / 60f * 6
            Canvas(modifier = Modifier.size(Dp(time * 5.dp.value) + 5.dp, 10.dp)) {
                val radius = 5.dp.toPx()
                val width = (radius * time - radius).coerceAtLeast(radius)
                drawLine(
                    color = event.color,
                    start = Offset(radius, radius),
                    end = Offset(width, radius),
                    strokeWidth = radius * 2,
                    cap = StrokeCap.Round,
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = event.eventType.value + " • " + event.classroom,
                fontSize = 12.sp,
            )
        }
        Row {
            if (!expanded.value) {
                Text(
                    text = event.start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = event.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if (expanded.value) {
            Row {
                Text(
                    text = stringResource(
                        R.string.time_range,
                        event.start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        event.end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    ),
                    fontSize = 12.sp
                )
                Text(
                    text = event.professor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}