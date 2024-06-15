package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.feature.weather.WeatherDisplay
import java.util.Locale

@Preview
@Composable
fun HomeTabCompose() {
    AppTheme {
        Scaffold {
            LazyColumn(Modifier.padding(it)) {
                //item { WeatherCompose() }
                item { RemindersCompose() }
                item { TodayTimetableCompose() }
                item { CardsCompose() }
            }
        }

    }
}

@Composable
fun WeatherCompose(
    weather: WeatherDisplay = WeatherDisplay(
        location = "Split",
        temperature = 20.0,
        humidity = 56.5,
        wind = 5.1,
        precipChance = 0.0,
        icon = "_42d",
        summary = "Clear sky"
    )
) {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.darker_cyan))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = weather.location, fontSize = 16.sp, modifier = Modifier.padding(3.dp),
            )
        }
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val context = LocalContext.current
            Image(
                painter = painterResource(
                    id = context.resources.getIdentifier(
                        weather.icon,
                        "drawable",
                        context.packageName
                    )
                ),
                contentDescription = "Weather icon",
                modifier = Modifier
                    .weight(0.45f)
                    .aspectRatio(1f)
                    .padding(15.dp)
            )
            Column(
                modifier = Modifier.weight(0.55f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format(Locale.US, getString(context, R.string.weatherTemp), weather.temperature),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = weather.summary, fontSize = 13.sp)
                Row {
                    Box(Modifier.weight(.3f, false)) {
                        WeatherItem(
                            text = String.format(
                                Locale.US,
                                getString(context, R.string.weatherWind),
                                weather.wind
                            ), id = R.drawable.wind
                        )
                    }
                    Box(Modifier.weight(.3f, false)) {
                        WeatherItem(
                            text = String.format(
                                Locale.US,
                                getString(context, R.string.weatherHumidity),
                                weather.humidity
                            ), id = R.drawable.vlaga
                        )
                    }
                    Box(Modifier.weight(.3f, false)) {
                        WeatherItem(
                            text = String.format(
                                Locale.US,
                                getString(context, R.string.weatherPrecipChance),
                                weather.precipChance
                            ), id = R.drawable.oborine
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherItem(text: String, id: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(5.dp)
    ) {
        Icon(
            painter = painterResource(id = id),
            contentDescription = "Weather icon",
            modifier = Modifier.size(20.dp)
        )
        Text(text = text, fontSize = 12.sp)
    }
}

@Composable
fun RemindersCompose() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row { Text(text = "PODSJETNICI", fontSize = 13.sp, modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 0.dp)) }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.colorPrimaryDark))
                .padding(20.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.add_new),
                    contentDescription = "Add reminder",
                    modifier = Modifier.size(25.dp)
                )
                Text(text = "Dodaj podsjetnik", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
            }
            ReminderItem()
        }
    }
}

@Composable
fun ReminderItem(reminderText: String = "text remonders") {
    val clicked = remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Image(
            painter = painterResource(id = if (clicked.value) R.drawable.circle_checked else R.drawable.circle_white),
            contentDescription = "checkmark",
            modifier = Modifier
                .size(25.dp)
                .noRippleClickable {
                    clicked.value = !clicked.value
                }
        )
        Text(text = reminderText, fontSize = 16.sp, modifier = Modifier.padding(10.dp))
    }
}
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}


@Composable
fun TodayTimetableCompose(
    lastFetched: String = "22:29:31 14.6.2024"
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "DANAŠNJA PREDAVANJA",
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(20.dp, 10.dp, 0.dp, 0.dp)
                    .weight(.6f, false)
            )
            Text(
                text = lastFetched,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(0.dp, 10.dp, 20.dp, 0.dp)
                    .weight(.4f, false)
            )
        }
        Column {
            TimetableItem()
            TimetableItem(colorId = R.color.blue_nice)
        }
    }
}

@Composable
fun TimetableItem(
    title: String = "JEZICI I PREVODITELJI",
    time: String = "10:15 - 15:00",
    type: String = "Ispit",
    classroom: String = "B525",
    colorId: Int = R.color.purple_nice
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(20.dp, 0.dp)
            .background(colorResource(id = R.color.colorPrimaryDark))
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        VerticalDivider(
            color = colorResource(id = colorId),
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp),
            thickness = 20.dp
        )
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(10.dp, 5.dp)
        ) {
            Text(text = title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Row {
                Text(text = "$time ∙ $type ∙ $classroom", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun CardsCompose() {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "PREHRANA",
                fontSize = 13.sp,
                modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 0.dp)
            )
        }
        CardCompose("Menza", "Pregledaj dnevni jelovnik")
        CardCompose("Studentski Ugovori", "Pregledaj svoje ugovore")
    }
}

@Composable
fun CardCompose(title: String, description: String) {
    Box(
        modifier = Modifier
            .padding(20.dp, 0.dp, 20.dp, 10.dp)
            .height(140.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.colorPrimaryDark))
    ) {
        Column {
            Text(
                text = title,
                fontSize = 35.sp,
                modifier = Modifier.padding(20.dp, 20.dp, 0.dp, 0.dp),
                lineHeight = 37.sp
            )
            Text(
                text = description,
                fontSize = 13.sp,
                modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)
            )
        }
    }
}