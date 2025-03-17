package com.tstudioz.fax.fme.feature.home.view

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.compose.notesContainer
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.WeatherDisplay
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.view.MenzaCompose
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import com.tstudioz.fax.fme.util.testEvents
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

val sidePadding = 24.dp

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class)
@Composable
fun HomeTabCompose(
    homeViewModel: HomeViewModel = koinViewModel(),
    menzaViewModel: MenzaViewModel = koinViewModel()
) {

    val weather: LiveData<WeatherDisplay> = homeViewModel.weatherDisplay
    val notes: LiveData<List<Note>> = homeViewModel.notes
    val events: LiveData<List<Event>> = homeViewModel.events
    val menza: LiveData<Menza?> = menzaViewModel.menza
    val insertNote: (note: Note) -> Unit = homeViewModel::insert
    val deleteNote: (note: Note) -> Unit = homeViewModel::delete
    val menzaShow = remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                homeViewModel.fetchDailyTimetable()
                menzaViewModel.getMenza()
            }

            else -> {}
        }
    }

    AppTheme {
        BottomSheetScaffold(
            snackbarHost = { SnackbarHost(hostState = homeViewModel.snackbarHostState) },
            sheetPeekHeight = 0.dp,
            sheetContent = {
                if (menzaShow.value) {
                    MenzaCompose(menza, menzaShow)
                }
            }) { paddingValues ->
            Box(modifier = Modifier.fillMaxHeight()) {
                LazyColumn(
                    Modifier
                        .padding(paddingValues)
                ) {
                    item {
                        WeatherCompose(
                            weather.observeAsState().value ?: WeatherDisplay(
                                location = "",
                                temperature = 20.0,
                                humidity = 0.00,
                                wind = 0.00,
                                precipChance = 0.0,
                                icon = "_1d",
                                summary = ""
                            )
                        )
                    }
                    item {
                        NotesCompose(
                            notes = notes.observeAsState().value ?: emptyList(),
                            onClick = { openDialog.value = !openDialog.value },
                            insertNote,
                            deleteNote
                        )
                    }
                    item {
                        TodayTimetableCompose(
                            //testEvents
                            events.observeAsState().value?.filter { event -> event.start.toLocalDate() == LocalDate.now() }
                                ?: emptyList()
                        )
                    }
                    item { CardsCompose(menzaShow) }
                }
            }
        }
    }
}

@Composable
fun WeatherCompose(
    weather: WeatherDisplay
) {
    Column(
        modifier = Modifier.padding(32.dp, 54.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Bok Stipe",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "U " + weather.location + "u je trenutno " + weather.summary.lowercase(Locale.getDefault()) + " (" + weather.temperature + "°C)",
            fontSize = 11.sp,
        )
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
        modifier = Modifier
            .padding(24.dp, 12.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(notesContainer)
            .padding(24.dp, 12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.notes),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)
            )
        }
        val editMessage = remember { mutableStateOf("") }
        val message = remember { mutableStateOf("") }
        val openDialog = remember { mutableStateOf(false) }
        if (!openDialog.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable { openDialog.value = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.add_new),
                    contentDescription = stringResource(id = R.string.add_note),
                    modifier = Modifier.size(25.dp)
                )
                Text(
                    text = stringResource(id = R.string.add_note),
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(start = 10.dp)
                )
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
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
                                text = stringResource(id = R.string.enter_note),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(0.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.align(Alignment.End)) {
                        OutlinedButton(
                            onClick = { openDialog.value = false }
                        ) { Text(stringResource(id = R.string.cancel_note)) }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = {
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
                        ) { Text(stringResource(id = R.string.save_note)) }
                    }
                }

            }
        }
        notes.sortedByDescending { it.dateCreated }.forEach { note ->
            key(note.id) {
            NoteItem(
                note = note,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(
    note: Note,
    markDone: (isDone: Boolean) -> Unit = { },
    delete: () -> Unit = { }
) {
    val isDone = remember { mutableStateOf(note.checked ?:false) }
    val longClicked = remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(onLongClick = { longClicked.value = !longClicked.value }) {}
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        if (longClicked.value) {
            Image(
                painter = painterResource(id = R.drawable.trash_can_icon),
                contentDescription = stringResource(id = R.string.delete_note_desc),
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
                contentDescription = stringResource(id = R.string.checkmark_note_desc),
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
fun TodayTimetableCompose(events: List<Event>) {
    Column(
        modifier = Modifier.padding(24.dp, 12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.todaysEvents),
            fontSize = 13.sp,
        )

        if (events.isNotEmpty()) {
            events.forEach { event ->
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
    Column(
        Modifier.padding(0.dp, 5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val time = Duration.between(event.start, event.end).toMinutes() / 60f*6
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
                text = event.eventType.value,
                fontSize = 12.sp,
            )
            Text(
                text = " • " + event.classroom,
                fontSize = 12.sp,
            )
        }
        Row {
            Text(
                text = event.start.toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = event.name,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CardsCompose(menzaShow: MutableState<Boolean>) {
    Row(Modifier.padding(horizontal = sidePadding)) {
        val context = LocalContext.current
        Box(
            Modifier
                .weight(0.5f)
        ) {
            CardCompose(
                stringResource(id = R.string.menza_title),
                stringResource(id = R.string.menza_desc),
                colorResource(id = R.color.welcome2),
                colorResource(id = R.color.welcome2),
                onClick = {
                    menzaShow.value = true
                })
        }
        Box(
            Modifier
                .weight(0.5f)
        ) {
            CardCompose(
                stringResource(id = R.string.ugovori_title),
                stringResource(id = R.string.ugovori_desc),
                colorResource(id = R.color.green_blue),
                colorResource(id = R.color.lust),
                onClick = {
                    val appPackageName = "com.ugovori.studentskiugovori"
                    val intent = context.packageManager.getLaunchIntentForPackage(appPackageName)
                    if (intent != null) {
                        context.startActivity(intent)
                    } else {
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=$appPackageName")
                                )
                            )
                        } catch (ex: ActivityNotFoundException) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                )
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CardCompose(title: String, description: String, color1: Color, color2: Color, onClick: () -> Unit = { }) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 5.dp)
            .clip(RoundedCornerShape(10.dp))
            .height(200.dp)
            .angledGradientBackground(
                colors = listOf(color1, color2),
                degrees = 60f,
                true
            )
            .padding(15.dp)
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
        )
        Text(
            text = description,
            fontSize = 13.sp,
        )
    }
}

fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float, halfHalf: Boolean = false) =
    drawBehind {
        /*
        Have to compute length of gradient vector so that it lies within
        the visible rectangle.
        --------------------------------------------
        | length of gradient ^  /                  |
        |             --->  /  /                   |
        |                  /  / <- rotation angle  |
        |                 /  o --------------------|  y
        |                /  /                      |
        |               /  /                       |
        |              v  /                        |
        --------------------------------------------
                             x

                   diagonal angle = atan2(y, x)
                 (it's hard to draw the diagonal)

        Simply rotating the diagonal around the centre of the rectangle
        will lead to points outside the rectangle area. Further, just
        truncating the coordinate to be at the nearest edge of the
        rectangle to the rotated point will distort the angle.
        Let α be the desired gradient angle (in radians) and γ be the
        angle of the diagonal of the rectangle.
        The correct for the length of the gradient is given by:
        x/|cos(α)|  if -γ <= α <= γ,   or   π - γ <= α <= π + γ
        y/|sin(α)|  if  γ <= α <= π - γ, or π + γ <= α <= 2π - γ
        where γ ∈ (0, π/2) is the angle that the diagonal makes with
        the base of the rectangle.

        */

        var deg2 = degrees

        val (x, y) = size
        val gamma = atan2(y, x)

        if (halfHalf) {
            deg2 = atan2(x, y).times(180f / PI).toFloat()
        }

        if (gamma == 0f || gamma == (PI / 2).toFloat()) {
            // degenerate rectangle
            return@drawBehind
        }

        val degreesNormalised = (deg2 % 360).let { if (it < 0) it + 360 else it }

        val alpha = (degreesNormalised * PI / 180).toFloat()

        val gradientLength = when (alpha) {
            // ray from centre cuts the right edge of the rectangle
            in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
                x / cos(alpha)
            }
            // ray from centre cuts the top edge of the rectangle
            in gamma..(PI - gamma).toFloat() -> {
                y / sin(alpha)
            }
            // ray from centre cuts the left edge of the rectangle
            in (PI - gamma)..(PI + gamma) -> {
                x / -cos(alpha)
            }
            // ray from centre cuts the bottom edge of the rectangle
            in (PI + gamma)..(2 * PI - gamma) -> {
                y / -sin(alpha)
            }
            // default case (which shouldn't really happen)
            else -> hypot(x, y)
        }

        val centerOffsetX = cos(alpha) * gradientLength / 2
        val centerOffsetY = sin(alpha) * gradientLength / 2

        drawRect(
            brush = Brush.linearGradient(
                colors = colors,
                // negative here so that 0 degrees is left -> right and 90 degrees is top -> bottom
                start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
                end = Offset(center.x + centerOffsetX, center.y + centerOffsetY)
            ),
            size = size
        )
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
            Text(text = stringResource(id = R.string.enter_note))
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
                Text(stringResource(id = R.string.cancel_note))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    message.value = editMessage.value
                    openDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.save_note))
            }
        }
    }
}