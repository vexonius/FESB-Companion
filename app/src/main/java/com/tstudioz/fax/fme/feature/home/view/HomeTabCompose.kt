package com.tstudioz.fax.fme.feature.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.database.models.Note
import com.tstudioz.fax.fme.feature.home.models.WeatherDisplay
import com.tstudioz.fax.fme.feature.home.compose.CardsCompose
import com.tstudioz.fax.fme.feature.home.compose.NotesCompose
import com.tstudioz.fax.fme.feature.home.compose.TodayTimetableCompose
import com.tstudioz.fax.fme.feature.home.utils.getWeatherText
import com.tstudioz.fax.fme.feature.menza.view.MenzaCompose
import com.tstudioz.fax.fme.feature.menza.view.MenzaViewModel
import com.tstudioz.fax.fme.routing.HomeRouter
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import java.util.Locale

val sidePadding = 24.dp

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class)
@Composable
fun HomeTabCompose(
    homeViewModel: HomeViewModel = koinViewModel(),
    menzaViewModel: MenzaViewModel = koinViewModel(),
    router: HomeRouter = koinInject<HomeRouter>(),
) {

    val weather: LiveData<WeatherDisplay> = homeViewModel.weatherDisplay
    val notes: LiveData<List<Note>> = homeViewModel.notes
    val events: LiveData<List<Event>> = homeViewModel.events
    val insertNote: (note: Note) -> Unit = homeViewModel::insert
    val deleteNote: (note: Note) -> Unit = homeViewModel::delete

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                homeViewModel.fetchDailyTimetable()
            }

            else -> {}
        }
    }

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = homeViewModel.snackbarHostState) },
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0.dp)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxHeight()) {
                if (menzaViewModel.menzaOpened.observeAsState().value == true) {
                    MenzaCompose(menzaViewModel)
                    return@Scaffold
                }
                LazyColumn(
                    Modifier
                        .padding(paddingValues)
                ) {
                    item {
                        Row(
                            Modifier
                                .height(54.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.settings_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 10.dp, end = 10.dp)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        router.routeToSettings()
                                    }
                            )
                        }
                    }
                    item {
                        WeatherCompose(
                            weather.observeAsState().value,
                            homeViewModel.nameOfUser.observeAsState().value ?: ""
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
                            events.observeAsState().value?.filter { event -> event.start.toLocalDate() == LocalDate.now() }
                                ?: emptyList()
                        )
                    }
                    item { CardsCompose({ menzaViewModel.openMenza() }, homeViewModel) }
                }
            }
        }
    }
}

@Composable
fun WeatherCompose(
    weather: WeatherDisplay?,
    nameOfUser: String
) {
    Column(
        modifier = Modifier.padding(32.dp, 0.dp, 0.dp, 0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.hi_user, nameOfUser),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        if (weather != null) {
            Text(
                text = stringResource(
                    R.string.weather_info,
                    weather.location,
                    stringResource(getWeatherText(weather.summary.lowercase(Locale.getDefault()))),
                    weather.temperature
                ),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
fun WeatherPreview() {
    AppTheme {
        Surface{
            WeatherCompose(
                weather = WeatherDisplay(
                    location = "Split",
                    temperature = 20.0,
                    summary = "rain"
                ),
                nameOfUser = "Marko"
            )
        }
    }
}