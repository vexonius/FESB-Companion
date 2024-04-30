package com.tstudioz.fax.fme.compose

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.tstudioz.fax.fme.database.models.TimeTableInfo
import com.tstudioz.fax.fme.models.data.User
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun HomeCompose() {

    val mainViewModel: MainViewModel by inject(MainViewModel::class.java)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val event = mainViewModel.showDayEvent.observeAsState().value
    val shPref by inject(SharedPreferences::class.java)

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
            if (mainViewModel.showWeekChooseMenu.observeAsState(initial = false).value) {
                ModalBottomSheet(sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = WindowInsets(0.dp),
                    onDismissRequest = {
                        mainViewModel.showWeekChooseMenu(false)
                    })
                {
                    Column(Modifier.padding(8.dp, 8.dp, 8.dp, 20.dp)) {
                        val currentMonth = remember { YearMonth.now() }
                        val startMonth = remember { currentMonth.minusMonths(100) }
                        val endMonth = remember { currentMonth.plusMonths(100) }
                        val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
                        var selection by remember { mutableStateOf<CalendarDay?>(null) }
                        val state = rememberCalendarState(
                            startMonth = startMonth,
                            endMonth = endMonth,
                            firstVisibleMonth = currentMonth,
                            firstDayOfWeek = firstDayOfWeek
                        )
                        val coroutineScope = rememberCoroutineScope()
                        SimpleCalendarTitle(
                            modifier = Modifier.fillMaxWidth(),
                            currentMonth = state.firstVisibleMonth.yearMonth,
                            goToPrevious = {
                                coroutineScope.launch {
                                    state.scrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                                }
                            },
                            goToNext = {
                                coroutineScope.launch {
                                    state.scrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                                }
                            },
                        )
                        Spacer(modifier = Modifier.padding(0.dp, 5.dp))
                        HorizontalCalendar(state = state, dayContent = { day ->
                            Day(day, isSelected = selection == day, mainViewModel = mainViewModel) { clicked ->
                                selection = if (clicked == selection) {
                                    null
                                } else {
                                    clicked
                                }

                            }
                        })
                        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp)) {
                            Button(onClick = {
                                coroutineScope.launch{
                                    sheetState.hide()
                                    delay(300)
                                    mainViewModel.showWeekChooseMenu(false)
                                }
                            }) {
                                Text(text = "Odustani")
                            }
                            Button(onClick = {
                                val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")

                                if (selection != null) {
                                    val start = selection!!.date.dayOfWeek.value
                                    val startDate = selection!!.date.minusDays((start - 1).toLong())
                                    val endDate = selection!!.date.plusDays(7 - start.toLong())
                                    mainViewModel.fetchUserTimetable(
                                        User(shPref.getString("username", "") ?: "", ""),
                                        dateFormatter.format(startDate),
                                        dateFormatter.format(endDate)
                                    )
                                    coroutineScope.launch{
                                        sheetState.hide()
                                        delay(300)
                                        mainViewModel.showWeekChooseMenu(false)
                                    }
                                }
                            }) {
                                Text(text = "Odaberi")
                            }
                        }
                    }
                }
            }
        },
        sheetPeekHeight = 0.dp,
    ) {
        Column {
            if (mainViewModel.lessons.observeAsState().value?.isNotEmpty() == true) {
                val mapped = mainViewModel.lessons.observeAsState(emptyList()).value.onEach {
                    it.color = colorResource(id = it.colorId)
                }
                val subExists: Boolean = mapped.any { it.start.dayOfWeek.value == 6 }

                Schedule(
                    events = mapped,
                    minTime = LocalTime.of(8, 0),
                    maxTime = LocalTime.of(20, 0),
                    minDate = mainViewModel.shownWeek.value ?: LocalDate.now(),
                    maxDate = (mainViewModel.shownWeek.value ?: LocalDate.now()).plusDays(if (subExists) 5 else 4),
                )
            }
        }
    }
}


@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    mainViewModel: MainViewModel,
    onClick: (CalendarDay, ) -> Unit = {},
) {
    val selectedItemColor = MaterialTheme.colorScheme.secondary
    val inActiveTextColor = Color.Gray
    val textColor = when (day.position) {
        DayPosition.MonthDate -> Color.Unspecified
        DayPosition.InDate, DayPosition.OutDate -> inActiveTextColor
    }
    val colors = mutableListOf(MaterialTheme.colorScheme.background)
    val inPeriods = mutableListOf<TimeTableInfo>()


    mainViewModel.periods.value?.forEach {
        if ((it.StartDate?.compareTo(day.date) ?: 1) <= 0 && (it.EndDate?.compareTo(day.date) ?: -1) >= 0) {
            when (it.ColorCode) {
                "White" -> colors.add(Color.White)
                "Blue" -> colors.add(Color(0xff0060ff))
                "Yellow" -> colors.add(Color(0xffe5c700))
                "Orange" -> colors.add(Color(0xffff6600))
                "Purple" -> colors.add(Color(0xffa200ff))
                "Red" -> colors.add(Color(0xffff0000))
                "Green" -> colors.add(Color(0xff0b9700))
                else -> {}
            }
            inPeriods.add(it)
        }
    }

    val orderList = listOf(
        Color(0xffff6600).value,
        Color(0xff0060ff).value,
        Color(0xffe5c700).value,
        Color(0xffff0000).value,
        Color(0xffa200ff).value,
        Color(0xff0b9700).value,
        MaterialTheme.colorScheme.background.value,
        Color.White.value,
    )

    val color = colors.sortedBy {
        orderList.indexOf<ULong>(it.value)
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(0.dp, 2.dp)
            .border(
                width = if (isSelected) 1.dp else 1.dp,
                color = if (isSelected) selectedItemColor else color.first(),
            )
            .background(color = color.first())
            .clickable { onClick(day) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}