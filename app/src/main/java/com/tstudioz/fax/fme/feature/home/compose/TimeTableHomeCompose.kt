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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.util.testEvents
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
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(12.dp, 0.dp),
        )

        if (events.isNotEmpty()) {
            events.forEach { event -> TimetableItem(event) }
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
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 10.dp),
                )
            }

        }
    }
}

@Composable
fun TimetableItem(event: Event) {

    val hourFormatter = DateTimeFormatter.ofPattern("HH:mm")

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
            val dividerColor = MaterialTheme.colorScheme.outline
            val dividerWidth = 1.dp
            val hours=  Duration.between(event.start, event.end).toMinutes() / 60f
            val time = hours * 6
            Canvas(modifier = Modifier.size(Dp(time * 5.dp.value) + 5.dp, 10.dp)) {
                val radius = 5.dp.toPx()
                val width = (radius * time - radius).coerceAtLeast(radius)
                val dividerFreq = width.div(hours)
                drawLine(
                    color = event.color,
                    start = Offset(radius, radius),
                    end = Offset(width, radius),
                    strokeWidth = radius * 2,
                    cap = StrokeCap.Round,
                )
                for (i in 1 until hours.toInt().plus(1)) {
                    drawLine(
                        color = dividerColor.copy(alpha = 0.5f),
                        start = Offset(dividerFreq * i, radius),
                        end = Offset(dividerFreq * i + dividerWidth.toPx(), radius),
                        strokeWidth = radius * 2,
                    )
                }
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
                    text = event.start.toLocalTime().format(hourFormatter),
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
                        event.start.toLocalTime().format(hourFormatter),
                        event.end.toLocalTime().format(hourFormatter)
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

@Preview
@Composable
fun TodayTimetablePreview() {
    AppTheme {
        Surface {
            TodayTimetableCompose(testEvents)
        }
    }
}