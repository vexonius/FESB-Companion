package com.tstudioz.fax.fme.feature.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.compose.CardsCompose
import com.tstudioz.fax.fme.feature.home.compose.NotesCompose
import com.tstudioz.fax.fme.feature.home.compose.TodayTimetableCompose
import com.tstudioz.fax.fme.feature.menza.models.Menza
import com.tstudioz.fax.fme.feature.menza.view.MenzaCompose
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import com.tstudioz.fax.fme.util.testEvents
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.util.Locale

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
                            ), homeViewModel.nameOfUser.observeAsState().value ?: ""
                        )
                    }
                    item {
                        NotesCompose(
                            notes = notes.observeAsState().value ?: emptyList(),
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
                    item { CardsCompose(menzaShow, homeViewModel) }
                }
            }
        }
    }
}

@Composable
fun WeatherCompose(
    weather: WeatherDisplay,
    nameOfUser: String
) {
    Column(
        modifier = Modifier.padding(32.dp, 54.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.hi_user, nameOfUser),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(
                R.string.weather_info,
                weather.location,
                weather.summary.lowercase(Locale.getDefault()),
                weather.temperature
            ),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}