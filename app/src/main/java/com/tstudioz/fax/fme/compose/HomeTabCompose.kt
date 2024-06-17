package com.tstudioz.fax.fme.compose

import android.view.LayoutInflater
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.database.models.TimetableType
import com.tstudioz.fax.fme.feature.weather.WeatherDisplay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Preview
@Composable
fun HomeTabCompose(
    weather: LiveData<WeatherDisplay> = liveData {
        WeatherDisplay(
            location = "Split",
            temperature = 20.0,
            humidity = 56.5,
            wind = 5.1,
            precipChance = 0.0,
            icon = "_42d",
            summary = "Clear sky"
        )
    },
    notes: LiveData<List<Note>> = liveData {
        listOf(
            Note(
                id = "1",
                noteTekst = "Ovo je bilješka",
                dateCreated = LocalDateTime.now(),
                checked = false
            )
        )
    },
    lastFetched: LiveData<String> = liveData { "22:29:31 14.6.2024" },
    events: LiveData<List<Event>> = liveData {
        listOf(
            Event(
                id = "1",
                name = "JEZICI I PREVODITELJI",
                shortName = "JIP",
                colorId = R.color.blue_nice,
                professor = "prof. dr. sc. Ivan Meštrović",
                eventType = TimetableType.KOLOKVIJ,
                groups = "1. grupa",
                classroom = "B525",
                start = LocalDateTime.now(),
                end = LocalDateTime.now().plusHours(3),
                description = "Predavanje iz kolegija Jezici i prevoditelji"
            )
        )
    },
    insertNote: (note: Note) -> Unit = { },
    deleteNote: (note: Note) -> Unit = { }
) {
    val openDialog = remember { mutableStateOf(false) }
    AppTheme {
        Scaffold(Modifier.background(colorResource(id = R.color.dark_cyan))) { paddingValues ->
            LazyColumn(
                Modifier
                    .padding(paddingValues)
                    .wrapContentHeight()
            ) {
                item {
                    WeatherCompose(
                        weather.observeAsState().value ?: WeatherDisplay(
                            location = "Split",
                            temperature = 20.0,
                            humidity = 56.5,
                            wind = 5.1,
                            precipChance = 0.0,
                            icon = "_42d",
                            summary = "Clear sky"
                        )
                    )
                }
                item {
                    NotesCompose(
                        notes = notes.observeAsState().value ?: emptyList(),
                        onClick = { openDialog.value = !openDialog.value },
                        insertNote, deleteNote
                    )
                }
                item {
                    TodayTimetableCompose(
                        lastFetched.observeAsState().value ?: "",
                        events.observeAsState().value?.filter { event -> event.start.toLocalDate() == LocalDate.now() }
                            ?: listOf()
                    )
                }
                item { CardsCompose() }
            }
        }
    }
}

@Composable
fun WeatherCompose(
    weather: WeatherDisplay
) {
    Column(
        modifier = Modifier.background(colorResource(id = R.color.dark_cyan))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = weather.location, fontSize = 16.sp, modifier = Modifier.padding(3.dp))
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
fun NotesCompose(
    notes: List<Note> = listOf(),
    onClick: () -> Unit,
    insertNote: (note: Note) -> Unit = { },
    deleteNote: (note: Note) -> Unit = { }
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Text(
                text = "PODSJETNICI",
                fontSize = 13.sp,
                modifier = Modifier.padding(20.dp, 5.dp, 0.dp, 0.dp),
                color = colorResource(id = R.color.shady_blue)
            )
        }
        Column(
            modifier = Modifier
                .background(colorResource(id = R.color.colorPrimaryDark))
                .fillMaxWidth()
        ) {
            val editMessage = remember { mutableStateOf("") }
            val message = remember { mutableStateOf("") }
            val openDialog = remember { mutableStateOf(false) }
            if (!openDialog.value) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, 10.dp, 20.dp, 10.dp)
                        .clickable { openDialog.value = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_new),
                        contentDescription = "Add reminder",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = "Dodaj podsjetnik",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(start = 10.dp)
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
                    .clickable { onClick() }) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = editMessage.value,
                            onValueChange = { editMessage.value = it },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = colorResource(id = R.color.colorPrimaryDark),
                                unfocusedContainerColor = colorResource(id = R.color.colorPrimaryDark),
                            ),
                            placeholder = {
                                Text(
                                    text = "Dodaj podsjetnik",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(0.dp)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.align(Alignment.End)) {
                            Button(
                                onClick = { openDialog.value = false }
                            ) { Text("Odustani") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                message.value = editMessage.value
                                openDialog.value = false
                                editMessage.value = ""
                                insertNote(
                                    Note(
                                        noteTekst = message.value,
                                        checked = false,
                                        dateCreated = LocalDateTime.now(),
                                        id = UUID.randomUUID().toString(),
                                    )
                                )
                            }
                            ) { Text("Spremi") }
                        }
                    }

                }
            }
            notes.forEach { note ->
                NoteItem(
                    note = note,
                    isDone = remember { mutableStateOf(note.checked ?: false) },
                    delete = { deleteNote(note) },
                    markDone = { isDone ->
                        insertNote(
                            Note(
                                noteTekst = note.noteTekst,
                                checked = isDone,
                                dateCreated = note.dateCreated,
                                id = note.id
                            )
                        )
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun NoteItemPreview() {
    NoteItem(
        note = Note(
            id = "1",
            noteTekst = "Ovo je bilješka",
            dateCreated = LocalDateTime.now(),
            checked = false
        ),
        isDone = mutableStateOf(false)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    isDone: MutableState<Boolean>,
    markDone: (isDone: Boolean) -> Unit = { },
    delete: () -> Unit = { }
) {
    val longClicked = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .combinedClickable(onLongClick = { longClicked.value = !longClicked.value }) {}
            .padding(20.dp, 0.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        if (longClicked.value) {
            Image(
                painter = painterResource(id = R.drawable.deletedelete),
                contentDescription = "delete",
                modifier = Modifier
                    .size(25.dp)
                    .noRippleClickable {
                        longClicked.value = !longClicked.value
                        delete()
                    }
            )
        } else {
            Image(
                painter = painterResource(id = if (isDone.value) R.drawable.circle_checked else R.drawable.circle_white),
                contentDescription = "checkmark",
                modifier = Modifier
                    .size(25.dp)
                    .noRippleClickable {
                        isDone.value = !isDone.value
                        markDone(isDone.value)
                    }
            )
        }
        Text(
            text = note.noteTekst ?: "",
            fontSize = 16.sp,
            modifier = Modifier.padding(10.dp),
            textDecoration = if (isDone.value) TextDecoration.LineThrough else TextDecoration.None
        )
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
    lastFetched: String = "22:29:31 14.6.2024",
    events: List<Event> = listOf(
        Event(
            id = "1",
            name = "JEZICI I PREVODITELJI",
            shortName = "JIP",
            colorId = R.color.blue_nice,
            professor = "prof. dr. sc. Ivan Meštrović",
            eventType = TimetableType.KOLOKVIJ,
            groups = "1. grupa",
            classroom = "B525",
            start = LocalDateTime.now(),
            end = LocalDateTime.now().plusHours(3),
            description = "Predavanje iz kolegija Jezici i prevoditelji"
        ),
        Event(
            id = "2",
            name = "PREVODITELJI",
            shortName = "JIP",
            colorId = R.color.purple_nice,
            professor = "prof. dr. sc. Ivan Meštrović",
            eventType = TimetableType.ISPIT,
            groups = "1. grupa",
            classroom = "B5",
            start = LocalDateTime.now(),
            end = LocalDateTime.now().plusHours(2),
            description = "Predavanje iz kolegija Jezici i prevoditelji"
        )
    )
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .padding(20.dp, 7.dp, 20.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "DANAŠNJA PREDAVANJA",
                fontSize = 13.sp,
                modifier = Modifier.weight(.6f, false),
                color = colorResource(id = R.color.shady_blue)
            )
            Text(
                text = lastFetched,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                modifier = Modifier.weight(.4f, false),
                color = colorResource(id = R.color.shady_blue)
            )
        }

        if (events.isNotEmpty()) {
            events.forEach() { event ->
                TimetableItem(event)
            }
        } else {
            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.colorPrimary))
                    .fillMaxWidth()
                    .padding(20.dp, 0.dp)
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.smiley),
                    contentDescription = "Smiley",
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .size(40.dp)
                        .aspectRatio(1f)
                )
                Text(
                    text = "Odmori se",
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 15.dp, bottom = 10.dp)
                        .fillMaxWidth(),
                    color = colorResource(id = R.color.shady_blue)
                )
            }

        }
    }
}

@Composable
fun TimetableItem(event: Event) {
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
            color = colorResource(id = event.colorId),
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
            Text(text = event.name, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val range = event.start.format(formatter) + " - " + event.end.format(formatter)
            Row {
                Text(text = "$range ∙ ${event.eventType.type} ∙ ${event.classroom}", fontSize = 13.sp)
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
                modifier = Modifier.padding(20.dp, 7.dp, 0.dp, 0.dp),
                color = colorResource(id = R.color.shady_blue)
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
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.particle_card, null, false)
            }
        )
        Column {
            Text(
                text = title,
                fontSize = 32.sp,
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

@Composable
fun CustomDialog(
    message: MutableState<String>,
    openDialog: MutableState<Boolean>,
    editMessage: MutableState<String>
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "Unesi Bilješku")
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = editMessage.value,
                onValueChange = { editMessage.value = it },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,

                    ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.align(Alignment.End)
        ) {
            Button(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Odustani")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    message.value = editMessage.value
                    openDialog.value = false
                }
            ) {
                Text("Spremi")
            }
        }
    }
}