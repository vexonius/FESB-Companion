package com.tstudioz.fax.fme.feature.attendance.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {
        LazyColumn(Modifier.padding(it)) {
            items(items.size) { index ->
                val item = items[index]
                ListItem(
                    headlineContent = {
                        Text(
                            text = item.first().`class` ?: ""
                        )
                    },
                    supportingContent = {
                        Column {
                            (item).forEach { it ->
                                AttendanceItem(it)
                            }
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(4.dp))
            }
        }
    }
}