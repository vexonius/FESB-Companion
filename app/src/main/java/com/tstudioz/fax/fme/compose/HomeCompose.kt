package com.tstudioz.fax.fme.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun HomeCompose(oneDay: Boolean = false) {

    val mainViewModel: MainViewModel by inject(MainViewModel::class.java)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val event = mainViewModel.showDayEvent.observeAsState().value

    BottomSheetScaffold(
        sheetContent = {
            if (mainViewModel.showDay.observeAsState(initial = false).value) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { mainViewModel.hideDay() },
                    containerColor = event?.color ?: Color.Transparent,
                    windowInsets = WindowInsets(0.dp),
                    dragHandle = { },
                    shape = RectangleShape
                ) {
                    mainViewModel.showDayEvent.observeAsState().value?.let {
                        BottomInfoCompose(it)
                    }
                }
            }
            /*if (homeViewModel.showStudyChooseMenu.observeAsState().value == true) {
                val map = homeViewModel.classesMap.observeAsState().value?.toList()
                ModalBottomSheet(
                    sheetState = sheetStateStudy,
                    onDismissRequest = {
                        homeViewModel.showStudyChooseMenu.postValue(false)
                    }) {
                    LazyColumn(contentPadding = PaddingValues(8.dp)) {
                        items(map?.size ?: 0) {
                            map?.get(it)?.let { it1 ->
                                ListItem(
                                    headlineContent = { Text(it1.first) },
                                    modifier = Modifier.clickable {
                                        sharedPreferences.edit().putString("studij", it1.first)
                                            .apply()
                                        homeViewModel.getData(studij = it1.first, force = true)
                                        homeViewModel.showStudyChooseMenu.postValue(false)
                                    },
                                )
                                HorizontalDivider()
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }
            }
            if (homeViewModel.showWeekChooseMenu.observeAsState().value == true) {
                val map = homeViewModel.weeks.observeAsState().value?.toList()
                ModalBottomSheet(
                    sheetState = sheetStateWeek,
                    onDismissRequest = {
                        homeViewModel.showWeekChooseMenu.postValue(
                            false
                        )
                    }
                ) {
                    map?.forEach{ (t, u) ->
                        u["text"]?.jsonPrimitive?.content?.let {
                            ListItem(headlineContent = {
                                Text(
                                    text = it,
                                    Modifier
                                        .clickable {
                                            val week = u["datefrom"]?.jsonPrimitive?.content ?: ""
                                            val year = week.split("-")[0].toInt()
                                            val month = week.split("-")[1].toInt()
                                            val day = week.split("-")[2].toInt()
                                            homeViewModel.setSelectedWeek(
                                                LocalDate.of(year, month, day)
                                            )
                                            homeViewModel.getData(tjedan = t, force = true)
                                            homeViewModel.showWeekChooseMenu.postValue(false)
                                        },
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            })
                            HorizontalDivider()
                        }
                    }
                    Spacer(modifier = Modifier.height(60.dp))

                }
            }*/
        },
        sheetPeekHeight = 0.dp,
    ) {
        Column {
            if (mainViewModel.lessons.observeAsState().value?.isNotEmpty() == true) {
                val mapped = mainViewModel.lessons.observeAsState(emptyList()).value.onEach {
                    it.color = colorResource(id = it.colorId)
                }
                if (!oneDay) {

                    Schedule(
                        events = mapped,
                        minTime = LocalTime.of(8, 0),
                        maxTime = LocalTime.of(20, 0),
                    )
                } else {
                    Schedule(
                        events = mapped.filter {
                            it.start.toLocalDate() == LocalDate.now()
                        },
                        minTime = LocalTime.of(8, 0),
                        maxTime = LocalTime.of(20, 0),
                    )
                }
            }
        }

    }

}