package com.tstudioz.fax.fme.compose

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tstudioz.fax.fme.database.models.Event
import com.tstudioz.fax.fme.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.java.KoinJavaComponent
import org.koin.java.KoinJavaComponent.inject
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class, InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun HomeCompose(oneDay: Boolean = false) {

    val mainViewModel: MainViewModel by inject(MainViewModel::class.java)

    Column {
        if (mainViewModel.lessons.observeAsState().value?.isNotEmpty() == true) {
            if (!oneDay) {
                val mapped = mainViewModel.lessons.observeAsState().value!!.map { Event(
                    id = it.id,
                    name = it.name,
                    fullName = it.fullName,
                    shortName = it.shortName,
                    color = colorResource(id = it.colorId),
                    colorId = it.colorId,
                    teacher = it.teacher,
                    groups = it.groups,
                    classroom = it.classroom,
                    classroomShort = it.classroomShort,
                    start = it.start,
                    end = it.end,
                    week = it.week,
                    description = it.description,
                ) }
                Schedule(
                    events = mapped  ?: emptyList(),
                    minTime = LocalTime.of(8, 0),
                    maxTime = LocalTime.of(20, 0),
                )
            } else {
                Schedule(
                    events = mainViewModel.lessons.observeAsState().value?.filter {
                        it.start.toLocalDate() == LocalDate.now()
                    } ?: emptyList(),
                    minTime = LocalTime.of(8, 0),
                    maxTime = LocalTime.of(20, 0),
                )
            }
        }
    }

}