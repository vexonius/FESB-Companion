package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tstudioz.fax.fme.R

@Preview
@Composable
fun HomeTabCompose() {
    AppTheme {
        Scaffold {
            LazyColumn (Modifier.padding(it)) {
                item { WeatherCompose() }
                item { RemindersCompose() }
                item { TodayTimetableCompose() }
                item { CardsCompose() }
            }
        }

    }
}

@Composable
fun WeatherCompose() {
    Column(
        modifier = Modifier
            .background(colorResource(id = R.color.darker_cyan))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Split", fontSize = 16.sp, modifier = Modifier.padding(3.dp),
            )
        }
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable._42d),
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
                Text(text = "20°C", fontSize = 64.sp, fontWeight = FontWeight.Bold)
                Text(text = "Clear sky", fontSize = 13.sp)
                Row {
                    WeatherItem(text = "5.1 km/h", id = R.drawable.wind)
                    WeatherItem(text = "56.5%", id = R.drawable.vlaga)
                    WeatherItem(text = "0.0 mm", id = R.drawable.oborine)
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
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .background(colorResource(id = R.color.colorPrimaryDark))
                .padding(20.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_new),
                contentDescription = "Add reminder",
                modifier = Modifier.size(25.dp)
            )
            Text(text = "Dodaj podsjetnik", fontSize = 16.sp, modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun TodayTimetableCompose() {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "DANAŠNJA PREDAVANJA",
                fontSize = 13.sp,
                modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 0.dp)
            )
            Text(
                text = "14.6.2024 22:29:31",
                fontSize = 10.sp,
                modifier = Modifier.padding(0.dp, 10.dp, 20.dp, 0.dp)
            )
        }
        Column(
        ) {
            TimetableItem()
            TimetableItem()
        }
    }
}

@Composable
fun TimetableItem(
    title: String = "JEZICI I PREVODITELJI",
    time: String = "10:15 - 15:00",
    type: String = "Ispit",
    classroom: String = "B525"
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
            color = colorResource(id = R.color.purple_nice),
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
            )}
        CardCompose()
        CardCompose()
        CardCompose()
    }
}

@Composable
fun CardCompose() {
    Box(
        modifier = Modifier
            .padding(20.dp, 10.dp, 20.dp, 0.dp)
            .height(140.dp)
            .fillMaxWidth()
            .background(colorResource(id = R.color.colorPrimaryDark))
    ) {
        Column {
            Text(text = "Menza", fontSize = 35.sp, modifier = Modifier.padding(20.dp, 20.dp, 0.dp, 0.dp))
            Text(
                text = "Pregledaj dnevni jelovnik",
                fontSize = 13.sp,
                modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp)
            )
        }
    }
}