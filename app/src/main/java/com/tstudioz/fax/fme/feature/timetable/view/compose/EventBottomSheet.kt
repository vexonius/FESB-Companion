package com.tstudioz.fax.fme.feature.timetable.view.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event

@Composable
fun EventBottomSheet(event: Event) {
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(20.dp, 5.dp, 15.dp, 20.dp)
            .fillMaxSize()
    ) {
        Text(
            text = event.name,
            fontSize = 28.sp,
            modifier = Modifier.padding(0.dp, 15.dp, 15.dp, 15.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(0.dp, 0.dp, 15.dp, 15.dp)
        ) {
            val radius = 8.dp
            Canvas(modifier = Modifier.size(radius * 2)) { drawCircle(color = event.color, radius = radius.toPx()) }
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = event.eventType.type, style = MaterialTheme.typography.titleMedium)
        }
        val modifier = Modifier.padding(bottom = 10.dp)
        Column(Modifier.fillMaxWidth()) {
            Row {
                RowItem(
                    title = stringResource(id = R.string.professor),
                    text = event.professor,
                    modifier = modifier.weight(9.5f),
                )
                RowItem(
                    title = stringResource(id = R.string.group),
                    text = event.groups.split(",").firstOrNull() ?: "",
                    modifier = modifier.weight(9.5f),
                )
            }
            Row {
                RowItem(
                    title = stringResource(id = R.string.time),
                    text = stringResource(
                        id = R.string.time_range,
                        event.start.toLocalTime(),
                        event.end.toLocalTime()
                    ),
                    modifier = modifier.weight(9.5f),
                )
                RowItem(
                    title = stringResource(id = R.string.classroom),
                    text = event.classroom,
                    modifier = modifier.weight(9.5f),
                )
            }
            Row {
                RowItem(
                    title = stringResource(id = R.string.recurring),
                    text = event.recurringUntil,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
fun RowItem(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }

}