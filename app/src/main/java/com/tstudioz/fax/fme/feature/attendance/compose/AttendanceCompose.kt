package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tstudioz.fax.fme.R
import com.tstudioz.fax.fme.compose.AppTheme
import com.tstudioz.fax.fme.database.models.AttendanceEntry
import com.tstudioz.fax.fme.feature.attendance.view.AttendanceViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun AttendanceCompose(attendanceViewModel: AttendanceViewModel) {

    val items = attendanceViewModel.attendanceList.observeAsState().value ?: emptyList()

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsState()
    val snackbarHostState = attendanceViewModel.snackbarHostState

    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                attendanceViewModel.fetchAttendance()
            }

            else -> {}
        }
    }

    AppTheme {
        if (items.isNotEmpty()) {
            CreateAttendanceListView(items, snackbarHostState)
        } else {
            EmptyView()
        }
    }
}

@Composable
fun EmptyView() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
fun CreateAttendanceListView(items: List<List<AttendanceEntry>>, snackbarHostState: SnackbarHostState) {
    var filteredItems by remember { mutableStateOf(items) }
    var selectedFirstSemester by remember { mutableStateOf(false) }
    var selectedSecondSemester by remember { mutableStateOf(false) }
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            Row {
                FilterButton(selected = selectedFirstSemester, text = "1. Semestar",
                    onClick = {
                        selectedFirstSemester = !selectedFirstSemester
                        selectedSecondSemester = false
                        filteredItems = if (selectedFirstSemester) {
                            items.filter { it.firstOrNull()?.semester == 1 }
                        } else {
                            items
                        }
                    })
                FilterButton(selected = selectedSecondSemester, text = "2. Semestar",
                    onClick = {
                        selectedSecondSemester = !selectedSecondSemester
                        selectedFirstSemester = false
                        filteredItems = if (selectedSecondSemester) {
                            items.filter { it.firstOrNull()?.semester == 2 }
                        } else {
                            items
                        }
                    })

            }

            filteredItems.forEach() { item ->
                AttendanceItem(item)
            }
        }
    }
}

@Composable
fun FilterButton(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Spacer(modifier = Modifier.padding(10.dp))
    TextButton(
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(
                color = colorResource(
                    id = if (selected)
                        R.color.brandeis_blue else R.color.raisin_black
                )
            ),
        onClick = onClick
    ) {
        Text(text, color = colorResource(id = R.color.white), lineHeight = 1.em)
    }
}