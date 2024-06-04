package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.TimetableType
import java.time.LocalDateTime

@Preview
@Composable
fun BottomInfoCompose(
    event: Event = Event(
        name = "Event",
        start = LocalDateTime.now(),
        end = LocalDateTime.now(),
        eventType = TimetableType.OTHER,
        description = "Description",
        color = Color.Blue,
        shortName = "E",
        id = "id",
        groups = "Grupa 1, ",
        )
) {
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(0.dp, 0.dp, 0.dp, 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(event.color)
        ) {
            Text(text = event.name, fontSize = 24.sp, modifier = Modifier.padding(15.dp))
        }
        BottomElement(
            text = event.eventType.type,
            modifierText = Modifier.padding(10.dp, 10.dp, 0.dp, 5.dp),
            modifierRow = Modifier.padding(15.dp, 15.dp, 0.dp, 10.dp),
            icon = R.drawable.classroom
        )
        BottomElement(
            text = event.professor,
            modifierText = Modifier.padding(10.dp, 10.dp, 0.dp, 5.dp),
            modifierRow = Modifier.padding(start = 15.dp, bottom = 10.dp),
            icon = R.drawable.profesor
        )
        BottomElement(
            text = "${event.start.toLocalTime()} - ${event.end.toLocalTime()}",
            modifierText = Modifier.padding(10.dp, 10.dp, 0.dp, 5.dp),
            modifierRow = Modifier.padding(start = 15.dp, bottom = 10.dp),
            icon = R.drawable.time
        )
        BottomElement(
            text = event.groups.split(",")[0],
            modifierText = Modifier.padding(10.dp, 10.dp, 0.dp, 5.dp),
            modifierRow = Modifier.padding(start = 15.dp, bottom = 10.dp),
            icon = R.drawable.group
        )
        BottomElement(
            text = event.classroom,
            modifierText = Modifier.padding(10.dp, 10.dp, 0.dp, 5.dp),
            modifierRow = Modifier.padding(start = 15.dp, bottom = 10.dp),
            icon = R.drawable.mjesto_odrzavanja
        )
    }
}

@Composable
fun BottomElement(
    text: String,
    modifierText: Modifier = Modifier,
    modifierRow: Modifier = Modifier,
    color: Color = Color.Transparent,
    icon: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifierRow
    ) {
        Icon(
            painterResource(icon),
            contentDescription = "Localized description",
            modifier = Modifier.size(25.dp)
        )
        Text(text = text, modifier = modifierText)
    }

}